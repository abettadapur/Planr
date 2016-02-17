import facebook
from asq.initiators import query
from funtimes.repositories.yelpCategoryRepository import YelpCategoryRepository
from funtimes.rest import authenticate
from funtimes.models.user import User
from funtimes.repositories.itineraryRepository import ItineraryRepository
from funtimes.repositories.itemRepository import ItemRepository
from funtimes.repositories.userRepository import UserRepository
from funtimes.repositories.userAuthorizationRepository import UserAuthorizationRepository
from flask import request
from flask_restful import abort, Resource
from flask_restful.reqparse import RequestParser
from sqlalchemy.exc import InvalidRequestError


class HelloWorld(Resource):
    def get(self):
        return {"message": "Welcome to the FunTimes API"}


class AuthResource(Resource):
    def __init__(self):
        self.auth_parser = RequestParser()
        self.auth_parser.add_argument('token', type=str, required=True, help="No token to verify", location='json')
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
        user = graph.get_object("me")

        if 'email' not in user:
            on_invalid_auth(
                "The provided key does not have email permissions. Obtain a key with extended email permissions"
            )

        if user and user['id'] == uid:
            if not user_repository.user_exists(uid):
                new_user = User(uid, user['first_name'], user['last_name'], user['email'])
                user_repository.add_or_update(new_user)
                user_repository.save_changes()

            users = user_repository.get(facebook_id=uid)
            user = users[0] if users else None
            if user:
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


class ItineraryResource(Resource):
    def __init__(self):
        self.update_parser = RequestParser()
        self.update_parser.add_argument('name', type=str, required=True, location='json', help='No name provided')
        self.update_parser.add_argument('date', type=str, required=True, location='json', help='No date provided')
        self.update_parser.add_argument('start_time', type=str, required=True, location='json',
                                        help='No start_time provided')
        self.update_parser.add_argument('end_time', type=str, required=True, location='json',
                                        help='No end_time provided')
        self.update_parser.add_argument('city', type=str, required=True, location='json', help='No city provided')
        self.update_parser.add_argument('public', type=bool, required=True, location='json',
                                        help='No publicity provided')
        self.itinerary_repository = ItineraryRepository()
        super(ItineraryResource, self).__init__()

    # Get an itinerary by id
    @authenticate
    def get(self, id, **kwargs):
        user = kwargs['user']
        itinerary = self.itinerary_repository.find(id)

        if not itinerary:
            abort(404, message="No itinerary with that id exists")

        if itinerary.user.id != user.id:
            abort(404, message="No itinerary with that id exists")

        return itinerary

    # Update an itinerary by id
    @authenticate
    def put(self, id, **kwargs):
        user = kwargs['user']
        args = self.update_parser.parse_args()

        itinerary = self.itinerary_repository.find(id)

        if not itinerary:
            abort(404, message="No itinerary with that id exists")

        if itinerary.user.id != user.id:
            abort(404, message="No itinerary with that id exists")

        itinerary.update_from_dict(args)
        result = self.itinerary_repository.add_or_update(itinerary)

        if not result.success():
            on_error(error_message="Could not update itinerary", result=result)

        self.itinerary_repository.save_changes()
        return itinerary

    @authenticate
    def delete(self, id, **kwargs):
        user = kwargs['user']
        itinerary = self.itinerary_repository.find(id)

        if not itinerary:
            abort(404, message="No itinerary with that id exists")

        if itinerary.user.id != user.id:
            abort(404, message="No itinerary with that id exists")

        result = self.itinerary_repository.delete(id)
        if not result.success:
            on_error(error_message="Could not delete itinerary", result=result)

        self.itinerary_repository.save_changes()
        return {"message": "Deleted itinerary"}


class ItineraryListResource(Resource):
    def __init__(self):
        self.create_parser = RequestParser()
        self.create_parser.add_argument('name', type=str, required=True, location='json', help='No name provided')
        self.create_parser.add_argument('date', type=str, required=True, location='json', help='No date provided')
        self.create_parser.add_argument('start_time', type=str, required=True, location='json',
                                        help='No start_time provided')
        self.create_parser.add_argument('end_time', type=str, required=True, location='json',
                                        help='No end_time provided')
        self.create_parser.add_argument('city', type=str, required=True, location='json', help='No city provided')
        self.create_parser.add_argument('public', type=bool, required=True, location='json',
                                        help='No publicity provided')
        self.itinerary_repository = ItineraryRepository()

        super(ItineraryListResource, self).__init__()

    # List all itineraries
    @authenticate
    def get(self, **kwargs):
        try:
            user = kwargs['user']
            filter_args = request.args.to_dict()
            itineraries = self.itinerary_repository.get(user_id=user.id, **filter_args)
            return itineraries
        except InvalidRequestError as ireq:
            on_error(str(ireq))

    # Create a new itinerary
    @authenticate
    def post(self, **kwargs):
        user = kwargs['user']
        args = self.create_parser.parse_args()
        itinerary = self.itinerary_repository.create_from_dict(args, user)
        # TODO(abettadapur): Populate itinerary with yelp items
        result = self.itinerary_repository.add_or_update(itinerary)

        if not result.success:
            on_error(error_message="Could not create itinerary", result=result)

        self.itinerary_repository.save_changes()
        return itinerary


