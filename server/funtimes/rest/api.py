import facebook

from asq.initiators import query
from flask import request
from flask_restful import abort, Resource
from flask_restful.reqparse import RequestParser
from funtimes.maps.maps import get_polyline
from funtimes.models.entities.change_result import ChangeResult
from funtimes.models.entities.rating import Rating
from funtimes.models.entities.user import User
from funtimes.repositories.itemRepository import ItemRepository
from funtimes.repositories.itineraryRepository import ItineraryRepository
from funtimes.repositories.ratingRepository import RatingRepository
from funtimes.repositories.userAuthorizationRepository import UserAuthorizationRepository
from funtimes.repositories.userRepository import UserRepository
from funtimes.repositories.yelpCategoryRepository import YelpCategoryRepository
from funtimes.repositories.cityRepository import CityRepository
from funtimes.rest import authenticate
from funtimes.generation.generation import populate_sample_itinerary
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
        user = graph.get_object("me", fields="email,first_name,last_name")

        if 'email' not in user:
            on_invalid_auth(
                "The provided key does not have email permissions. Obtain a key with extended email permissions"
            )

        if user and user['id'] == uid:
            if not user_repository.user_exists(user['email']):
                new_user = User(uid, user['first_name'], user['last_name'], user['email'])
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


class CityResource(Resource):
    def __init__(self):
        self.query_parser = RequestParser()
        self.query_parser.add_argument('name', required=False, help='Name of city')
        self.query_parser.add_argument('state', required=False, help='Name of state')
        self.city_repository = CityRepository()
        super(CityResource, self).__init__()

    def get(self, **kwargs):
        args = self.query_parser.parse_args()
        cities = self.city_repository.search(args.name, args.state)
        return cities


class ItineraryResource(Resource):
    def __init__(self):
        self.update_parser = RequestParser()
        self.update_parser.add_argument('name', type=str, required=True, location='json', help='No name provided')
        self.update_parser.add_argument('start_time', type=str, required=True, location='json',
                                        help='No start_time provided')
        self.update_parser.add_argument('end_time', type=str, required=True, location='json',
                                        help='No end_time provided')
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

        if itinerary.user.id != user.id and not query(itinerary.shared_users).contains(user, lambda lhs, rhs: lhs.id == rhs.id):
            abort(404, message="No itinerary with that id exists")

        if 'include_polyline' in request.args and request.args['include_polyline']:
            polyline = get_polyline(itinerary)
            itinerary_dict = itinerary.as_dict()
            itinerary_dict['polylines'] = polyline
            return itinerary_dict

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
            permission = self.itinerary_repository.get_shared_user_permission(id, user.id)
            if not permission:
                abort(404, message="No itinerary with that id exists")
            elif permission == 'READ':
                abort(403, message="This user does not have edit permissions")

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

        self.itinerary_repository.delete(id)
        self.itinerary_repository.save_changes()
        return {"message": "Deleted itinerary"}


class ItineraryListResource(Resource):
    def __init__(self):
        self.get_parser = RequestParser()
        self.get_parser.add_argument('shared', type=bool, required=False)

        self.create_parser = RequestParser()
        self.create_parser.add_argument('name', type=str, required=True, location='json', help='No name provided')
        self.create_parser.add_argument('start_time', type=str, required=True, location='json',
                                        help='No start_time provided')
        self.create_parser.add_argument('end_time', type=str, required=True, location='json',
                                        help='No end_time provided')
        self.create_parser.add_argument('city', type=str, required=True, location='json', help='No city provided')
        self.create_parser.add_argument('starting_address', type=str, required=True, location='json', help='No starting address provided')
        self.create_parser.add_argument('starting_coordinate', type=str, required=True, location='json', help='No starting coordinate provided')
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
            if 'shared' in filter_args:
                filter_args['shared'] = bool(filter_args['shared'])
            itineraries = self.itinerary_repository.get(user=user, **filter_args)
            return itineraries
        except InvalidRequestError as ireq:
            on_error(str(ireq))

    # Create a new itinerary
    @authenticate
    def post(self, **kwargs):
        user = kwargs['user']
        args = self.create_parser.parse_args()
        itinerary = self.itinerary_repository.create_from_dict(args, user)
        populate_sample_itinerary(itinerary)
        # TODO(abettadapur): Populate itinerary with yelp items
        result = self.itinerary_repository.add_or_update(itinerary)

        if not result.success:
            on_error(error_message="Could not create itinerary", result=result)

        self.itinerary_repository.save_changes()
        return itinerary


