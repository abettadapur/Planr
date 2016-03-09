from funtimes.models.entities.change_result import ChangeResult

from funtimes.models.entities.yelp_category import YelpCategory
from funtimes.repositories.baseRepository import BaseRepository
from asq.initiators import query


class YelpCategoryRepository(BaseRepository):

    def __init__(self):
        super(YelpCategoryRepository, self).__init__(YelpCategory)

    def add_or_update(self, entity):
        return super(YelpCategoryRepository, self).add_or_update(entity)

    def validate(self, entity):
        return ChangeResult()

    def get_categories_for_time(self, start_time, end_time):
        categories = query(YelpCategory.query.all())
        return categories.where(lambda c: start_time.time() <= c.end_time).where(
            lambda c: end_time.time() >= c.start_time).to_list()

    def get_from_list(self, categories):
        results = []
        for category in categories:
            results.append(self.find(category['id']))
        return results
