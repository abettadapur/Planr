from funtimes.models.entities.change_result import ChangeResult

from asq.initiators import query
from funtimes.models.entities.user import User
from funtimes.repositories.baseRepository import BaseRepository


class UserRepository(BaseRepository):
    def __init__(self):
        super(UserRepository, self).__init__(User)

    def add_or_update(self, entity):
        return super(UserRepository, self).add_or_update(entity)

    def validate(self, entity):
        return ChangeResult()

    def user_exists(self, facebook_id):
        return query(self.get(facebook_id=facebook_id)).any()
