import json

from flask import make_response
from flask_restful import Api
from funtimes import app
from funtimes.models.entities.base import BaseModel
from funtimes.rest import api as funtimes_api

api = Api(app)
api.add_resource(funtimes_api.HelloWorld, '/api')
api.add_resource(funtimes_api.AuthResource, '/api/auth')
api.add_resource(funtimes_api.PlanListResource, '/api/plans')
api.add_resource(funtimes_api.PlanResource, '/api/plans/<int:id>')
api.add_resource(funtimes_api.PlanRandomizeResource, '/api/plans/<int:id>/randomize')
api.add_resource(funtimes_api.PlanSearchResource, '/api/plans/search')
api.add_resource(funtimes_api.PlanShareResource, '/api/plans/<int:plan_id>/share')
api.add_resource(funtimes_api.ItemListResource, '/api/plans/<int:plan_id>/items')
api.add_resource(funtimes_api.ItemResource, '/api/plans/<int:plan_id>/items/<int:item_id>')
api.add_resource(funtimes_api.ItemSearchResource, '/api/search')
api.add_resource(funtimes_api.FriendsResource, '/api/registered_friends')
api.add_resource(funtimes_api.CategoryResource, '/api/categories')
api.add_resource(funtimes_api.PlanGenerateResource, '/api/plans/generate')


def default(obj):
    """Default JSON serializer."""
    import datetime
    if isinstance(obj, datetime.datetime):
        if obj.utcoffset() is not None:
            obj = obj - obj.utcoffset()
        return obj.strftime("%Y-%m-%d %H:%M:%S %z")

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
