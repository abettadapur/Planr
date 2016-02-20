from datetime import datetime

from funtimes import db
from funtimes.models.entities.base import BaseModel


class Item(BaseModel):
    __tablename__ = "item"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    yelp_category_id = db.Column(db.Integer, db.ForeignKey("yelp_category.id"))
    yelp_category = db.relationship("YelpCategory")
    itinerary_id = db.Column(db.Integer, db.ForeignKey("itinerary.id", name="fk_item_itinerary_id"))
    itinerary = db.relationship("Itinerary", back_populates="items")
    start_time = db.Column(db.DateTime, nullable=False)
    end_time = db.Column(db.DateTime, nullable=False)

    def update_from_dict(self, args):
        self.name = args['name']
        self.start_time = datetime.strptime(args['start_time'], "%Y-%m-%d %H:%M:%S")
        self.end_time = datetime.strptime(args['end_time'], "%Y-%m-%d %H:%M:%S")

    @staticmethod
    def create_from_dict(args):
        item = Item()
        item.name = args['name']
        item.start_time = datetime.strptime(args['start_time'], "%Y-%m-%d %H:%M:%S")
        item.end_time = datetime.strptime(args['end_time'], "%Y-%m-%d %H:%M:%S")
        return item

