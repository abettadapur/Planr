from funtimes import db
from flask_restful import Resource, reqparse, abort


class HelloWorld(Resource):
	def get(self):
		return {"hello":"world"}