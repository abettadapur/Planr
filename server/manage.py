from flask_migrate import Migrate, MigrateCommand
from flask_script import Manager
from funtimes import app
from funtimes import db
from funtimes.models.entities.base import BaseModel
from funtimes.models.entities.item import Item
from funtimes.models.entities.coordinate import Coordinate
from funtimes.models.entities.plan import Plan
from funtimes.models.entities.user import User
from funtimes.models.entities.rating import Rating
from funtimes.models.entities.user_authorization import UserAuthorization
from funtimes.models.entities.yelp_item import YelpItem
from funtimes.models.entities.location import Location
from funtimes.models.entities.yelp_search_filter import YelpSearchFilter
from funtimes.models.entities.yelp_category import YelpCategory

migrate = Migrate(app, db)

manager = Manager(app)
manager.add_command('db', MigrateCommand)

if __name__ == '__main__':
    manager.run()
