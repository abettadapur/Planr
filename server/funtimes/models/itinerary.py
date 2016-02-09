import enum

from funtimes import db
from funtimes.models.base import BaseModel
from datetime import datetime


ItineraryShares = db.Table("ItineraryShares",
                           db.Column('id', db.Integer, primary_key=True),
                           db.Column('user_id', db.Integer, db.ForeignKey("user.id")),
                           db.Column('itinerary_id', db.Integer, db.ForeignKey("itinerary.id")),
                           db.Column('permissions', db.Enum("READ", "EDIT"), nullable=False)
                           )


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
    def as_dict(self):
        itinerary_dict = super(Itinerary, self).as_dict()
        itinerary_dict['user'] = self.user.as_dict()
        itinerary_dict['items'] = [i.as_dict() for i in self.items]
        return itinerary_dict

    def update_from_dict(self, update_dict):
        self.name = update_dict['name']
        self.date = datetime.strptime(update_dict['date'], "%Y-%m-%d")
        self.start_time = datetime.strptime(update_dict['start_time'], "%Y-%m-%d %H:%M:%S")
        self.end_time = datetime.strptime(update_dict['end_time'], "%Y-%m-%d %H:%M:%S")
        self.city = update_dict['city']
        self.public = update_dict['public']

    @staticmethod
    def create_from_dict(create_dict, user):
        itinerary = Itinerary()
        itinerary.name = create_dict['name']
        itinerary.date = datetime.strptime(create_dict['date'], "%Y-%m-%d")
        itinerary.start_time = datetime.strptime(create_dict['start_time'], "%Y-%m-%d %H:%M:%S")
        itinerary.end_time = datetime.strptime(create_dict['end_time'], "%Y-%m-%d %H:%M:%S")
        itinerary.city = create_dict['city']
        itinerary.public = create_dict['public']
        itinerary.user_id = user.id
        return itinerary
