from funtimes.models.entities.change_result import ChangeResult

from funtimes.models.entities.plan import PlanShares
from funtimes.repositories.baseRepository import BaseRepository


class PlanShareRepository(BaseRepository):

    def __init__(self):
        super(PlanShareRepository, self).__init__(PlanShares)

    def add_or_update(self, entity):
        return super(PlanShareRepository, self).add_or_update(entity)

    def validate(self, entity):
        return ChangeResult()



