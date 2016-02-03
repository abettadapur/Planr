from funtimes import db
from funtimes.models.user_authorization import UserAuthorization
from funtimes.repositories.baseRepository import BaseRepository


class UserAuthorizationRepository(BaseRepository):

    def __init__(self):
        super(UserAuthorizationRepository, self).__init__(UserAuthorization)

    def add_or_update(self, entity):
        super(UserAuthorizationRepository, self).add_or_update(entity)

    def validate(self, entity):
        return True

    def get_user_from_token(self, token):
        user = self.get(token=token).first().user
        return user

    def remove_authorization(self, user_id):
        UserAuthorization.query.filter_by(user_id=user_id).delete(synchronize_session=False)

    def insert_authorization(self, token, user_id):
        self.remove_authorization(user_id)
        user_authorization = UserAuthorization(token, user_id)
        self.add_or_update(user_authorization)


