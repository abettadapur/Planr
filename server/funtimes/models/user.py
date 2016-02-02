from funtimes import db
from funtimes.models.base import BaseModel


class User(BaseModel):
    __tablename__ = "User"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    itineraries = db.relationship("Itinerary")

    def __init__(self, name):
        self.name = name
