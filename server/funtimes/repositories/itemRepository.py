from funtimes.models.item import Item
from funtimes.repositories.baseRepository import BaseRepository


class ItemRepository(BaseRepository):

    def __init__(self):
        super(ItemRepository, self).__init__(Item)

    def add_or_update(self, entity):
        super(ItemRepository, self).add_or_update(entity)

    def validate(self, entity):
        return True

