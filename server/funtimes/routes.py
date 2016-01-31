import json

from funtimes import db
from funtimes.rest import api as funtimes_api
from funtimes import app
from flask import render_template
from flask_restful import Api
from flask import make_response

api = Api(app)
api.add_resource(funtimes_api.HelloWorld, '/api')


def default(obj):
    """Default JSON serializer."""
    import calendar, datetime
    if isinstance(obj, datetime.datetime):
        if obj.utcoffset() is not None:
            obj = obj - obj.utcoffset()
        return obj.strftime("%Y-%m-%d %H:%M:%S")

    if isinstance(obj, datetime.date):
        return obj.strftime("%Y-%m-%d")
    raise TypeError('Not sure how to serialize %s' % (obj,))


@api.representation('application/json')
def output_json(data, code, headers=None):
    resp = make_response(json.dumps(data, default=default), code)
    resp.headers.extend(headers or {})
    return resp
