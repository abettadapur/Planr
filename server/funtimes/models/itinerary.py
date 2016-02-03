from funtimes import db
from funtimes.models.base import BaseModel
import datetime, time


class Itinerary(BaseModel):
    __tablename__ = "itinerary"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    date = db.Column(db.Date, nullable=False)
    start_time = db.Column(db.DateTime, nullable=False)
    end_time = db.Column(db.DateTime, nullable=False)
    city = db.Column(db.String(200), nullable=False)
    public = db.Column(db.Boolean)
    items = db.relationship("Item")
    user_id = db.Column(db.Integer, db.ForeignKey("user.id"))
    user = db.relationship("User", back_populates="itineraries")
    # user

    def __init__(self, name):
        super(Itinerary, self).__init__()
        self.name = name

    def as_dict(self):
        itinerary_dict = super(Itinerary, self).as_dict()
        itinerary_dict['user'] = self.user.as_dict()
        itinerary_dict['items'] = [i.as_dict() for i in self.items]
        return itinerary_dict

