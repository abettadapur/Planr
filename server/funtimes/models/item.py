from funtimes import db
from funtimes.models.base import BaseModel


class Item(BaseModel):
    __tablename__ = "Item"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    category_id = db.Column(db.Integer, db.ForeignKey("Category.id"))
    category = db.relationship("Category")
    itinerary_id = db.Column(db.Integer, db.ForeignKey("Itinerary.id"))
    itinerary = db.relationship("Itinerary", back_populates="items")
    start_time = db.Column(db.DateTime, nullable=False)
    end_time = db.Column(db.DateTime, nullable=False)

    def __init__(self, name):
        self.name = name
