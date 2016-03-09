from datetime import datetime

from asq.initiators import query
from funtimes.models.entities.change_result import ChangeResult
from funtimes.models.entities.coordinate import Coordinate

from funtimes.models.entities.plan import Plan
from funtimes.models.entities.plan import PlanShares
from funtimes.repositories.baseRepository import BaseRepository
from funtimes.repositories.itemRepository import ItemRepository
from funtimes.repositories.planShareRepository import PlanShareRepository
from funtimes.repositories.userRepository import UserRepository


class PlanRepository(BaseRepository):
    def __init__(self):
        super(PlanRepository, self).__init__(Plan)

    def add_or_update(self, entity):
        result = ChangeResult()
        # item_repository = ItemRepository()
        # for item in entity.items:
        #     result.add_child_result(item_repository.add_or_update(item))
        result.add_child_result(super(PlanRepository, self).add_or_update(entity))
        return result

    def validate(self, entity):
        return ChangeResult()

    def create_from_dict(self, create_dict, user):
        return Plan.create_from_dict(create_dict, user)

    def get(self, user=None, shared=False, **kwargs):
        if user:
            kwargs['user_id'] = user.id

        plans = super(PlanRepository, self).get(**kwargs)
        if shared:
            plans.extend(user.shared_plans)

        return plans

    def search(self, query, city):
        plans = Plan.query.filter_by(public=True)
        if city:
            plans = plans.filter_by(city=city)
        if query:
            plans = plans.filter(Plan.name.like(query))

        return plans.all()

    def share(self, plan, user_id, permission):
        user_repository = UserRepository()
        plan_share_repository = PlanShareRepository()
        result = ChangeResult()
        user = user_repository.find(user_id)
        if not user:
            result.errors.append("No user with id {0} found".format(user_id))
        else:
            existing = query(PlanShares.query.filter_by(plan_id=plan.id, user_id=user.id).all()).single_or_default(
                default=None)
            if existing:
                existing.permission = permission
                result.add_child_result(plan_share_repository.add_or_update(existing))
            else:
                plan_share = PlanShares()
                plan_share.plan_id = plan.id
                plan_share.user_id = user.id
                plan_share.permission = permission
                result.add_child_result(plan_share_repository.add_or_update(plan_share))
        return result

    def unshare(self, plan, user_id):
        user_repository = UserRepository()
        plan_share_repository = PlanShareRepository()
        result = ChangeResult()
        user = user_repository.find(user_id)
        if not user:
            result.errors.append("No user with id {0} found".format(user_id))
        else:
            existing = query(PlanShares.query.filter_by(plan_id=plan.id, user_id=user.id).all()).single_or_default(
                default=None)
            if existing:
                plan_share_repository.delete(existing.id)
        return result

    def get_shared_user_permission(self, plan_id, user_id):
        shared_user = query(
            PlanShares.query.filter_by(plan_id=plan_id, user_id=user_id).all()).single_or_default(
            default=None
        )
        if not shared_user:
            return None
        return shared_user.permission

    def clear_plan(self, plan):
        item_repository = ItemRepository()
        for item in plan.items:
            item_repository.delete(item.id)

        item_repository.save_changes()
