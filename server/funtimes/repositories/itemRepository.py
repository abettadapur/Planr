from funtimes.models.entities.change_result import ChangeResult

from funtimes.models.entities.item import Item
from funtimes.repositories.baseRepository import BaseRepository
from funtimes.repositories.yelpItemRepository import YelpItemRepository


class ItemRepository(BaseRepository):

    def __init__(self):
        super(ItemRepository, self).__init__(Item)

    def add_or_update(self, item):
        result = ChangeResult()
        # yelp_repository = YelpItemRepository()
        #
        # if item.type == "YELP" and item.yelp_item is not None:
        #     result.add_child_result(yelp_repository.add_or_update(item.yelp_item))
        #     item.yelp_item_id = item.yelp_item.id

        result.add_child_result(super(ItemRepository, self).add_or_update(item))
        return result

    def validate(self, entity):
        return ChangeResult()

    def create_from_dict(self, args):
        return Item.create_from_dict(args)

