from funtimes.models.entities.change_result import ChangeResult

from funtimes.models.entities.itinerary import Itinerary
from funtimes.models.entities.itinerary import ItineraryShares
from funtimes.repositories.baseRepository import BaseRepository
from funtimes.repositories.itineraryShareRepository import ItineraryShareRepository
from funtimes.repositories.userRepository import UserRepository


class ItineraryRepository(BaseRepository):

    def __init__(self):
        super(ItineraryRepository, self).__init__(Itinerary)

    def add_or_update(self, entity):
        return super(ItineraryRepository, self).add_or_update(entity)

    def validate(self, entity):
        return ChangeResult()

    def create_from_dict(self, create_dict, user):
        return Itinerary.create_from_dict(create_dict, user)

    def get(self, user_id=None, **kwargs):
        if user_id:
            kwargs['user_id'] = user_id
        return super(ItineraryRepository, self).get(**kwargs)

    def search(self, query, city):
        itineraries = Itinerary.query().filter_by(public=True)
        if city:
            itineraries = itineraries.filter_by(city=city)
        if query:
            itineraries = itineraries.filter(Itinerary.name.like(query))

        return itineraries

    def share(self, itinerary, user_id, permission):
        user_repository = UserRepository()
        itinerary_share_repository = ItineraryShareRepository()
        result = ChangeResult()
        user = user_repository.find(user_id)
        if not user:
            result.errors.append("No user with id {0} found".format(user_id))
        else:
            itinerary_share = ItineraryShares()
            itinerary_share.itinerary_id = itinerary.id
            itinerary_share.user_id = user.id
            itinerary_share.permission = permission
            result.add_child_result(itinerary_share_repository.add_or_update(itinerary_share))
        return result


    



