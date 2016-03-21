from funtimes.models.entities.location import Location
from funtimes.models.entities.yelp_category import YelpCategory
from funtimes import db
from funtimes.models.entities.base import BaseModel

class YelpItemCategories(BaseModel):
    __tablename__ = "yelp_item_categories"
    id = db.Column(db.Integer, primary_key=True)
    cat_id = db.Column(db.Integer, db.ForeignKey("yelp_category.id"))
    yelp_item_id = db.Column(db.String(300), db.ForeignKey("yelp_item.id", ondelete="CASCADE"))


class YelpItem(BaseModel):
    __tablename__ = "yelp_item"
    id = db.Column(db.String(300), primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    image_url = db.Column(db.String(300), nullable=True)
    url = db.Column(db.String(300), nullable=True)
    phone = db.Column(db.String(15), nullable=True)
    rating = db.Column(db.Integer())
    review_count = db.Column(db.Integer())
    location_id = db.Column(db.Integer, db.ForeignKey("location.id"))
    location = db.relationship("Location")
    categories = db.relationship("YelpCategory", secondary="yelp_item_categories")

    def __init__(self, id=None, name=None, image_url=None, url=None, phone=None, rating=1, review_count = 0, location=None, categories = []):
        self.id = id
        self.name = name
        self.image_url = image_url
        self.url = url
        self.phone = phone
        self.rating = rating
        self.review_count = review_count
        self.location = location
        self.categories = categories

    def as_dict(self):
        item_dict = super(YelpItem, self).as_dict()
        item_dict['location'] = self.location.as_dict()
        item_dict['categories'] = [cat.as_dict() for cat in self.categories]
        return item_dict

    @staticmethod
    def create_from_dict(dict):
        categories = []
        for tup in dict['categories']:
            categories += YelpCategory.get_from_yelp_tuple(tup)

        if len(categories) == 0:
            categories.append(YelpCategory.get_default())

        item = YelpItem(
            id=dict['id'],
            name=dict['name'],
            image_url=dict['image_url'] if 'image_url' in dict else None,
            url=dict['url'],
            phone=dict['phone'] if 'phone' in dict else None,
            rating=dict['rating'],
            review_count=dict['review_count'],
            location=Location.create_from_yelp_dict(dict['location']),
            categories=categories
        )
        return item

    @staticmethod
    def from_json(json):
        if json:
            item = YelpItem(
                id=json.get('id'),
                name=json.get('name'),
                image_url=json.get('image_url'),
                url=json.get('url'),
                phone=json.get('phone'),
                rating=json.get('rating'),
                review_count=json.get('review_count'),
                location=Location.from_json(json.get('location'))
            )
            return item
        return None
