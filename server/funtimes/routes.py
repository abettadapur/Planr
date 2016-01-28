from funtimes import db
from funtimes.rest import api as funtimes_api
from funtimes import app
from flask import render_template
from flask_restful import Api

api = Api(app)
api.add_resource(funtimes_api.HelloWorld, '/api')