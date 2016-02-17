from flask_migrate import Migrate, MigrateCommand
from flask_script import Manager
from funtimes import db
from funtimes import app
from funtimes.etc import config
from funtimes.models.itinerary import Itinerary
from funtimes.models.itinerary import ItineraryShares
from funtimes.models.item import Item
from funtimes.models.user import User
from funtimes.models.yelpcategory import YelpCategory
from funtimes.models.yelp_search_filter import YelpSearchFilter



migrate = Migrate(app, db)

manager = Manager(app)
manager.add_command('db', MigrateCommand)

if __name__ == '__main__':
    manager.run()
