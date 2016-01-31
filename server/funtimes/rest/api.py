from funtimes import db
from funtimes.repositories.itineraryRepository import ItineraryRepository
from flask_restful import Resource, reqparse, abort


class HelloWorld(Resource):
    def get(self):
        repository = ItineraryRepository()
        itinerary = repository.find(1)
        return itinerary.as_dict()

