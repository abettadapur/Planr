from funtimes import db
from funtimes.models.entities.base import BaseModel


class YelpSearchFilter(BaseModel):
    __tablename__ = "yelp_search_filter"
    id = db.Column(db.Integer, primary_key=True)
    filter = db.Column(db.String(200), nullable=False)
    category_id = db.Column(db.Integer, db.ForeignKey("yelp_category.id"))
    category = db.relationship("YelpCategory", back_populates="search_filters")

    def __init__(self, name, filter):
        self.name = name
        self.filter = filter
