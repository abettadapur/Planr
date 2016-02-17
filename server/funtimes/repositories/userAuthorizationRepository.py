from funtimes.models.change_result import ChangeResult

from asq.initiators import query
from funtimes.models.entities.user_authorization import UserAuthorization
from funtimes.repositories.baseRepository import BaseRepository


class UserAuthorizationRepository(BaseRepository):

    def __init__(self):
        super(UserAuthorizationRepository, self).__init__(UserAuthorization)

    def add_or_update(self, entity):
        return super(UserAuthorizationRepository, self).add_or_update(entity)

    def validate(self, entity):
        return ChangeResult()

    def get_user_from_token(self, token):
        user = query(self.get(token=token)).first_or_default(default=None)
        if not user:
            return None
        return user.user

    def remove_authorization(self, user_id):
        UserAuthorization.query.filter_by(user_id=user_id).delete(synchronize_session=False)

    def insert_authorization(self, token, user_id):
        self.remove_authorization(user_id)
        user_authorization = UserAuthorization(token, user_id)
        return self.add_or_update(user_authorization)


