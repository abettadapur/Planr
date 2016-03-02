import json

from flask import make_response
from flask_restful import Api
from funtimes import app
from funtimes.models.entities.base import BaseModel
from funtimes.rest import api as funtimes_api

api = Api(app)
api.add_resource(funtimes_api.HelloWorld, '/api')
api.add_resource(funtimes_api.AuthResource, '/api/auth')
api.add_resource(funtimes_api.CityResource, '/api/cities')
api.add_resource(funtimes_api.ItineraryListResource, '/api/itineraries')
api.add_resource(funtimes_api.ItineraryResource, '/api/itineraries/<int:id>')
api.add_resource(funtimes_api.ItineraryRandomizeResource, '/api/itineraries/<int:id>/randomize')
api.add_resource(funtimes_api.ItinerarySearchResource, '/api/itineraries/search')
api.add_resource(funtimes_api.ItineraryShareResource, '/api/itineraries/<int:itinerary_id>/share')
api.add_resource(funtimes_api.ItemListResource, '/api/itineraries/<int:itinerary_id>/items')
api.add_resource(funtimes_api.ItemResource, '/api/itineraries/<int:itinerary_id>/items/<int:item_id>')
api.add_resource(funtimes_api.FriendsResource, '/api/registered_friends')


def default(obj):
    """Default JSON serializer."""
    import datetime
    if isinstance(obj, datetime.datetime):
        if obj.utcoffset() is not None:
            obj = obj - obj.utcoffset()
        return obj.strftime("%Y-%m-%d %H:%M:%S")

    if isinstance(obj, datetime.date):
        return obj.strftime("%Y-%m-%d")
    if isinstance(obj, datetime.time):
        return obj.strftime("%H:%M:%S")
    if isinstance(obj, BaseModel):
        return obj.as_dict()
    raise TypeError('Not sure how to serialize %s' % (obj,))


@api.representation('application/json')
def output_json(data, code, headers=None):
    resp = make_response(json.dumps(data, default=default), code)
    resp.headers.extend(headers or {})
    return resp
