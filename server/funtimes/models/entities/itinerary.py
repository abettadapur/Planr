from datetime import datetime

from funtimes import db
from funtimes.models.entities.base import BaseModel
from funtimes.models.entities.coordinate import Coordinate


class ItineraryShares(BaseModel):
    __tablename__ = "itinerary_shares"
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey("user.id"))
    itinerary_id = db.Column(db.Integer, db.ForeignKey("itinerary.id", ondelete="CASCADE"))
    permission = db.Column('permissions', db.Enum("READ", "EDIT"), nullable=False)


class Itinerary(BaseModel):
    __tablename__ = "itinerary"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    start_time = db.Column(db.DateTime, nullable=False)
    end_time = db.Column(db.DateTime, nullable=False)
    starting_address = db.Column(db.String(2000), nullable=False)
    starting_coordinate_id = db.Column(db.Integer(), db.ForeignKey("coordinate.id", ondelete="CASCADE"))
    starting_coordinate = db.relationship("Coordinate", cascade="all, delete")
    public = db.Column(db.Boolean)
    items = db.relationship("Item", cascade="all, delete")
    user_id = db.Column(db.Integer, db.ForeignKey("user.id"))
    user = db.relationship("User", back_populates="itineraries")
    shared_users = db.relationship("User", secondary="itinerary_shares")

    def __init__(self, name=None, start_time=None, end_time=None, city=None, starting_address=None,
                 starting_coordinate=None, public=False, items=[], user=None):
        self.name = name
        self.start_time = start_time
        self.end_time = end_time
        self.starting_address = starting_address
        self.starting_coordinate = starting_coordinate
        self.public = public
        self.items = items
        self.user = user

    # user
    def as_dict(self):
        itinerary_dict = super(Itinerary, self).as_dict()
        itinerary_dict['user'] = self.user.as_dict()
        itinerary_dict['items'] = [i.as_dict() for i in self.items]
        itinerary_dict['shared_users'] = [{"user": u.as_dict(), "permission": ""} for u in self.shared_users]
        itinerary_dict['starting_coordinate'] = self.starting_coordinate.as_dict()
        return itinerary_dict

    def update_from_dict(self, update_dict):
        self.name = update_dict['name']
        self.start_time = datetime.strptime(update_dict['start_time'], "%Y-%m-%d %H:%M:%S %z")
        self.end_time = datetime.strptime(update_dict['end_time'], "%Y-%m-%d %H:%M:%S %z")
        self.city = update_dict['city']
        self.public = update_dict['public']

    @staticmethod
    def create_from_dict(create_dict, user):
        itinerary = Itinerary(
            name=create_dict['name'],
            start_time=datetime.strptime(create_dict['start_time'], "%Y-%m-%d %H:%M:%S %z"),
            end_time=datetime.strptime(create_dict['end_time'], "%Y-%m-%d %H:%M:%S %z"),
            starting_address=create_dict['starting_address'],
            starting_coordinate=Coordinate(float(create_dict['starting_coordinate'].partition(',')[0]),
                                           float(create_dict['starting_coordinate'].partition(',')[2])),
            public=create_dict['public'],
            user=user
        )
        return itinerary
