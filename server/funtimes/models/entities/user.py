from funtimes import db
from funtimes.models.entities.base import BaseModel
from funtimes.models.entities.plan import Plan


class User(BaseModel):
    __tablename__ = "user"
    id = db.Column(db.Integer, primary_key=True)
    facebook_id = db.Column(db.String(50), nullable=False)
    first_name = db.Column(db.String(200), nullable=False)
    last_name = db.Column(db.String(200), nullable=False)
    email = db.Column(db.String(200), nullable=False)
    plans = db.relationship("Plan")
    shared_plans = db.relationship("Plan", secondary="plan_shares")

    def __init__(self, facebook_id, first_name, last_name, email):
        self.facebook_id = facebook_id
        self.first_name = first_name
        self.last_name = last_name
        self.email = email
