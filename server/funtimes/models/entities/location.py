from funtimes.models.util.coordinate import Coordinate


class Location(object):
    def __init__(self, address, city, postal_code, coordinate):
        self.address = address
        self.city = city
        self.postal_code = postal_code
        self.coordinate = coordinate

    @staticmethod
    def create_from_dict(dict):
        location = Location(
            address=dict['address'],
            city=dict['city'],
            postal_code=dict['postal_code'],
            coordinate=Coordinate(dict['coordinate']['latitude'], dict['coordinate']['longitude'])
        )
        return location
