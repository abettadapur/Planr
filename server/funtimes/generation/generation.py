from datetime import datetime

from asq.initiators import query
from funtimes.integrations.yelp import yelpapi
from funtimes.models.entities.item import Item
from funtimes.models.entities.yelp_item import YelpItem
from funtimes.models.util.enums import StrategyType
from funtimes.models.util.strategy import DistanceStrategy, FirstStrategy, FirstRandomStrategy
from funtimes.repositories.yelpCategoryRepository import YelpCategoryRepository
from funtimes.repositories.yelpItemRepository import YelpItemRepository


def populate_sample_plan(plan, categories):
    items = fetch_items(plan.starting_coordinate, categories, plan.start_time.date())
    plan.items = items


def fetch_items(coordinate, categories, plan_date):
    items = []
    for category in categories:
        yelp_item = get_yelp_item(coordinate, category)

        item = Item(
            name=yelp_item.name,
            yelp_category=category,
            type="YELP",
            yelp_item=yelp_item,
            start_time=datetime.combine(plan_date, category.start_time),
            end_time=datetime.combine(plan_date, category.end_time),
            location=yelp_item.location
        )

        coordinate = item.location.coordinate
        items.append(item)

    return items


def get_yelp_item(coordinate, category, strategy=DistanceStrategy()):
    extra_yelp_params = {}
    yelp_item_repository = YelpItemRepository()
    if coordinate is not None:
        extra_yelp_params['ll'] = str(coordinate)

    # for these strategies, use YelpAPI
    # then reset strategy_name so that we can still call strategy module below
    extra_yelp_params['radius_filters'] = 7500

    if strategy.type == StrategyType.popularity:
        extra_yelp_params['sort'] = 2
        strategy = FirstStrategy()
    elif strategy.type == StrategyType.distance:
        extra_yelp_params['sort'] = 1
        strategy = FirstRandomStrategy()

    search_results = query(yelpapi.search(category.search_term,
                                          query(category.search_filters).select(lambda f: f.filter).to_list(),
                                          **extra_yelp_params)).where(
        lambda r: ['Food Trucks', 'foodtrucks'] not in r['categories'])
    yelp_items = [YelpItem.create_from_dict(result) for result in search_results]
    yelp_item = strategy.run_strategy(yelp_items)
    return yelp_item_repository.get_or_insert(yelp_item)
