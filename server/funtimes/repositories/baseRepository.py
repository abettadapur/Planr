from abc import ABCMeta, abstractmethod
from funtimes.models.base import BaseModel
from funtimes.models.itinerary import Itinerary
from funtimes.models.item import Item
from funtimes.models.user import User
from funtimes.models.category import Category
from funtimes.models.search_filter import SearchFilter
from funtimes.models.user_authorization import UserAuthorization
from funtimes import db


class BaseRepository(metaclass=ABCMeta):
    def __init__(self, model_class: BaseModel):
        self.model_class = model_class

    # Get an entity by its primary key
    def find(self, id):
        entity = self.model_class.query.get(id)
        return entity

    # Add an entity if it doesn't exist, or update an existing entity
    def add_or_update(self, entity):
        if self.validate(entity):
            db.session.add(entity)
        else:
            pass

    def get(self, **kwargs):
        entities = self.model_class.query.filter_by(**kwargs).all()
        return entities

    def delete(self, id):
        self.model_class.query.filter_by(id=id).delete()

    @abstractmethod
    def validate(self, entity):
        pass

    def save_changes(self):
        db.session.commit()
