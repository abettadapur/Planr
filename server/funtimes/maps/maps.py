import googlemaps
from funtimes.etc import config
from funtimes.etc.util import pairwise
from asq.initiators import query

maps = googlemaps.Client(key=config.GOOGLE_API_KEY)


def get_directions(origin, destination):
    directions = maps.directions(str(origin), str(destination))
    return directions


def get_city(latitude, longitude):
    address_components = query(maps.reverse_geocode((latitude, longitude))[0]['address_components'])
    city = address_components.single(lambda a: 'locality' in a['types'])['long_name']
    state_province = address_components.single(lambda a: 'administrative_area_level_1' in a['types'])['short_name']
    return city + ", "+state_province

def get_polyline(plan):
    polylines = []
    counter = 1

    if plan.starting_coordinate:
        directions = get_directions(plan.starting_coordinate, plan.items[0].location.coordinate)
        polylines.append(
            {
                "order": 0,
                "origin": plan.starting_coordinate.as_dict(),
                "destination": plan.items[0].location.as_dict(),
                "polyline": directions[0]['overview_polyline']['points']
            }
        )

    for i1, i2 in pairwise(plan.items):
        directions = get_directions(i1.location.coordinate, i2.location.coordinate)
        polylines.append(
            {
                "order": counter,
                "origin": i1.location.as_dict(),
                "destination": i2.location.as_dict(),
                "polyline": directions[0]['overview_polyline']['points']
            }
        )
        counter += 1
    return polylines


def distance_matrix(origin, destinations):
    pass


def closest_item(origin, destinations):
    pass
