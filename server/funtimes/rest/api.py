from funtimes import db
from funtimes.repositories.itineraryRepository import ItineraryRepository
from flask_restful import Resource, abort
from flask_restful.reqparse import RequestParser


class HelloWorld(Resource):
    def get(self):
        return {"message": "Welcome to the FunTimes API"}


class ItineraryResource(Resource):
    def __init__(self):
        super(ItineraryResource, self).__init__()
        self.update_parser = RequestParser()

    # Get an itinerary by id
    def get(self, id):
        pass

    # Update an itinerary by id
    def put(self, id):
        pass


class ItineraryListResource(Resource):
    def __init__(self):
        super(ItineraryListResource, self).__init__()
        self.create_parser = RequestParser()

    # List all itineraries
    def get(self):
        pass

    # Create a new itinerary
    def post(self):
        pass
