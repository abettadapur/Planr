import facebook

from asq.initiators import query
import flask
from flask import request
from flask_restful import abort, Resource
from flask_restful.reqparse import RequestParser
from funtimes.maps.maps import get_polyline, get_city
from funtimes.models.entities.change_result import ChangeResult
from funtimes.models.entities.item import Item
from funtimes.models.entities.plan import Plan
from funtimes.models.entities.yelp_item import YelpItem
from funtimes.models.entities.rating import Rating
from funtimes.models.entities.user import User
from funtimes.repositories.itemRepository import ItemRepository
from funtimes.repositories.planRepository import PlanRepository
from funtimes.repositories.ratingRepository import RatingRepository
from funtimes.repositories.userAuthorizationRepository import UserAuthorizationRepository
from funtimes.repositories.userRepository import UserRepository
from funtimes.repositories.yelpCategoryRepository import YelpCategoryRepository
from funtimes.rest import authenticate
from funtimes.generation.generation import populate_sample_plan
from funtimes.integrations.yelp import yelpapi
from sqlalchemy.exc import InvalidRequestError


class HelloWorld(Resource):
    def get(self):
        return {"message": "Welcome to the FunTimes API"}


class AuthResource(Resource):
    def __init__(self):
        self.auth_parser = RequestParser()
        self.auth_parser.add_argument(
            'token', type=str, required=True, help="No token to verify", location='json')
        self.auth_parser.add_argument('user_id', type=str, required=True,
                                      help="An associated Facebook User ID is required", location='json')
        super(AuthResource, self).__init__()

    def post(self):
        user_repository = UserRepository()
        auth_repository = UserAuthorizationRepository()
        args = self.auth_parser.parse_args()
        token = args['token']
        uid = args['user_id']

        graph = facebook.GraphAPI(access_token=token)
        user = graph.get_object("me", fields="email,first_name,last_name")

        if 'email' not in user:
            on_invalid_auth(
                "The provided key does not have email permissions. Obtain a key with extended email permissions"
            )

        if user and user['id'] == uid:
            if not user_repository.user_exists(user['email']):
                new_user = User(uid, user['first_name'], user[
                    'last_name'], user['email'])
                user_repository.add_or_update(new_user)
                user_repository.save_changes()

            users = user_repository.get(email=user['email'])
            user = users[0] if users else None

            if user:
                user.facebook_id = uid
                user_repository.add_or_update(user)
                user_repository.save_changes()

                auth_repository.insert_authorization(token, user.id)
                auth_repository.save_changes()
            else:
                on_server_error("Unknown error adding user")

            return True
        else:
            on_invalid_auth()

    @authenticate
    def delete(self, **kwargs):
        user = kwargs['user']


class PlanResource(Resource):
    def __init__(self):
        self.update_parser = RequestParser()
        self.update_parser.add_argument(
            'name', type=str, required=False, location='json', help='No name provided')
        self.update_parser.add_argument('start_time', type=str, required=False, location='json',
                                        help='No start_time provided')
        self.update_parser.add_argument('end_time', type=str, required=False, location='json',
                                        help='No end_time provided')
        self.update_parser.add_argument('public', type=bool, required=False, location='json',
                                        help='No publicity provided')
        self.plan_repository = PlanRepository()
        super(PlanResource, self).__init__()

    # Get an plan by id
    @authenticate
    def get(self, id, **kwargs):
        user = kwargs['user']
        plan = self.plan_repository.find(id)

        if not plan:
            abort(404, message="No plan with that id exists")

        if plan.user.id != user.id and not query(plan.shared_users).contains(user, lambda lhs, rhs: lhs.id == rhs.id):
            abort(404, message="No plan with that id exists")

        if 'include_polyline' in request.args and request.args['include_polyline']:
            polyline = get_polyline(plan)
            plan_dict = plan.as_dict()
            plan_dict['polylines'] = polyline
            return plan_dict

        return plan

    # Update an plan by id
    @authenticate
    def put(self, id, **kwargs):
        user = kwargs['user']
        args = self.update_parser.parse_args()
        plan = self.plan_repository.find(id)

        if not plan:
            abort(404, message="No plan with that id exists")

        if plan.user.id != user.id:
            permission = self.plan_repository.get_shared_user_permission(
                id, user.id)
            if not permission:
                abort(404, message="No plan with that id exists")
            elif permission == 'READ':
                abort(403, message="This user does not have edit permissions")

        plan.update_from_dict(args)
        result = self.plan_repository.add_or_update(plan)

        if not result.success():
            on_error(error_message="Could not update plan", result=result)

        self.plan_repository.save_changes()
        return plan

    @authenticate
    def delete(self, id, **kwargs):
        user = kwargs['user']
        plan = self.plan_repository.find(id)

        if not plan:
            abort(404, message="No plan with that id exists")

        if plan.user.id != user.id:
            abort(404, message="No plan with that id exists")

        self.plan_repository.delete(id)
        self.plan_repository.save_changes()
        return {"message": "Deleted plan"}


