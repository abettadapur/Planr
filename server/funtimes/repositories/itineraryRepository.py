from funtimes import db
from funtimes.models.itinerary import Itinerary
from funtimes.repositories.baseRepository import BaseRepository


class ItineraryRepository(BaseRepository):

    def __init__(self):
        super(ItineraryRepository, self).__init__(Itinerary)

    def add_or_update(self, entity):
        super(ItineraryRepository, self).add_or_update(entity)

    def validate(self, entity):
        return True

    def create_from_dict(self, create_dict, user):
        return Itinerary.create_from_dict(create_dict, user)

    def get(self, user_id=None, **kwargs):
        if user_id:
            kwargs['user_id'] = user_id
        return super(ItineraryRepository, self).get(**kwargs)

