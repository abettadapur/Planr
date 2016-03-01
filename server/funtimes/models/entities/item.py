from datetime import datetime

from funtimes import db
from funtimes.models.entities.base import BaseModel
from funtimes.models.entities.yelp_item import YelpItem


class Item(BaseModel):
    __tablename__ = "item"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    yelp_category_id = db.Column(db.Integer, db.ForeignKey("yelp_category.id"))
    yelp_category = db.relationship("YelpCategory")
    itinerary_id = db.Column(db.Integer, db.ForeignKey("itinerary.id", name="fk_item_itinerary_id", ondelete="CASCADE"))
    itinerary = db.relationship("Itinerary", back_populates="items")
    start_time = db.Column(db.DateTime, nullable=False)
    end_time = db.Column(db.DateTime, nullable=False)
    type = db.Column(db.Enum("YELP", "USER"), nullable=False)
    yelp_item_id = db.Column(db.String(300), db.ForeignKey("yelp_item.id"))
    yelp_item = db.relationship("YelpItem")
    location_id = db.Column(db.Integer, db.ForeignKey("location.id"))
    location = db.relationship("Location")

    def __init__(self, name=None, yelp_category=None, itinerary_id = None, start_time=None, end_time=None, type=None, yelp_item=None, location=None):
        self.name = name
        self.yelp_category = yelp_category
        self.itinerary_id = itinerary_id
        self.start_time = start_time
        self.end_time = end_time
        self.type = type
        self.yelp_item = yelp_item
        self.location = location

    def update_from_dict(self, args):
        self.name = args['name']
        self.start_time = datetime.strptime(args['start_time'], "%Y-%m-%d %H:%M:%S")
        self.end_time = datetime.strptime(args['end_time'], "%Y-%m-%d %H:%M:%S")

    def as_dict(self):
        item_dict = super(Item, self).as_dict()
        item_dict['yelp_item'] = self.yelp_item.as_dict()
        item_dict['location'] = self.location.as_dict()
        return item_dict

    @staticmethod
    def create_from_dict(args):
        item = Item()
        item.name = args['name']
        item.start_time = datetime.strptime(args['start_time'], "%Y-%m-%d %H:%M:%S")
        item.end_time = datetime.strptime(args['end_time'], "%Y-%m-%d %H:%M:%S")
        return item

