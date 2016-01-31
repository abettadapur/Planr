from funtimes import db
from funtimes.models.itinerary import Itinerary
from funtimes.repositories.baseRepository import BaseRepository


class ItineraryRepository(BaseRepository):

    def __init__(self):
        super(ItineraryRepository, self).__init__(Itinerary)

    def validate(self, entity):
        return True

