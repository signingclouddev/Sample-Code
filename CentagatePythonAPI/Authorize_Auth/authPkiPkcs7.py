
import requests
from requests.exceptions import HTTPError

import json
import time

import hmac
import hashlib

import base64

from base64 import b64encode


# get current timestamp
timedata = int(time.time())


#input variable
username =''
signature = ''
algorithm = ''
plainText = ''
authToken = ''
integrationKey = ''
unixTimestamp = str(timedata)
ipAddress = ''
userAgent = '' 
browserFp = ''
supportFido = ''
secretkey = ''
hmacdata = ''


hmacdata = str(username) + str(signature) + str(algorithm) + str(plainText) + str(authToken) + str(integrationKey) + str(unixTimestamp) + str(supportFido) + str(ipAddress) + str(userAgent) + str(browserFp) 


message = bytes(hmacdata, 'utf-8')
secretkey = bytes(secretkey, 'utf-8')
centagate_hmac = hmac.new(secretkey, message, hashlib.sha256).hexdigest()


auth_data = {
    'username': username,
	'signature': signature,
    'algorithm': algorithm,
	'plainText': plainText,
    'authToken': authToken,
	'integrationKey': integrationKey,
	'unixTimestamp': unixTimestamp,
	'ipAddress': ipAddress,
    'userAgent': userAgent,
    'browserFp': browserFp,
	'supportFido': supportFido,
	'hmac': centagate_hmac
}

data_auth = json.dumps(auth_data)


for url in ['https://<domain_name>/v2/CentagateWS/webresources/auth/authPkiPkcs7']:
    try:

        # If the response was successful, no Exception will be raised
        headers = {'Accept':'application/json', 'Content-Type': 'application/json', 'Host':'<domain_name>'}
        response = requests.post(url, data=data_auth)
        print(response)
        print(response.headers)
        authToken_data = json.dumps(response.json())
        print(authToken_data)
    except HTTPError as http_err:
        print(f'HTTP error occurred: {http_err}')  
    except Exception as err:
        print(f'Other error occurred: {err}')  
    else:
        print('Success!')

