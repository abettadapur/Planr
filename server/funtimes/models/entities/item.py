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
    plan_id = db.Column(db.Integer, db.ForeignKey("plan.id", name="fk_item_plan_id", ondelete="CASCADE"))
    plan = db.relationship("Plan", back_populates="items")
    start_time = db.Column(db.DateTime, nullable=False)
    end_time = db.Column(db.DateTime, nullable=False)
    type = db.Column(db.Enum("YELP", "USER"), nullable=False)
    yelp_item_id = db.Column(db.String(300), db.ForeignKey("yelp_item.id"))
    yelp_item = db.relationship("YelpItem")
    location_id = db.Column(db.Integer, db.ForeignKey("location.id"))
    location = db.relationship("Location")

    def __init__(self, name=None, yelp_category=None, yelp_category_id = None, plan_id = None, start_time=None, end_time=None, type=None, yelp_item=None, yelp_item_id=None, location=None, location_id = None):
        self.name = name
        self.yelp_category = yelp_category
        self.yelp_category_id = yelp_category_id
        self.plan_id = plan_id
        self.start_time = start_time
        self.end_time = end_time
        self.type = type
        self.yelp_item = yelp_item
        self.yelp_item_id = yelp_item_id
        self.location = location
        self.location_id = location_id

    def update_from_dict(self, args):
        self.name = args['name']
        self.start_time = datetime.strptime(args['start_time'], "%Y-%m-%d %H:%M:%S")
        self.end_time = datetime.strptime(args['end_time'], "%Y-%m-%d %H:%M:%S")

    def as_dict(self):
        item_dict = super(Item, self).as_dict()
        item_dict['yelp_item'] = self.yelp_item.as_dict()
        item_dict['location'] = self.location.as_dict()
        item_dict['yelp_category'] = self.yelp_category.as_dict()
        return item_dict

    @staticmethod
    def create_from_dict(args):
        item = Item()
        item.name = args['name']
        item.start_time = datetime.strptime(args['start_time'], "%Y-%m-%d %H:%M:%S")
        item.end_time = datetime.strptime(args['end_time'], "%Y-%m-%d %H:%M:%S")
        item.yelp_item_id = args['yelp_item_id']
        return item

