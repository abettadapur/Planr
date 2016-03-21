from funtimes import db
from funtimes.models.entities.base import BaseModel
from funtimes.models.entities.yelp_search_filter import YelpSearchFilter
from funtimes.repositories.yelpCategoryRepository import YelpCategoryRepository


class YelpCategory(BaseModel):
    __tablename__ = "yelp_category"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    search_term = db.Column(db.String(200), nullable=True)
    search_filters = db.relationship("YelpSearchFilter")
    icon_string = db.Column(db.String(500))

    def __init__(self, id=None, name=None, search_term=None, icon_string=None):
        self.id=id
        self.name = name
        self.search_term = search_term
        self.icon_string = icon_string

    @staticmethod
    def from_json(json):
        if json:
            category = YelpCategory(
                name=json.get('name'),
                id=json.get('id'),
                search_term=json.get('search_term'),
                icon_string=json.get('icon_string')
            )
            return category
        return None
    
    @staticmethod
    def get_from_yelp_tuple(yelp_tuple):
        cat_filter = yelp_tuple[1]
        categories = []
        match = YelpSearchFilter.query.filter_by(filter=cat_filter).all()
        if match is None:
            return categories
        for filter in match:
            cat=YelpCategory.query.filter_by(id=filter.category_id).one_or_none()
            if cat is not None:
                categories.append(cat)
        return categories

    def as_dict(self):
        cat_dict = super(YelpCategory, self).as_dict()
        cat_dict['search_filters'] = [filt.as_dict() for filt in self.search_filters]
        return cat_dict

    @staticmethod
    def get_default():
        category_repository = YelpCategoryRepository()
        return category_repository.get(name="Other")

