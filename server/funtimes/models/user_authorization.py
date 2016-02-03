from funtimes import db
from funtimes.models.base import BaseModel


class UserAuthorization(BaseModel):
    __tablename__ = "user_authorization"
    user_id = db.Column(db.Integer, db.ForeignKey("user.id"), primary_key=True)
    user = db.relationship("User")
    token = db.Column(db.String(300), nullable=False)

    def __init__(self, token, user_id):
        self.user_id = user_id
        self.token = token
