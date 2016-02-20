from funtimes.models.entities.item import Item
from funtimes.models.util.enums import StrategyType
from funtimes.yelp import yelpapi


def fetch_sample_itinerary(city, start_time, end_time):
    categories = get_categories(start_time, end_time)
    items = fetch_items(city, categories)


def fetch_items(city, categories):
    items = []
    coordinate = None
    for category in categories:
        yelp_item = get_yelp_item(city, category, coordinate)
        item = Item()
        items.append(item)


def get_yelp_item(city, category, coordinate = None, strategy=StrategyType.distance):
    extra_yelp_params = {}
    if coordinate is not None:
        extra_yelp_params['cll'] = str(coordinate)

    # for these strategies, use YelpAPI
    # then reset strategy_name so that we can still call strategy module below
    extra_yelp_params['radius_filters'] = 7500

    if strategy == StrategyType.popularity:
        extra_yelp_params['sort'] = 2
        #strategy_name = 'first'
    elif strategy == StrategyType.distance:
        extra_yelp_params['sort'] = 1
        #strategy_name = 'first_random'

    search_results = yelpapi.search(category.search_term, city, category.filters, **extra_yelp_params)

    # candidate_ids_with_names = [(result['id'], result['name'])
    #                             for result
    #                             in search_results
    #                             if result['id'] not in disallowed_yelp_ids]

    candidate_ids = [elem[0] for elem in candidate_ids_with_names]

    if len(candidate_ids_with_names) == 0:
        print "best_yelp_id_with_name: no good ids found!  returning random result"
        default_result = search_results[random.randrange(len(search_results))]
        return default_result['id'], default_result['name']

    index = strategy.run_strategy(strategy_name, candidate_ids)

    if index == -1:  # strategy found nothing
        index = 0

    yelp_id = candidate_ids_with_names[index][0]
    yelp_name = candidate_ids_with_names[index][1]
    return yelp_id, yelp_namex