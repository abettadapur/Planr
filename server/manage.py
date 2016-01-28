import argparse

from funtimes import db

def create_db():
    db.create_all()
def drop_db():
    db.drop_all()


def main():
    parser = argparse.ArgumentParser(
        description='Manage this Flask application.')
    parser.add_argument(
        'command', help='the name of the command you want to run')
    parser.add_argument(
        '--seedfile', help='the file with data for seeding the database')
    args = parser.parse_args()

    if args.command == 'create_db':
        create_db()

        print("DB created!")
    elif args.command == 'delete_db':
        drop_db()

        print("DB deleted!")
    else:
        raise Exception('Invalid command')

if __name__ == '__main__':
    main()