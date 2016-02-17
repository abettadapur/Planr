from funtimes.models.change_result import ChangeResult

from funtimes.models.entities.item import Item
from funtimes.repositories.baseRepository import BaseRepository


class ItemRepository(BaseRepository):

    def __init__(self):
        super(ItemRepository, self).__init__(Item)

    def add_or_update(self, entity):
        return super(ItemRepository, self).add_or_update(entity)

    def validate(self, entity):
        return ChangeResult()

    def create_from_dict(self, args):
        return Item.create_from_dict(args)