class PlanListResource(Resource):
    def __init__(self):
        self.get_parser = RequestParser()
        self.get_parser.add_argument('shared', type=bool, required=False)

        self.create_parser = RequestParser()
        self.create_parser.add_argument(
            'name', type=str, required=True, location='json', help='No name provided')
        self.create_parser.add_argument('start_time', type=str, required=True, location='json',
                                        help='No start_time provided')
        self.create_parser.add_argument('end_time', type=str, required=True, location='json',
                                        help='No end_time provided')
        self.create_parser.add_argument(
            'starting_address', type=str, required=True, location='json', help='No starting address provided')
        self.create_parser.add_argument(
            'starting_coordinate', type=str, required=True, location='json', help='No starting coordinate provided')
        self.create_parser.add_argument('public', type=bool, required=True, location='json',
                                        help='No publicity provided')
        self.plan_repository = PlanRepository()
        self.item_repository = ItemRepository()

        super(PlanListResource, self).__init__()

    # List all plans
    @authenticate
    def get(self, **kwargs):
        try:
            user = kwargs['user']
            filter_args = request.args.to_dict()
            if 'shared' in filter_args:
                filter_args['shared'] = bool(filter_args['shared'])
            plans = self.plan_repository.get(user=user, **filter_args)
            return plans
        except InvalidRequestError as ireq:
            on_error(str(ireq))

    # Create a new plan
    @authenticate
    def post(self, **kwargs):
        user = kwargs['user']
        json = request.json

        if 'items' not in json or len(json['items']) == 0:
            on_error(error_message="No items were provided")

        plan = Plan.from_json(json, user)
        items = self.item_repository.from_list(json['items'])

        if plan.starting_coordinate is not None:
            plan.city = get_city(plan.starting_coordinate.latitude, 
                                 plan.starting_coordinate.longitude)

        if plan.starting_address is None:
            plan.set_start_item(items[0])

        result = self.plan_repository.add_or_update(plan)

        if not result.success():
            on_error(error_message="Could not create plan", result=result)

        for item in items:
            result.add_child_result(plan.add_item(item))

        if not result.success():
            on_error(error_message="Could not add items to plan", result=result)

        self.plan_repository.save_changes()
        return plan


class PlanGenerateResource(Resource):
    def __init__(self):
        self.plan_repository = PlanRepository()
        self.category_repository = YelpCategoryRepository()

    @authenticate
    def post(self, **kwargs):
        user = kwargs['user']
        json = request.json

        if 'categories' not in json or len(json['categories']) == 0:
            on_error(error_message="Categories were not provided")

        if 'plan' not in json:
            on_error(error_message="Plan details were not provided")

        categories = self.category_repository.get_from_list(json['categories'])
        plan = json['plan']

        plan = Plan.from_json(plan, user)
        self.plan_repository.expunge(plan)
        result, failed_categories = populate_sample_plan(plan, categories)

        if not result:
            return failed_categories, 400

        polyline = get_polyline(plan)
        plan_dict = plan.as_dict()
        plan_dict['polylines'] = polyline
        return plan_dict


