import oauth2
import requests

from funtimes.etc import config
from requests.exceptions import HTTPError
from urllib.parse import quote

API_HOST = "api.yelp.com"
SEARCH_PATH = "/v2/search/"
BUSINESS_PATH = "/v2/business/"

CONSUMER_KEY = config.CONSUMER_KEY
CONSUMER_SECRET = config.CONSUMER_SECRET
API_TOKEN = config.API_TOKEN
API_SECRET = config.API_SECRET


def _make_request(path, url_params=None):
    url_params = url_params or {}

    url = "http://{host}{path}?".format(host=API_HOST, path=quote(path))
    consumer = oauth2.Consumer(CONSUMER_KEY, CONSUMER_SECRET)
    oauth_request = oauth2.Request(method='GET', url=url, parameters=url_params)

    oauth_request.update(
        {
            'oauth_nonce': oauth2.generate_nonce(),
            'oauth_timestamp': oauth2.generate_timestamp(),
            'oauth_token': API_TOKEN,
            'oauth_consumer_key': CONSUMER_KEY
        }
    )

    token = oauth2.Token(API_TOKEN, API_SECRET)
    oauth_request.sign_request(oauth2.SignatureMethod_HMAC_SHA1(), consumer, token)
    signed_url = oauth_request.to_url()

    try:
        response = requests.get(signed_url)
        response.raise_for_status()
        return response.json()
    except HTTPError as error:
        print(error)


def search(query, location, category_filters, **kwargs):
    url_params = {
        'term': query.replace(' ', '+') if query else "",
        'location': location.replace(' ', '+'),
        'category_filter': ','.join(category_filters)}
    url_params.update(kwargs)

    if 'radius_filters' not in url_params:
        url_params['radius_filters'] = 150000  # default radius is set to 15Km

    businesses = _make_request(SEARCH_PATH, url_params)
    return businesses['businesses']  # Sara was here yolo and ACTUALLY wrote all this code

def get_business(yelp_id):
    api_entry = _make_request(BUSINESS_PATH+yelp_id)
    return api_entry

