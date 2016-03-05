from asq.initiators import query
from funtimes.models.entities.change_result import ChangeResult

from funtimes.models.entities.itinerary import Itinerary
from funtimes.models.entities.itinerary import ItineraryShares
from funtimes.repositories.baseRepository import BaseRepository
from funtimes.repositories.itemRepository import ItemRepository
from funtimes.repositories.itineraryShareRepository import ItineraryShareRepository
from funtimes.repositories.userRepository import UserRepository


class ItineraryRepository(BaseRepository):
    def __init__(self):
        super(ItineraryRepository, self).__init__(Itinerary)

    def add_or_update(self, entity):
        result = ChangeResult()
        # item_repository = ItemRepository()
        # for item in entity.items:
        #     result.add_child_result(item_repository.add_or_update(item))
        result.add_child_result(super(ItineraryRepository, self).add_or_update(entity))
        return result

    def validate(self, entity):
        return ChangeResult()

    def create_from_dict(self, create_dict, user):
        return Itinerary.create_from_dict(create_dict, user)

    def get(self, user=None, shared=False, **kwargs):
        if user:
            kwargs['user_id'] = user.id

        itineraries = super(ItineraryRepository, self).get(**kwargs)
        if shared:
            itineraries.extend(user.shared_itineraries)

        return itineraries

    def search(self, query, city):
        itineraries = Itinerary.query.filter_by(public=True)
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
            existing = query(ItineraryShares.query.filter_by(itinerary_id=itinerary.id, user_id=user.id).all()).single_or_default(default=None)
            if existing:
                existing.permission = permission
                result.add_child_result(itinerary_share_repository.add_or_update(existing))
            else:
                itinerary_share = ItineraryShares()
                itinerary_share.itinerary_id = itinerary.id
                itinerary_share.user_id = user.id
                itinerary_share.permission = permission
                result.add_child_result(itinerary_share_repository.add_or_update(itinerary_share))
        return result

    def unshare(self, itinerary, user_id):
        user_repository = UserRepository()
        itinerary_share_repository = ItineraryShareRepository()
        result = ChangeResult()
        user = user_repository.find(user_id)
        if not user:
            result.errors.append("No user with id {0} found".format(user_id))
        else:
            existing = query(ItineraryShares.query.filter_by(itinerary_id=itinerary.id, user_id=user.id).all()).single_or_default(default=None)
            if existing:
                itinerary_share_repository.delete(existing.id)
        return result

    def get_shared_user_permission(self, itinerary_id, user_id):
        shared_user = query(
            ItineraryShares.query.filter_by(itinerary_id=itinerary_id, user_id=user_id).all()).single_or_default(
            default=None
        )
        if not shared_user:
            return None
        return shared_user.permission

    def clear_itinerary(self, itinerary):
        item_repository = ItemRepository()
        for item in itinerary.items:
            item_repository.delete(item.id)

        item_repository.save_changes()



