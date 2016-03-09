from funtimes import db
from funtimes.models.entities.base import BaseModel


class Coordinate(BaseModel):

    id = db.Column(db.Integer, primary_key=True)
    latitude = db.Column(db.Float, nullable=False)
    longitude = db.Column(db.Float, nullable=False)

    def __init__(self, id=None, lat=None, long=None):
        self.id=id
        self.latitude = lat
        self.longitude = long

    def __str__(self):
        return str('%0.6f' % self.latitude) + ',' + str('%0.6f' % self.longitude)

    @staticmethod
    def from_json(json):
        if json:
            coordinate = Coordinate(
                id = json.get("id"),
                lat=json.get('latitude'),
                long=json.get('longitude')
            )
            return coordinate
        return None
