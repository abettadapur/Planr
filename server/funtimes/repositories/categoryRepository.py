from funtimes.models.category import Category
from funtimes.models.change_result import ChangeResult
from funtimes.repositories.baseRepository import BaseRepository


class CategoryRepository(BaseRepository):

    def __init__(self):
        super(CategoryRepository, self).__init__(Category)

    def add_or_update(self, entity):
        return super(CategoryRepository, self).add_or_update(entity)

    def validate(self, entity):
        return ChangeResult()
