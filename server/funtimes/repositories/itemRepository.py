from datetime import datetime

from funtimes.models.entities.change_result import ChangeResult

from funtimes.models.entities.item import Item
from funtimes.repositories.baseRepository import BaseRepository
from funtimes.repositories.planRepository import PlanRepository
from funtimes.repositories.yelpCategoryRepository import YelpCategoryRepository
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

    def from_list(self, args):
        items = []
        for item in args:
            items.append(self.from_json(item))
        return items

    def from_json(self, json):
        item = Item(
            name=json.get('name'),
            yelp_category_id=json.get('yelp_category', {}).get('id'),
            yelp_category = json.get('yelp_category'),
            plan_id=json.get('plan_id'),
            start_time=datetime.strptime(json.get('start_time', "1970-00-01 00:00:00"), "%Y-%m-%d %H:%M:%S"),
            end_time=datetime.strptime(json.get('end_time', "1970-00-01 00:00:00"), "%Y-%m-%d %H:%M:%S"),
            type=json.get("type"),
            yelp_item_id=json.get('yelp_item', {}).get('id'),
            yelp_item=json.get('yelp_item'),
            location_id=json.get('location', {}).get('id'),
            location=json.get('location')
        )
        return item
