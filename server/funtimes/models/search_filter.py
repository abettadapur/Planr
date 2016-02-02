from funtimes import db
from funtimes.models.base import BaseModel


class SearchFilter(BaseModel):
    __tablename__ = "search_filter"
    id = db.Column(db.Integer, primary_key=True)
    filter = db.Column(db.String(200), nullable=False)
    category_id = db.Column(db.Integer, db.ForeignKey("category.id"))

    def __init__(self, name, filter):
        self.name = name
        self.filter = filter
