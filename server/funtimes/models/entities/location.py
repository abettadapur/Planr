from funtimes import db
from funtimes.models.entities.coordinate import Coordinate
from funtimes.models.entities.base import BaseModel


class Location(BaseModel):
    __tablename__ = "location"
    id = db.Column(db.Integer(), primary_key=True)
    address = db.Column(db.String(500), nullable=False)
    city = db.Column(db.String(100), nullable=False)
    state_code = db.Column(db.String(5), nullable=True)
    postal_code = db.Column(db.String(100), nullable=True)
    coordinate_id = db.Column(db.Integer, db.ForeignKey("coordinate.id"))
    coordinate = db.relationship("Coordinate", cascade="all")

    def __init__(self, id=None, address=None, city=None, postal_code=None, state_code=None, coordinate=None, coordinate_id=None):
        self.id = id
        self.address = address
        self.city = city
        self.postal_code = postal_code
        self.coordinate = coordinate
        self.coordinate_id = coordinate_id
        self.state_code = state_code

    def as_dict(self):
        location_dict = super(Location, self).as_dict()
        location_dict['coordinate'] = self.coordinate.as_dict()
        location_dict['display'] = str(self)
        return location_dict

    @staticmethod
    def create_from_yelp_dict(create_dict):
        location = Location(
            address=', '.join(create_dict['address']),
            city=create_dict['city'],
            postal_code=create_dict['postal_code'] if 'postal_code' in create_dict else None,
            state_code=create_dict['state_code'] if 'state_code' in create_dict else None,
            coordinate=Coordinate(lat=create_dict['coordinate']['latitude'], long=create_dict['coordinate']['longitude']) if 'coordinate' in create_dict else None
        )
        return location

    @staticmethod
    def from_json(json):
        if json:
            item = Location(
                id=json.get('id'),
                address=json.get('address'),
                city=json.get('city'),
                postal_code=json.get('postal_code'),
                state_code=json.get('state_code'),
                coordinate=Coordinate.from_json(json.get('coordinate')),
                coordinate_id=json.get("coordinate", {}).get("id")
            )
            return item
        return None

    def __str__(self):
        return "{address}, {city}, {state}, {postal_code}".format(
            address=self.address,
            city=self.city,
            state=self.state_code,
            postal_code=self.postal_code
        )
