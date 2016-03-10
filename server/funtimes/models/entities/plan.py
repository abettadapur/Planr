from datetime import datetime

from funtimes import db
from funtimes.models.entities.base import BaseModel
from funtimes.models.entities.coordinate import Coordinate
from funtimes.repositories.itemRepository import ItemRepository

class PlanShares(BaseModel):
    __tablename__ = "plan_shares"
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey("user.id"))
    plan_id = db.Column(db.Integer, db.ForeignKey("plan.id", ondelete="CASCADE"))
    permission = db.Column('permissions', db.Enum("READ", "EDIT"), nullable=False)


class PlanCategories(BaseModel):
    __tablename__ = "plan_categories"
    id = db.Column(db.Integer, primary_key=True)
    cat_id = db.Column(db.Integer, db.ForeignKey("yelp_category.id"))
    plan_id = db.Column(db.Integer, db.ForeignKey("plan.id", ondelete="CASCADE"))


class Plan(BaseModel):
    __tablename__ = "plan"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    start_time = db.Column(db.DateTime)
    end_time = db.Column(db.DateTime)
    starting_address = db.Column(db.String(2000), nullable=False)
    starting_coordinate_id = db.Column(db.Integer(), db.ForeignKey("coordinate.id", ondelete="CASCADE"))
    starting_coordinate = db.relationship("Coordinate", cascade="all, delete")
    public = db.Column(db.Boolean)
    items = db.relationship("Item", cascade="all, delete", order_by="Item.start_time")
    categories = db.relationship("YelpCategory", secondary="plan_categories")
    user_id = db.Column(db.Integer, db.ForeignKey("user.id"))
    user = db.relationship("User", back_populates="plans")
    shared_users = db.relationship("User", secondary="plan_shares")

    def __init__(self, name=None, start_time=None, end_time=None, city=None, starting_address=None,
                 starting_coordinate=None, public=False, items=[], categories=[], user=None):
        self.name = name
        self.start_time = start_time
        self.end_time = end_time
        self.starting_address = starting_address
        self.starting_coordinate = starting_coordinate
        self.public = public
        self.items = items
        self.categories = categories
        self.user = user

    # user
    def as_dict(self):
        plan_dict = super(Plan, self).as_dict()
        plan_dict['user'] = self.user.as_dict()
        plan_dict['items'] = [i.as_dict() for i in self.items]
        plan_dict['shared_users'] = [{"user": u.as_dict(), "permission": ""} for u in self.shared_users]
        plan_dict['starting_coordinate'] = self.starting_coordinate.as_dict()
        return plan_dict

    def update_from_dict(self, update_dict):
        self.name = update_dict['name']

        # Not requierd, can do this through item addition/removal also now
        if 'start_time' in update_dict:
            self.start_time = datetime.strptime(update_dict['start_time'], "%Y-%m-%d %H:%M:%S %z")

        if 'end_time' in update_dict:
            self.end_time = datetime.strptime(update_dict['end_time'], "%Y-%m-%d %H:%M:%S %z")

        self.public = update_dict['public']

    @staticmethod
    def create_from_dict(create_dict, user):
        plan = Plan(
            name=create_dict['name'],
            public=create_dict['public'],
            user=user
        )
        if 'start_time' in create_dict:
            plan.start_time = datetime.strptime(create_dict['start_time'], "%Y-%m-%d %H:%M:%S %z")

        if 'end_time' in create_dict:
            plan.end_time = datetime.strptime(create_dict['end_time'], "%Y-%m-%d %H:%M:%S %z")

        if 'starting_address' in create_dict:
            plan.starting_address = create_dict['starting_address']
            plan.starting_coordinate = Coordinate(float(create_dict['starting_coordinate'].partition(',')[0]),
                                                  float(create_dict['starting_coordinate'].partition(',')[2]))
        return plan

    @staticmethod
    def from_json(json, user):
        plan = Plan(
                name=json.get('name'),
                start_time=datetime.strptime(json.get('start_time'), "%Y-%m-%d %H:%M:%S %z") if json.get('start_time') is not None else None,
                end_time=datetime.strptime(json.get('end_time'), "%Y-%m-%d %H:%M:%S %z") if json.get('end_time') is not None else None,
                public=json.get('public'),
                starting_address=json.get('starting_address'),
                starting_coordinate=Coordinate.from_json(json.get('starting_coordinate')),
                user=user,
            )
        return plan

    def _set_start_item(self, item):
        self.start_time = item.start_time
        self.starting_address = item.location.address
        self.starting_coordinate = item.location.coordinate
        self.starting_coordinate_id = item.location.coordinate_id

    def add_item(self, item):
        # Item model assumed to be valid here (correct plan ID)
        if self.start_time is None or item.start_time < self.start_time:
            self._set_start_item(item)
        if self.end_time is None or item.end_time < self.end_time:
            self.end_time = item.end_time

        # @Alex Allow duplicates? Makes removal easier later (see beow)

        item = db.session.merge(item)

        result = ItemRepository().validate(item)

        if result.success():
            self.categories.append(item.yelp_category)
            self.items.append(item)

        return result

    def remove_item(self, item):
        # Item model assumed to be valid here (correct plan ID)
        if len(self.items) == 1:
            self.start_time = None
            self.end_time = None
            self.items = []
            self.categories = []
            self.starting_address = None
            self.starting_coordinate = None
            self.starting_coordinate_id = None
        else:
            self.items.remove(item)
            if self.items[0].start_time < self.start_time:
                self._set_start_item(item)
            if self.items[-1].end_time < self.end_time:
                self.end_time = item.end_time
            # Remove category, duplicates make it so if there are multiple it is still in the list
            self.categories.remove(item.yelp_category)

        self.items.add(item)
