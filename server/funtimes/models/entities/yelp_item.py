from funtimes.models.entities.yelp_location import YelpLocation
from funtimes import db
from funtimes.models.entities.base import BaseModel


class YelpItem(BaseModel):
    __tablename__ = "yelp_item"
    id = db.Column(db.String(300), primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    image_url = db.Column(db.String(300), nullable=True)
    url = db.Column(db.String(300), nullable=True)
    phone = db.Column(db.String(15), nullable=True)
    rating = db.Column(db.Integer())
    yelp_location_id = db.Column(db.Integer, db.ForeignKey("yelp_location.id"))
    yelp_location = db.relationship("YelpLocation")

    def __init__(self, id=None, name=None, image_url=None, url=None, phone=None, rating=1, location=None):
        self.id = id
        self.name = name
        self.image_url = image_url
        self.url = url
        self.phone = phone
        self.rating = rating
        self.yelp_location = location

    def as_dict(self):
        item_dict = super(YelpItem, self).as_dict()
        item_dict['location'] = self.yelp_location.as_dict()
        return item_dict

    @staticmethod
    def create_from_dict(dict):
        item = YelpItem(
            id=dict['id'],
            name=dict['name'],
            image_url=dict['image_url'],
            url=dict['url'],
            phone=dict['phone'] if 'phone' in dict else None,
            rating=dict['rating'],
            location=YelpLocation.create_from_dict(dict['location'])
        )
        return item
