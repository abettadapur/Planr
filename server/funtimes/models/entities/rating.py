from funtimes import db
from funtimes.models.entities.base import BaseModel


class Rating(BaseModel):
    __tablename__ = "rating"
    id = db.Column(db.String(300), primary_key=True)
    title = db.Column(db.String(200), nullable=False)
    rating = db.Column(db.Integer(), nullable=False)
    content = db.Column(db.String(1000), nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey("user.id"))
    user = db.relationship("User")
    plan_id = db.Column(db.Integer(), db.ForeignKey("plan.id"))
    plan = db.relationship("Plan")

    def __init__(self, id=None, title=None, rating=0, content=None, user=None, plan=None):
        self.id = id
        self.title = title
        self.rating = rating
        self.content = content
        self.user = user
        self.plan = plan

    def as_dict(self):
        rating_dict = super(Rating, self).as_dict()
        rating_dict['user'] = self.user.as_dict()
        rating_dict['plan'] = self.plan.as_dict()
        return rating_dict
