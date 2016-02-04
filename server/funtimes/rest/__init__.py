from functools import wraps
from funtimes.repositories.userAuthorizationRepository import UserAuthorizationRepository
from flask_restful import request, abort
import flask_restful


def authenticate(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        user_auth_repository = UserAuthorizationRepository()

        if "Authorization" not in request.headers:
            abort(404)

        authorization_token = request.headers["Authorization"]
        user = user_auth_repository.get_user_from_token(authorization_token)

        if user:
            kwargs['user'] = user
            return func(*args, **kwargs)
        else:
            abort(401, message="Invalid authorization token")

    return wrapper


