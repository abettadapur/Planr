from funtimes import db
from funtimes.models.change_result import ChangeResult
from funtimes.models.itinerary import Itinerary
from funtimes.models.itinerary import ItineraryShares
from funtimes.repositories.baseRepository import BaseRepository
from funtimes.repositories.userRepository import UserRepository


class ItineraryShareRepository(BaseRepository):

    def __init__(self):
        super(ItineraryShareRepository, self).__init__(ItineraryShares)

    def add_or_update(self, entity):
        return super(ItineraryShareRepository, self).add_or_update(entity)

    def validate(self, entity):
        return ChangeResult()



