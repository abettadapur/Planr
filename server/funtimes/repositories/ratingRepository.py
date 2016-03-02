from funtimes.models.entities.change_result import ChangeResult
from funtimes.models.entities.rating import Rating
from funtimes.repositories.baseRepository import BaseRepository


class RatingRepository(BaseRepository):

    def __init__(self):
        super(RatingRepository, self).__init__(Rating)

    def add_or_update(self, item):
        result = ChangeResult()
        result.add_child_result(super(RatingRepository, self).add_or_update(item))
        return result

    def validate(self, entity):
        return ChangeResult()


