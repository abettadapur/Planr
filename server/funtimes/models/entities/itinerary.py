from datetime import datetime

from funtimes import db
from funtimes.models.entities.base import BaseModel


class ItineraryShares(BaseModel):
    __tablename__ = "itinerary_shares"
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey("user.id"))
    itinerary_id = db.Column(db.Integer, db.ForeignKey("itinerary.id"))
    permission = db.Column('permissions', db.Enum("READ", "EDIT"), nullable=False)


class Itinerary(BaseModel):
    __tablename__ = "itinerary"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    start_time = db.Column(db.DateTime, nullable=False)
    end_time = db.Column(db.DateTime, nullable=False)
    city = db.Column(db.String(200), nullable=False)
    public = db.Column(db.Boolean)
    items = db.relationship("Item")
    user_id = db.Column(db.Integer, db.ForeignKey("user.id"))
    user = db.relationship("User", back_populates="itineraries")
    shared_users = db.relationship("User", secondary="itinerary_shares")

    def __init__(self, name=None, start_time=None, end_time=None, city=None, public=False, items=[], user=None):
        self.name = name
        self.start_time = start_time
        self.end_time = end_time
        self.city = city
        self.public = public
        self.items = items
        self.user = user

    # user
    def as_dict(self):
        itinerary_dict = super(Itinerary, self).as_dict()
        itinerary_dict['user'] = self.user.as_dict()
        itinerary_dict['items'] = [i.as_dict() for i in self.items]
        return itinerary_dict

    def update_from_dict(self, update_dict):
        self.name = update_dict['name']
        self.start_time = datetime.strptime(update_dict['start_time'], "%Y-%m-%d %H:%M:%S %z")
        self.end_time = datetime.strptime(update_dict['end_time'], "%Y-%m-%d %H:%M:%S %z")
        self.city = update_dict['city']
        self.public = update_dict['public']

    @staticmethod
    def create_from_dict(create_dict, user):
        itinerary = Itinerary()
        itinerary.name = create_dict['name']
        itinerary.start_time = datetime.strptime(create_dict['start_time'], "%Y-%m-%d %H:%M:%S %z")
        itinerary.end_time = datetime.strptime(create_dict['end_time'], "%Y-%m-%d %H:%M:%S %z")
        itinerary.city = create_dict['city']
        itinerary.public = create_dict['public']
        itinerary.user = user
        return itinerary
