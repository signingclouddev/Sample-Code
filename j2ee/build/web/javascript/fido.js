var fidoResponse;
var fidoErrorCode = { OTHER_ERROR : 1 , BAD_REQUEST : 2 , CONFIGURATION_UNSUPPORTED : 3 , DEVICE_INELIGIBLE : 4 , TIMEOUT : 5 };
//var fidoReturnMessages = [ "OK" , "OTHER_ERROR" , "BAD_REQUEST" , "CONFIGURATION_UNSUPPORTED" , "DEVICE_INELIGIBLE" , "TIMEOUT" ] ;
//
//function register ( version , challenge , appId , callback )
//{
//    var regReq = [{
//        version: version,
//        challenge: challenge,
//        appId: appId
//    }] ;
//
//    u2f.register ( regReq , [] , callback ) ;
//}
//
//function sign ( version , challenge , keyHandle , appId , callback )
//{
//    var signReq = [{
//        version: version,
//        challenge: challenge,
//        keyHandle: keyHandle,
//        appId: appId
//    }] ;
//
//    u2f.sign ( signReq , callback ) ;
//}

function deleteCredential(centoken,username) {

    $.post( APIUrl + '/webauthn/delete/credential/' + username, JSON.stringify(
            {
                "credentialId": username,
                "cenToken": centoken,
                "username" : username
            }) 
    , null, 'json').done(function(data) {
                codeValue = JSON.parse(data.code);
                if(codeValue === 0) {
                    console.log(data.object);
                    console.log(JSON.parse(data.object));
                    response = JSON.parse(data.object);  
                    console.log(response.status);
                    if ( response.status !== "ok" ) {
                        msgbox('1', returnInfo.errorMsg);
                    } else {
                        unregister("5");
                    }
                }
                else {
                    msgbox('1', returnInfo.errorMsg);
                }
    });
}


function fetchCredentials(centoken, username) {
JSON.stringify({"username": username,"cenToken": centoken})
	$.post( APIUrl + '/webauthn/registered/keys/' + username, 
            JSON.stringify({"username": username,"cenToken": centoken}
        ), null, 'json').done(function(data) {
                
                codeValue = JSON.parse(data.code);
                if(codeValue === 0) {
                    preRegisterFidoToken(JSON.parse(data.object));   
                    msgbox('2', 'User mobile device register succesfully');
                }
                else {
                    msgbox('1', returnInfo.errorMsg);
                }
	});
}

function makeCredentialOptions(username, displayname, centoken) {
	return new Promise(function(resolve, reject) {
		$.ajax({
			type : 'POST',
			url : APIUrl + '/webauthn/attestation/options/' +username,
			data : JSON.stringify({
				username : username,
				displayName : displayname,
                                cenToken : centoken        
			}),
                        contentType: "application/json; charset=utf-8",
			dataType : 'json',
			success : function(data) {
                                codeValue = JSON.parse(data.code);
                                if(codeValue === 0) {
                                    loginUsername = username;
                                    resolve(makeCredential( JSON.parse(data.object), centoken));
                                }
                                else {
                                    msgbox('1', data.message);
                                }
			},
			error : function(xhr) {
				reject("Error message: " + xhr.status + ", " + xhr.statusText);
			}
		});
	});
}

//handle response data and data exchange with fido token
function makeCredential(options, centoken) {
	return new Promise(function(resolve, reject) {
		var returnInfo = {};
		if (options.status != "ok") {
			returnInfo.errorMsg = options.errorMessage;
			resolve(returnInfo);
		}
		
		var makeCredentialOptions = {};
		// Required parameters
		makeCredentialOptions.rp = options.rp;
		makeCredentialOptions.user = options.user;
		makeCredentialOptions.user.id = base64url.decode(options.user.id);
		makeCredentialOptions.challenge = base64url.decode(options.challenge);
		makeCredentialOptions.pubKeyCredParams = options.pubKeyCredParams;

		// Optional parameters
		if ('timeout' in options) {
			makeCredentialOptions.timeout = options.timeout;
		}
		if ('excludeCredentials' in options) {
			makeCredentialOptions.excludeCredentials = credentialListConversion(options.excludeCredentials);
		}
		if ('authenticatorSelection' in options) {
			makeCredentialOptions.authenticatorSelection = options.authenticatorSelection;
		}
		if ('attestation' in options) {
			makeCredentialOptions.attestation = options.attestation;
		}
		if ('extensions' in options) {
			makeCredentialOptions.extensions = options.extensions;
		}
//		if(consolePrint){ //console log
//		    (makeCredentialOptions);
//		}

		// check the browser support navigator.credentials.create
		if (typeof navigator.credentials.create !== "function") {
			returnInfo.errorMsg = "Browser does not support credential creation";
			resolve(returnInfo);
		}
		
		navigator.credentials.create({
			"publicKey" : makeCredentialOptions
		}).then(function(attestation) {
			var publicKeyCredential = {};
			if ('id' in attestation) {
				publicKeyCredential.id = attestation.id;
			}
			if ('type' in attestation) {
				publicKeyCredential.type = attestation.type;
			}
			if ('rawId' in attestation) {
				publicKeyCredential.rawId = attestation.rawId;
			}
			if ('response' in attestation) {
				var response = {};
				response.clientDataJSON = base64url.encode(attestation.response.clientDataJSON);
				response.attestationObject = base64url.encode(attestation.response.attestationObject);
				publicKeyCredential.response = response;
				publicKeyCredential.username = options.user.name;
				resolve(makCredentialResult(publicKeyCredential, centoken));
			} else {
				returnInfo.errorMsg = "Make Credential response lacking 'response' attribute";
				resolve(returnInfo);
			}
		}).catch(function(err) {
			var errMesg = err.message;
			if (errMesg != null && errMesg != "") {
				returnInfo.errorMsg = errMesg;
				resolve(returnInfo);
			} else {
			    returnInfo.errorMsg = "unknown error";
                resolve(returnInfo);
			}
		});
	});
}

