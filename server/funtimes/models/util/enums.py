from enum import Enum


class StrategyType(Enum):
    distance = 1,
    price = 2,
    popularity = 3,
    first = 4,
    random_first = 5,
    random = 6
