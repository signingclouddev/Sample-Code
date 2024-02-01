
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

q1A = b''

# Using base64.b64encode() method
q1A_base64 = b64encode(q1A)

q1A_base64 = str(q1A_base64.decode())

q2A = b''

# Using base64.b64encode() method
q2A_base64 = b64encode(q2A)

q2A_base64 = str(q2A_base64.decode())

#input variable
username =''
data = q1A_base64 + ',' + q2A_base64
authToken = ''
integrationKey = ''
unixTimestamp = str(timedata)
ipAddress = ''
userAgent = '' 
browserFp = ''
supportFido = ''
secretkey = ''
hmacdata = ''


hmacdata = str(username) + str(integrationKey) + str(unixTimestamp) + str(data) + str(supportFido) + str(authToken) + str(ipAddress) + str(userAgent) + str(browserFp) 

print("HMAC Data: "+hmacdata)
message = bytes(hmacdata, 'utf-8')
secretkey = bytes(secretkey, 'utf-8')
centagate_hmac = hmac.new(secretkey, message, hashlib.sha256).hexdigest()
print("HMAC Value: "+centagate_hmac)


auth_data = {
    'username': username,
	'data': data,
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


for url in ['https://<domain_name>/v2/CentagateWS/webresources/auth/authQna']:
    try:

        # If the response was successful, no Exception will be raised
        headers = {'Content-Type': 'application/json'}
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