class PlanRandomizeResource(Resource):
    def __init__(self):
        self.plan_repository = PlanRepository()

    @authenticate
    def post(self, id, **kwargs):
        user = kwargs['user']
        plan = query(self.plan_repository.get(user=user, id=id)
                     ).single_or_default(default=None)

        if plan is None:
            abort(404, message="No plan with that id was found")

        self.plan_repository.clear_plan(plan)
        populate_sample_plan(plan)
        result = self.plan_repository.add_or_update(plan)

        if not result.success:
            on_error(error_message="Could not randomize plan", result=result)

        self.plan_repository.save_changes()
        polyline = get_polyline(plan)
        plan_dict = plan.as_dict()
        plan_dict['polylines'] = polyline
        return plan_dict


class PlanSearchResource(Resource):
    def __init__(self):
        self.search_parser = RequestParser()
        self.search_parser.add_argument('query', type=str, required=False)
        self.search_parser.add_argument('city', type=str, required=False)
        self.plan_repository = PlanRepository()
        super(PlanSearchResource, self).__init__()

    @authenticate
    def get(self, **kwargs):
        args = self.search_parser.parse_args()

        if 'query' not in args:
            args['query'] = ''

        if 'city' not in args:
            args['city'] = ''

        plans = self.plan_repository.search(args['query'], args['city'])
        return plans


class PlanShareResource(Resource):
    def __init__(self):
        self.plan_repository = PlanRepository()
        super(PlanShareResource, self).__init__()

    @authenticate
    def post(self, plan_id, **kwargs):
        plan = query(self.plan_repository.get(user_id=kwargs['user'].id, id=plan_id)).single_or_default(
            default=None)
        if not plan:
            abort(404, message="This plan does not exist")

        post_body = request.json
        if type(post_body) is not list:
            on_error(error_message="Invalid post body")

        result = ChangeResult()
        try:
            shares = query(post_body)
            for shared_user in plan.shared_users:
                if not shares.contains(shared_user, lambda lhs, rhs: rhs['user_id'] == lhs.id):
                    result.add_child_result(
                        self.plan_repository.unshare(plan, shared_user.id))

            for share in post_body:
                user_id = share['user_id']
                permission = share['permission']
                result.add_child_result(
                    self.plan_repository.share(plan, user_id, permission))

        except KeyError as ke:
            on_error(error_message="Invalid post body")

        if not result.success():
            on_error(error_message="Could not share plan", result=result)

        self.plan_repository.save_changes()
        return plan

class ItemSearchResource(Resource):
    """A proxy to the Yelp API"""
    def __init__(self):
        self.parser = RequestParser()
        self.parser.add_argument('latitude', type=float, location='args',
                                             required=True, help='No latitude')
        self.parser.add_argument('longtitude', type=float, location='args',
                                             required=True, help='No longtitude')
        self.parser.add_argument('term', location='args')
        self.parser.add_argument('categories', location='args', default='')
        super(ItemSearchResource, self).__init__()
    
    @authenticate
    def get(self, **kwargs):
        args = self.parser.parse_args()
        search_results = yelpapi.search(args.latitude, args.longtitude, 
                              args.term,args.categories.split(','),**kwargs)
        yelp_items = [YelpItem.create_from_dict(result) for result in search_results]
        return yelp_items

class ItemResource(Resource):
    def __init__(self):
        self.plan_repository = PlanRepository()
        self.item_repository = ItemRepository()
        self.category_repository = YelpCategoryRepository()
        super(ItemResource, self).__init__()

    @authenticate
    def get(self, plan_id, item_id, **kwargs):
        user = kwargs['user']
        plan = query(self.plan_repository.get(user_id=user.id, id=plan_id)).single_or_default(
            default=None)
        if not plan:
            abort(404, message="This plan does not exist")
        item = query(plan.items).where(lambda i: i.id ==
                                                 item_id).single_or_default(default=None)
        if not item:
            abort(404, message="This item does not exist")
        return item

    @authenticate
    def put(self, plan_id, item_id, **kwargs):
        user = kwargs['user']
        json = request.json
        new_item = Item.from_json(json)

        if new_item.id != item_id:
            on_error(error_message="Could not update item, ids do not match")

        plan = query(self.plan_repository.get(user_id=user.id, id=plan_id)).single_or_default(
            default=None)
        if not plan:
            abort(404, message="This plan does not exist")

        old_item = query(plan.items).where(lambda i: i.id ==
                                                 item_id).single_or_default(default=None)
        if not old_item:
            abort(404, message="This item does not exist")

        result = self.item_repository.add_or_update(new_item)

        if not result.success():
            on_error(error_message="Could not update item", result=result)

        self.item_repository.save_changes()
        return new_item

    @authenticate
    def delete(self, plan_id, item_id, **kwargs):
        user = kwargs['user']
        plan = query(self.plan_repository.get(user_id=user.id, id=plan_id)).single_or_default(
            default=None)
        if not plan:
            abort(404, message="This plan does not exist")

        item = query(plan.items).where(lambda i: i.id ==
                                                 item_id).single_or_default(default=None)
        if not item:
            abort(404, message="This item does not exist")

        plan.remove_item(item)
        self.item_repository.delete(id)
        self.item_repository.save_changes()
        return {"message": "Deleted item"}


