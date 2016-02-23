from funtimes.models.entities.change_result import ChangeResult

from funtimes.models.entities.city import City
from funtimes.repositories.baseRepository import BaseRepository


class CityRepository(BaseRepository):

    def __init__(self):
        super(CityRepository, self).__init__(City)

    def validate(self, entity):
        return ChangeResult()

    def search(self, name=None, state=None, zip_code=None):
        if name is not None and state is None and ',' in name:
            parts = name.split(',')
            name = parts[0]
            state = parts[1].strip()
        cities = City.query
        if name:
            cities = cities.filter(City.city.contains(name))
        if state:
            cities = cities.filter_by(state=state)
        if zip_code:
            cities = cities.filter_by(zip=zip_code)

        return cities.all()

