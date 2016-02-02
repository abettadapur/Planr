from funtimes import db
from funtimes.models.base import BaseModel


class Category(BaseModel):
    __tablename__ = "Category"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    search_term = db.Column(db.String(200), nullable=True)
    search_filters = db.relationship("SearchFilter")

    def __init__(self, name, search_term):
        self.name = name
        self.search_term = search_term
