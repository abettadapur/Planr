import facebook
import requests
import json

authToken = 'CAACEdEose0cBAP6o2ZC3ZAwe9LoW4zIRDoyuSA0bQZAKMMAlbkVHahns209CRGmGYqcMOQz8dzp0JFbBNfJea2ZBdD0mN6YnBwNVgPZBPRKtLSAq72vbF1lBrmQhjPNHwumdbk5ULahvySk1KSxNLeNNNDf6BLcRsT0n8q5XRyx7bk5XGWw36Kg9LJmGFbcbNC0iB80qBKwZDZD'
api = facebook.GraphAPI(access_token=authToken)

response = requests.post('http://funtimes.bettadapur.com:5000/api/auth', data=json.dumps({"token": authToken, "user_id": "10206911819539644"}), headers={"Content-Type": "application/json"})
print(response)
print(response.text)

