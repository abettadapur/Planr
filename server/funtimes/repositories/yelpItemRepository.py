from funtimes.models.entities.change_result import ChangeResult

from funtimes.models.entities.yelp_item import YelpItem
from funtimes.repositories.baseRepository import BaseRepository
from funtimes import db


class YelpItemRepository(BaseRepository):

    def __init__(self):
        super(YelpItemRepository, self).__init__(YelpItem)

    def add_or_update(self, entity):
        return super(YelpItemRepository, self).add_or_update(entity)

    def validate(self, entity):
        return ChangeResult()

    def get_or_insert(self, yelp_item):
        return db.session.merge(yelp_item)