class ItineraryRandomizeResource(Resource):
    def __init__(self):
        self.itinerary_repository = ItineraryRepository()

    @authenticate
    def post(self, id, **kwargs):
        user = kwargs['user']
        itinerary = query(self.itinerary_repository.get(user=user, id=id)).single_or_default(default=None)

        if itinerary is None:
            abort(404, message="No itinerary with that id was found")

        self.itinerary_repository.clear_itinerary(itinerary)
        populate_sample_itinerary(itinerary)
        result = self.itinerary_repository.add_or_update(itinerary)

        if not result.success:
            on_error(error_message="Could not randomize itinerary", result=result)

        self.itinerary_repository.save_changes()
        polyline = get_polyline(itinerary)
        itinerary_dict = itinerary.as_dict()
        itinerary_dict['polylines'] = polyline
        return itinerary_dict


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
        self.itinerary_repository = ItineraryRepository()
        super(ItineraryShareResource, self).__init__()

    @authenticate
    def post(self, itinerary_id, **kwargs):
        itinerary = query(self.itinerary_repository.get(user_id=kwargs['user'].id, id=itinerary_id)).single_or_default(
            default=None)
        if not itinerary:
            abort(404, message="This itinerary does not exist")

        post_body = request.json
        if type(post_body) is not list:
            on_error(error_message="Invalid post body")

        result = ChangeResult()
        try:
            shares = query(post_body)
            for shared_user in itinerary.shared_users:
                if not shares.contains(shared_user, lambda lhs, rhs: rhs['user_id'] == lhs.id):
                    result.add_child_result(self.itinerary_repository.unshare(itinerary, shared_user.id))

            for share in post_body:
                user_id = share['user_id']
                permission = share['permission']
                result.add_child_result(self.itinerary_repository.share(itinerary, user_id, permission))

        except KeyError as ke:
            on_error(error_message="Invalid post body")

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


class RatingResource(Resource):
    def __init__(self):
        self.create_parser = RequestParser()
        self.create_parser.add_argument('title', type=str, required=True, location='json', help='Missing title')
        self.create_parser.add_argument('rating', type=int, required=True, location='json', help='Missing rating')
        self.create_parser.add_argument('content', type=str, required=True, location='json', help='Missing content')
        self.rating_repository = RatingRepository()
        self.itinerary_repository = ItineraryRepository()
        super(RatingResource, self).__init__()

    @authenticate
    def post(self, itinerary_id, **kwargs):
        user = kwargs['user']
        args = self.create_parser.parse_args()
        itinerary = self.itinerary_repository.find(itinerary_id)

        if not itinerary.public:
            abort(404, message="No itinerary with this id was found")

        rating = Rating(
            title=args['title'],
            rating=args['rating'],
            content=args['content'],
            user=user,
            itinerary=itinerary
        )

        result = self.rating_repository.add_or_update(rating)
        if not result.success():
            on_error(error_message="Could not save rating", result=result)

        self.rating_repository.save_changes()
        return rating

    @authenticate
    def get(self, itinerary_id, **kwargs):
        ratings = self.rating_repository.get(itinerary_id=itinerary_id)
        return ratings


class FriendsResource(Resource):
    def __init__(self):
        self.user_repository = UserRepository()
        super(FriendsResource, self).__init__()

    @authenticate
    def get(self, **kwargs):
        graph = facebook.GraphAPI(access_token=kwargs['token'])
        friends = graph.get_connections(kwargs['user'].facebook_id, "friends")['data']
        users = []
        for friend in friends:
            user = query(self.user_repository.get(facebook_id=friend['id'])).single_or_default(default=None)
            if user:
                users.append(user)

        return users


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
