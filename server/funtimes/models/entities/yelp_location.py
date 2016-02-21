from funtimes import db
from funtimes.models.entities.coordinate import Coordinate
from funtimes.models.entities.base import BaseModel


class YelpLocation(BaseModel):

    id = db.Column(db.Integer(), primary_key=True)
    address = db.Column(db.String(500), nullable=False)
    city = db.Column(db.String(100), nullable=False)
    postal_code = db.Column(db.String(100), nullable=False)
    coordinate_id = db.Column(db.Integer, db.ForeignKey("coordinate.id"))
    coordinate = db.relationship("Coordinate")

    def __init__(self, address, city, postal_code, coordinate):
        self.address = address
        self.city = city
        self.postal_code = postal_code
        self.coordinate = coordinate

    def as_dict(self):
        location_dict = super(YelpLocation, self).as_dict()
        location_dict['coordinate'] = self.coordinate.as_dict()
        return location_dict
    
    @staticmethod
    def create_from_dict(create_dict):
        location = YelpLocation(
            address=', '.join(create_dict['address']),
            city=create_dict['city'],
            postal_code=create_dict['postal_code'],
            coordinate=Coordinate(create_dict['coordinate']['latitude'], create_dict['coordinate']['longitude'])
        )
        return location
