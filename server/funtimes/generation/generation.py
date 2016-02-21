from funtimes.models.entities.item import Item
from funtimes.models.entities.itinerary import Itinerary
from funtimes.models.entities.yelp_item import YelpItem
from funtimes.models.util.enums import StrategyType
from funtimes.repositories.yelpCategoryRepository import YelpCategoryRepository
from funtimes.models.util.strategy import DistanceStrategy, FirstStrategy, FirstRandomStrategy
from funtimes.yelp import yelpapi
from asq.initiators import query


def populate_sample_itinerary(itinerary):
    category_repository = YelpCategoryRepository()
    categories = category_repository.get_categories_for_time(itinerary.start_time, itinerary.end_time)
    items = fetch_items(itinerary.city, categories)
    itinerary.items = items


def fetch_items(city, categories):
    items = []
    coordinate = None
    for category in categories:
        yelp_item = get_yelp_item(city, category, coordinate)
        item = Item(name=yelp_item.name, yelp_category=category, type="YELP", yelp_item=yelp_item,
                    start_time=category.start_time, end_time=category.end_time)
        items.append(item)

    return items


def get_yelp_item(city, category, coordinate=None, strategy=DistanceStrategy()):
    extra_yelp_params = {}
    if coordinate is not None:
        extra_yelp_params['cll'] = str(coordinate)

    # for these strategies, use YelpAPI
    # then reset strategy_name so that we can still call strategy module below
    extra_yelp_params['radius_filters'] = 7500

    if strategy.type == StrategyType.popularity:
        extra_yelp_params['sort'] = 2
        strategy = FirstStrategy()
    elif strategy.type == StrategyType.distance:
        extra_yelp_params['sort'] = 1
        strategy = FirstRandomStrategy()

    search_results = [YelpItem.create_from_dict(result) for result in
                      yelpapi.search(category.search_term, city,
                                     query(category.search_filters).select(lambda f: f.filter).to_list(),
                                     **extra_yelp_params)]
    yelp_item = strategy.run_strategy(search_results)
    return yelp_item
