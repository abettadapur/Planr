import facebook
from funtimes.rest import authenticate
from funtimes.models.user import User
from funtimes.repositories.itineraryRepository import ItineraryRepository
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


class ItineraryResource(Resource):
    def __init__(self):
        super(ItineraryResource, self).__init__()
        self.update_parser = RequestParser()

    # Get an itinerary by id
    def get(self, id, **kwargs):
        itinerary_repository = ItineraryRepository()
        itinerary = itinerary_repository.find(id)
        return itinerary

    # Update an itinerary by id
    def put(self, id):
        args = self.update_parser.parse_args()


class ItineraryListResource(Resource):
    def __init__(self):
        super(ItineraryListResource, self).__init__()
        self.create_parser = RequestParser()

    # List all itineraries
    @authenticate
    def get(self, **kwargs):
        try:
            user = kwargs['user']
            filter_args = request.args.to_dict()
            itinerary_repository = ItineraryRepository()
            itineraries = itinerary_repository.get(**filter_args)
            return itineraries
        except InvalidRequestError as ireq:
            on_error(str(ireq))

    # Create a new itinerary
    def post(self):
        pass


def on_error(error_message):
    abort(400, message=error_message)


def on_server_error(error_message):
    abort(500, message=error_message)


def on_invalid_auth(message=None):
    if message:
        abort(401, message=message)
    else:
        abort(401, message="unauthorized")
