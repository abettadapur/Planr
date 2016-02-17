from asq.initiators import query
from funtimes.models.item import Item
from funtimes.models.change_result import ChangeResult
from funtimes.repositories.baseRepository import BaseRepository
from funtimes.repositories.yelpCategoryRepository import YelpCategoryRepository


class ItemRepository(BaseRepository):

    def __init__(self):
        super(ItemRepository, self).__init__(Item)

    def add_or_update(self, entity):
        return super(ItemRepository, self).add_or_update(entity)

    def validate(self, entity):
        return ChangeResult()

    def create_from_dict(self, args):
        return Item.create_from_dict(args)

