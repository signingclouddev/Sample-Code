
#Import Packages
import requests
from requests.exceptions import HTTPError
import json
import time
import hmac
import hashlib


# get current timestamp
timedata = int(time.time())

#input variable
username = ''
authResult = 'true'
integrationKey = ''
unixTimestamp = str(timedata)
authToken = ''
ipAddress = ''
userAgent = '' 
browserFp = ''
supportFido = ''
secretkey = ''
hmacdata = ''

#generate hmac value
hmacdata = str(username) + str(authResult) + str(integrationKey) + str(unixTimestamp) + str(authToken) + str(supportFido) + str(ipAddress) + str(userAgent) + str(browserFp)


message = bytes(hmacdata, 'utf-8')
secretkey = bytes(secretkey, 'utf-8')
centagate_hmac = hmac.new(secretkey, message, hashlib.sha256).hexdigest()


auth_data = {
    'username': username,
	'authResult': authResult,
	'integrationKey': integrationKey,
	'unixTimestamp': unixTimestamp,
    'authToken': authToken,
	'ipAddress': ipAddress,
	'supportFido': supportFido,
	'userAgent': userAgent,
	'browserFp': browserFp,
	'hmac': centagate_hmac
}

data_auth = json.dumps(auth_data)


for url in ['https://<domain_name>/v2/CentagateWS/webresources/auth/adaptive']:
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