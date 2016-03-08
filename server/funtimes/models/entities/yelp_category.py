from funtimes import db
from funtimes.models.entities.base import BaseModel
from funtimes.models.entities.yelp_search_filter import YelpSearchFilter


class YelpCategory(BaseModel):
    __tablename__ = "yelp_category"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    search_term = db.Column(db.String(200), nullable=True)
    search_filters = db.relationship("YelpSearchFilter")

    def __init__(self, name, search_term):
        self.name = name
        self.search_term = search_term
