import googlemaps
from funtimes.etc import config
from funtimes.etc.util import pairwise

maps = googlemaps.Client(key=config.GOOGLE_API_KEY)


def get_directions(origin, destination):
    directions = maps.directions(str(origin), str(destination))
    return directions


def get_polyline(itinerary):
    polylines = []
    counter = 1
    for i1, i2 in pairwise(itinerary.items):
        directions = get_directions(i1.location, i2.location)
        polylines.append(
            {
                "order": counter,
                "origin": i1.location.as_dict(),
                "destination": i2.location.as_dict(),
                "polyline": directions[0]['overview_polyline']['points']
            }
        )
        counter = counter + 1
    return polylines


def distance_matrix(origin, destinations):
    pass


def closest_item(origin, destinations):
    pass