from funtimes import db
from funtimes.models.user import User
from funtimes.repositories.userAuthorizationRepository import UserAuthorizationRepository
from funtimes.repositories.baseRepository import BaseRepository


class UserRepository(BaseRepository):
    def __init__(self):
        super(UserRepository, self).__init__(User)

    def add_or_update(self, entity):
        super(UserRepository, self).add_or_update(entity)

    def validate(self, entity):
        return True

    def user_exists(self, facebook_id):
        users = self.get(facebook_id=facebook_id)
        return len(users) > 0
