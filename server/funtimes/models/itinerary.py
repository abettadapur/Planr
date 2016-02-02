from funtimes import db
from funtimes.models.base import BaseModel
import datetime, time


class Itinerary(BaseModel):
    __tablename__ = "Itinerary"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    date = db.Column(db.Date, nullable=False)
    start_time = db.Column(db.DateTime, nullable=False)
    end_time = db.Column(db.DateTime, nullable=False)
    city = db.Column(db.String(200), nullable=False)
    public = db.Column(db.Boolean)
    items = db.relationship("Item")
    user_id = db.Column(db.Integer, db.ForeignKey("User.id"))
    user = db.relationship("User", back_populates="itineraries")
    # user

    def __init__(self, name):
        self.name = name

