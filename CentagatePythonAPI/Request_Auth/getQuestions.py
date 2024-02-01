
import requests
from requests.exceptions import HTTPError

import json
import time

import hmac
import hashlib


# get current timestamp
timedata = int(time.time())

username = ''
authToken = ''
integrationKey = ''
unixTimestamp = str(timedata)
ipAddress = ''
userAgent = '' 
browserFp = ''
supportFido = ''
secretkey = ''
hmacdata = ''

hmacdata = str(username) + str(authToken) +  str(integrationKey) + str(unixTimestamp) + str(supportFido) + str(ipAddress) + str(userAgent) + str(browserFp)


message = bytes(hmacdata, 'utf-8')
secretkey = bytes(secretkey, 'utf-8')
centagate_hmac = hmac.new(secretkey, message, hashlib.sha256).hexdigest()


auth_data = {
    'username': username,
    'authToken': authToken,
	'integrationKey': integrationKey,
	'unixTimestamp': unixTimestamp,
	'ipAddress': ipAddress,
	'supportFido': supportFido,
	'userAgent': userAgent,
	'browserFp': browserFp,
	'hmac': centagate_hmac
}

data_auth = json.dumps(auth_data)


for url in ['https://<domain_name>/v2/CentagateWS/webresources/req/kba/getQuestions']:
    try:

        # If the response was successful, no Exception will be raised
        #response = requests.get(url, data=ver_data, verify=True)
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