class ItemListResource(Resource):
    def __init__(self):
        self.plan_repository = PlanRepository()
        self.item_repository = ItemRepository()
        self.yelp_category_repository = YelpCategoryRepository()
        super(ItemListResource, self).__init__()

    @authenticate
    def get(self, plan_id, **kwargs):
        user = kwargs['user']
        plan = query(self.plan_repository.get(user_id=user.id, id=plan_id)).single_or_default(
            default=None)
        if not plan:
            abort(404, message="This plan does not exist")
        return plan.items

    @authenticate
    def post(self, plan_id, **kwargs):
        user = kwargs['user']
        json = request.json
        item = Item.from_json(json)
        plan = query(self.plan_repository.get(user_id=user.id, id=plan_id)).single_or_default(
            default=None)
        if not plan:
            abort(404, message="This plan does not exist")

        result = plan.add_item(item)

        if not result.success():
            on_error(error_message="Could not create item for plan", result=result)

        result = self.plan_repository.add_or_update(plan)
        if not result.success():
            on_error(error_message="Could not create item for plan", result=result)

        self.plan_repository.save_changes()

        return item


class RatingResource(Resource):
    def __init__(self):
        self.create_parser = RequestParser()
        self.create_parser.add_argument(
            'title', type=str, required=True, location='json', help='Missing title')
        self.create_parser.add_argument(
            'rating', type=int, required=True, location='json', help='Missing rating')
        self.create_parser.add_argument(
            'content', type=str, required=True, location='json', help='Missing content')
        self.rating_repository = RatingRepository()
        self.plan_repository = PlanRepository()
        super(RatingResource, self).__init__()

    @authenticate
    def post(self, plan_id, **kwargs):
        user = kwargs['user']
        args = self.create_parser.parse_args()
        plan = self.plan_repository.find(plan_id)

        if not plan.public:
            abort(404, message="No plan with this id was found")

        rating = Rating(
            title=args['title'],
            rating=args['rating'],
            content=args['content'],
            user=user,
            plan=plan
        )

        result = self.rating_repository.add_or_update(rating)
        if not result.success():
            on_error(error_message="Could not save rating", result=result)

        self.rating_repository.save_changes()
        return rating

    @authenticate
    def get(self, plan_id, **kwargs):
        ratings = self.rating_repository.get(plan_id=plan_id)
        return ratings


class FriendsResource(Resource):
    def __init__(self):
        self.user_repository = UserRepository()
        super(FriendsResource, self).__init__()

    @authenticate
    def get(self, **kwargs):
        graph = facebook.GraphAPI(access_token=kwargs['token'])
        friends = graph.get_connections(
            kwargs['user'].facebook_id, "friends")['data']
        users = []
        for friend in friends:
            user = query(self.user_repository.get(
                facebook_id=friend['id'])).single_or_default(default=None)
            if user:
                users.append(user)

        return users


class CategoryResource(Resource):
    def __init__(self):
        self.category_repository = YelpCategoryRepository()
        super(CategoryResource, self).__init__()

    @authenticate
    def get(self, **kwargs):
        categories = self.category_repository.get()
        return categories


def on_error(error_message, result=None):
    if result:
        error_message += ": " + ", ".join(result.errors)
    abort(400, message=error_message)


def on_server_error(error_message):
    abort(500, message=error_message)


def on_invalid_auth(message=None):
    if message:
        abort(401, message=message)
    else:
        abort(401, message="unauthorized")
