from abc import ABCMeta, abstractmethod
from funtimes.models.base import BaseModel

class BaseRepository(metaclass=ABCMeta):

    def __init__(self, model_class: BaseModel):
        self.model_class = model_class

    def find(self, id):
        entity = self.model_class.query.get(id)
        return entity

    def add_or_update(self, entity):
        if self.validate(entity):
            pass
        else:
            pass

    def delete(self, id):
        pass

    @abstractmethod
    def validate(self, entity):
        pass

