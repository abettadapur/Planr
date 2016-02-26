from funtimes import db
from funtimes.models.entities.base import BaseModel

class City(BaseModel):
    __tablename__ = "city"
    zip = db.Column(db.Integer, nullable=True, primary_key=True)
    state = db.Column(db.String(2), nullable=True,primary_key=True)
    city = db.Column(db.String(16), nullable=True,primary_key=True)
    lat = db.Column(db.Float, nullable=True)
    lng = db.Column(db.Float, nullable=True)

    def __init__(self, zip, state, city, lat, lng):
        self.zip = zip
        self.state = state
        self.city = city
        self.lat = lat
        self.lng = lng

    def as_dict(self):
        ret_dict = {'zip': self.zip , 
                    'state': self.state, 
                    'city': self.city,
                    'lat': self.lat,
                    'lng': self.lng}
        return ret_dict
