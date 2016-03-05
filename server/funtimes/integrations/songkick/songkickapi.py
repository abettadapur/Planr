import requests

from funtimes.etc import config

API_HOST = 'http://api.songkick.com/api/3.0/events.json?apikey={api_key}'.format(api_key=config.SONGKICK_KEY)


def search_events(latitude, longitude, date, artist_name=""):
    args = {
        'location': "geo:{lat},{long}".format(lat=latitude, long=longitude),
        'min_date': date.strftime("%Y-%m-%d"),
        'max_date': date.strftime("%Y-%m-%d")
    }
    if artist_name is not "" or artist_name is not None:
        args['artist_name'] = artist_name

    response = requests.get(API_HOST, args)
    results = response.json()

    return results


