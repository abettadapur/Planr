from funtimes.models.entities.location import Location


class YelpItem(object):
    def __init__(self, id=None, name=None, image_url=None, url=None, phone=None, rating=1, location=None):
        self.id = id
        self.name = name
        self.image_url = image_url
        self.url = url
        self.phone = phone
        self.rating = rating
        self.location = location

    @staticmethod
    def create_from_dict(self, dict):
        item = YelpItem(
            id=dict['id'],
            name=dict['name'],
            image_url=dict['image_url'],
            url=dict['url'],
            phone=dict['phone'],
            rating=dict['rating'],
            location=Location.create_from_dict(dict['location'])
        )
        return item