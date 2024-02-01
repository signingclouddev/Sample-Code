
import requests
from requests.exceptions import HTTPError

import json
import time

import hmac
import hashlib

from base64 import b64encode

# get current timestamp
timedata = int(time.time())

detail_data = b''
# Using base64.b64encode() method
details_d = b64encode(detail_data)
details = str(details_d.decode())

username = ''
challenge = ''
details = details
otp = ''
userAgent = ''
browserFp = ''
secretkey = ''
hmacdata = ''

hmacdata = str(username) + str(challenge) + str(details) + str(otp) + str(userAgent) + str(browserFp)


message = bytes(hmacdata, 'utf-8')
secretkey = bytes(secretkey, 'utf-8')
centagate_hmac = hmac.new(secretkey, message, hashlib.sha256).hexdigest()



auth_data = {
    'username': username,
	'challenge': challenge,
    'details': details,
    'otp': otp,
	'userAgent': userAgent,
	'browserFp': browserFp,
	'hmac': centagate_hmac
}

data_auth = json.dumps(auth_data)


for url in ['https://<domain_name>/v2/CentagateWS/webresources/trans/authorise/{username}']:
    try:

        # If the response was successful, no Exception will be raised
        headers = {'Accept':'application/json', 'Content-Type': 'application/json', 'Host':'<domain_name>'}
        response = requests.post(url, data=data_auth, headers=headers)
        print(response)
        print(response.headers)
        print(response.json())
    except HTTPError as http_err:
        print(f'HTTP error occurred: {http_err}')  
    except Exception as err:
        print(f'Other error occurred: {err}')  
    else:
        print('Success!')