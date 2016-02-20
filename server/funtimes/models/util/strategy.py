from funtimes.models.util.enums import StrategyType
from abc import ABCMeta, abstractmethod
import random


class BaseStrategy(metaclass=ABCMeta):
    @abstractmethod
    def run_strategy(self, items):
        pass


class DistanceStrategy(BaseStrategy):
    def __init__(self):
        self.type = StrategyType.distance

    def run_strategy(self, items):
        pass


class PriceStrategy(BaseStrategy):
    def __init__(self):
        self.type = StrategyType.price

    def run_strategy(self, items):
        pass


class PopularityStrategy(BaseStrategy):
    def __init__(self):
        self.type = StrategyType.popularity

    def run_strategy(self, items):
        pass


class FirstStrategy(BaseStrategy):
    def __init__(self):
        self.type = StrategyType.first

    def run_strategy(self, items):
        return items[0]


class FirstRandomStrategy(BaseStrategy):
    def __init__(self):
        self.type = StrategyType.random_first

    def run_strategy(self, items):
        return items[random.randrange(8 if len(items) >= 8 else len(items))]
