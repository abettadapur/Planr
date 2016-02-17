from funtimes.models.change_result import ChangeResult

from funtimes.models.entities.yelpcategory import YelpCategory
from funtimes.repositories.baseRepository import BaseRepository


class YelpCategoryRepository(BaseRepository):

    def __init__(self):
        super(YelpCategoryRepository, self).__init__(YelpCategory)

    def add_or_update(self, entity):
        return super(YelpCategoryRepository, self).add_or_update(entity)

    def validate(self, entity):
        return ChangeResult()

    def get_categories_for_time(self, start_time, end_time):
        pass