// complete registration
function makCredentialResult(publicKeyCredential, centoken) {
	return new Promise(function(resolve, reject) {
		var returnInfo = {};
		$.post(APIUrl + '/webauthn/attestation/result/'+ loginUsername + "/" + centoken, JSON.stringify(publicKeyCredential)
			, null, 'json').done(function(data) {
                        
                        codeValue = JSON.parse(data.code);
                        if(codeValue === 0)  {
                            parameters = JSON.parse(data.object);    
                            if (parameters.status != "ok") {
                                    returnInfo.errorMsg = parameters.errorMessage;
                                    resolve(returnInfo);
                            }
//                            if(consolePrint){ // console log
//                                (parameters);
//                            }
                            if ('success' in parameters && 'message' in parameters) {
                                    returnInfo.successMsg = parameters.message;
                                    resolve(returnInfo);
                            }
                        }
                        else {
                            msgbox('1', data.message);
                        }
                });
	});
}


function verifyRegistration(fidoReturnObject) {

        var username = document.getElementById('userid').value;
        
        console.log("username = "+username);
        parameters = JSON.parse(fidoReturnObject);   
        
        console.log(parameters);
	getAssertion(parameters,username).then(function(returnInfo) {
                console.log(returnInfo);
		if (returnInfo.successMsg !== undefined) {
			//fido认证成功
//                        var fidoLink = $( "#loginform\\:fidoLinkHidden" ) ;
//                        if ( fidoLink )
//                            fidoLink.click ( ) ;
                        
		} else {
			console.log("login failed");
                        window.document.FORM.action = baseURL + "/LoginServlet?MODE=LOGOUT&returnMsg=fidoLoginFailed";
                        window.document.FORM.submit();
		}
	});

}

function getAssertion(parameters,username) {
	return new Promise(function(resolve, reject) { 
				resolve(analysisAssertion(parameters, username));
	});
}

function analysisAssertion(parameters, username) {
	return new Promise(function(resolve, reject) {
		var returnInfo = {};
		if (parameters.status != "ok") {
			returnInfo.errorMsg = parameters.errorMessage;
			resolve(returnInfo);
		}
		var requestOptions = {};
		requestOptions.challenge = base64url.decode(parameters.challenge);
		if ('timeout' in parameters) {
			requestOptions.timeout = parameters.timeout;
		}
		if ('rpId' in parameters) {
			requestOptions.rpId = parameters.rpId;
		}
		if ('allowCredentials' in parameters) {
			requestOptions.allowCredentials = credentialListConversion(parameters.allowCredentials);
		}
//		if(consolePrint){ // console log
//		    (requestOptions);
//		}
                console.log(requestOptions);
		if (typeof navigator.credentials.get !== "function") {
			returnInfo.errorMsg = "Browser does not support credential lookup";
			resolve(returnInfo);
		}

		navigator.credentials.get({
			"publicKey" : requestOptions
		}).then(function(assertion) {
			var publicKeyCredential = {};
			if ('id' in assertion) {
				publicKeyCredential.id = assertion.id;
			}
			if ('type' in assertion) {
				publicKeyCredential.type = assertion.type;
			}
			if ('rawId' in assertion) {
				publicKeyCredential.rawId = assertion.rawId;
			}
			if ('response' in assertion) {
				var response = {};
				response.clientDataJSON = base64url.encode(assertion.response.clientDataJSON);
				response.authenticatorData = base64url.encode(assertion.response.authenticatorData);
				response.signature = base64url.encode(assertion.response.signature);
				response.userHandle = base64url.encode(assertion.response.userHandle);
				publicKeyCredential.response = response;
				publicKeyCredential.username = username;
				//resolve(finishAssertion(publicKeyCredential));
                                console.log(publicKeyCredential);
                                document.getElementById('fidoPublicKeyCredential').value = JSON.stringify(publicKeyCredential);
//                               
//                                var fidoLink = $( "#btnAuthLogin" ) ;
//                                if ( fidoLink )
//                                    fidoLink.click ( ) ;
                                 
                                if (document.getElementById('loginSession').value !== '1') {
                                
                                    window.document.FORM.action = baseURL + "/LoginServlet?MODE=9";
                                    window.document.FORM.submit();
                                }
                                else {
                                    getInvokeTAC('9',username,'',parameters.challenge);
                                }
                                
                                
			}
		}).catch(function(err) {
                        console.log(err)
			var errMesg = err.message;
			if (errMesg != null && errMesg != "") {
				returnInfo.errorMsg = errMesg;
				resolve(returnInfo);
			}
		});
	});
}


function credentialListConversion(list) {
	var result = [];
	for (var i = 0; i < list.length; i++) {
		var credential = {};
		credential.type = list[i].type;
		credential.id = base64url.decode(list[i].id);
		if ('transports' in list) {
			credential.transports = list.transports;
		}
		result.push(credential);
	}
	return result;
}