class ItinerarySearchResource(Resource):
    def __init__(self):
        self.search_parser = RequestParser()
        self.search_parser.add_argument('query', type=str, required=False)
        self.search_parser.add_argument('city', type=str, required=False)
        self.itinerary_repository = ItineraryRepository()
        super(ItinerarySearchResource, self).__init__()

    @authenticate
    def get(self, **kwargs):
        args = self.search_parser.parse_args()

        if 'query' not in args:
            args['query'] = ''

        if 'city' not in args:
            args['city'] = ''

        itineraries = self.itinerary_repository.search(args['query'], args['city'])
        return itineraries


class ItineraryShareResource(Resource):
    def __init__(self):
        self.share_parser = RequestParser()
        self.share_parser.add_argument('user_id', type=str, required=True)
        self.share_parser.add_argument('permission', type=str, required=True)
        self.itinerary_repository = ItineraryRepository()
        super(ItineraryShareResource, self).__init__()

    @authenticate
    def post(self, itinerary_id, **kwargs):
        args = self.share_parser.parse_args()
        user_id = args['user_id']
        permission = args['permission']

        itinerary = query(self.itinerary_repository.get(user_id=kwargs['user'].id, id=itinerary_id)).single_or_default(
            default=None)
        if not itinerary:
            abort(404, message="This itinerary does not exist")

        result = self.itinerary_repository.share(itinerary, user_id, permission)

        if not result.success():
            on_error(error_message="Could not share itinerary", result=result)

        self.itinerary_repository.save_changes()
        return itinerary


class ItemResource(Resource):
    def __init__(self):
        self.reqparse = RequestParser()
        self.reqparse.add_argument('yelp_id', type=str, required=True, location='json', help='Missing yelp_id')
        self.reqparse.add_argument('category', type=str, required=True, location='json', help='Missing category')
        self.reqparse.add_argument('name', type=str, required=True, location='json', help='Missing name')
        self.reqparse.add_argument('start_time', type=str, required=True, location='json', help='Missing start_time')
        self.reqparse.add_argument('end_time', type=str, required=True, location='json', help='Missing end_time')
        self.itinerary_repository = ItineraryRepository()
        self.item_repository = ItemRepository()
        self.category_repository = YelpCategoryRepository()
        super(ItemResource, self).__init__()

    @authenticate
    def get(self, itinerary_id, item_id, **kwargs):
        user = kwargs['user']
        itinerary = query(self.itinerary_repository.get(user_id=user.id, id=itinerary_id)).single_or_default(
            default=None)
        if not itinerary:
            abort(404, message="This itinerary does not exist")
        item = query(itinerary.items).where(lambda i: i.id == item_id).single_or_default(default=None)
        if not item:
            abort(404, message="This item does not exist")
        return item

    @authenticate
    def put(self, itinerary_id, item_id, **kwargs):
        user = kwargs['user']
        args = self.reqparse.parse_args()
        itinerary = query(self.itinerary_repository.get(user_id=user.id, id=itinerary_id)).single_or_default(
            default=None)
        if not itinerary:
            abort(404, message="This itinerary does not exist")
        item = query(itinerary.items).where(lambda i: i.id == item_id).single_or_default(default=None)
        if not item:
            abort(404, message="This item does not exist")

        item.update_from_dict(args)
        category = query(self.category_repository.get(name=args['category'])).first_or_default(default=None)
        if not category:
            on_error("No category of that name exists")

        result = self.item_repository.add_or_update(itinerary)

        if not result.success():
            on_error(error_message="Could not update itinerary", result=result)

        self.item_repository.save_changes()
        return item


class ItemListResource(Resource):
    def __init__(self):
        self.reqparse = RequestParser()
        self.reqparse.add_argument('yelp_id', type=str, required=True, location='json', help='Missing yelp_id')
        self.reqparse.add_argument('yelp_category', type=str, required=True, location='json', help='Missing category')
        self.reqparse.add_argument('name', type=str, required=True, location='json', help='Missing name')
        self.reqparse.add_argument('start_time', type=str, required=True, location='json', help='Missing start_time')
        self.reqparse.add_argument('end_time', type=str, required=True, location='json', help='Missing end_time')
        self.itinerary_repository = ItineraryRepository()
        self.item_repository = ItemRepository()
        self.yelp_category_repository = YelpCategoryRepository()
        super(ItemListResource, self).__init__()

    @authenticate
    def get(self, itinerary_id, **kwargs):
        user = kwargs['user']
        itinerary = query(self.itinerary_repository.get(user_id=user.id, id=itinerary_id)).single_or_default(
            default=None)
        if not itinerary:
            abort(404, message="This itinerary does not exist")
        return itinerary.items

    @authenticate
    def post(self, itinerary_id, **kwargs):
        user = kwargs['user']
        args = self.reqparse.parse_args()
        itinerary = query(self.itinerary_repository.get(user_id=user.id, id=itinerary_id)).single_or_default(
            default=None)
        if not itinerary:
            abort(404, message="This itinerary does not exist")

        # TODO(abettadapur): Validation
        item = self.item_repository.create_from_dict(args)
        category = query(self.yelp_category_repository.get(name=args['yelp_category'])).first_or_default(default=None)
        if not category:
            on_error("No yelp category of that name exists")

        itinerary.items.add(item)
        result = self.itinerary_repository.add_or_update(itinerary)

        if not result.success():
            on_error(error_message="Could not create item for itinerary", result=result)

        self.itinerary_repository.save_changes()

        return item


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
