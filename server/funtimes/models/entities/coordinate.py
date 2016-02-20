from funtimes import db
from funtimes.models.entities.base import BaseModel


class Coordinate(BaseModel):

    id = db.Column(db.Integer, primary_key=True)
    latitude = db.Column(db.Float, nullable=False)
    longitude = db.Column(db.Float, nullable=False)

    def __init__(self, lat, long):
        self.latitude = lat
        self.longitude = long

    def __str__(self):
        return str('%0.6f' % self.latitude) + ',' + str('%0.6f' % self.longitude)
