package loginjsf;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import java.time.Instant;
import java.util.HashMap;

import java.security.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import java.util.Random;

import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.util.Base64;

//A class that including all Centagate API that used in this project
public class CentagateAPI {

    //Username and Password Authentication API, used to retrieve admin's authtoken and secretcode
    public HashMap<String, String> adminApiAuthentication() throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        String username = "winter";
        String password = "winter123";
        String integrationKey = "0fe831638cc01e3f5a6e8e9340dac280597d5e527f79648a065ea96769422fa4";
        
        Long timeStamp = Instant.now().getEpochSecond();
        String unixTimestamp = Long.toString(timeStamp);

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://cloud.centagate.com/v2/CentagateWS/webresources/auth/authBasic");
        
        Gson gson = new Gson();

        String hmac = convertHmacSha256("pGY9Jua6fpgm",username + password + integrationKey + unixTimestamp);
        
        HashMap<String, String> map = new HashMap<String, String>();
	    map.put("username", username);
	    map.put("password", password);
	    map.put("integrationKey", integrationKey);
        map.put("unixTimestamp", unixTimestamp);
	    map.put("ipAddress", ""); 
	    map.put("userAgent", ""); 
	    map.put("browserFp", ""); 
	    map.put("supportFido", ""); 
	    map.put("hmac", hmac); 

        String json = gson.toJson(map);

        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        Response response = target.request(mediaType).post(Entity.entity(json, mediaType));
        
        int statusCode = response.getStatus();
        String retJson = response.readEntity(String.class);
        
        //HashMap for return use
        HashMap<String, String> returnMap = new HashMap<String, String>();

        if(statusCode == 200 && !retJson.equals("")) {

            //To get the overall JSON sent by server and convert into Hash Map
            HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

            String code = returnData.get("code").toString();
            String message = returnData.get("message").toString();
            
            if(Integer.parseInt(code) != 0) {
                returnMap.put("code", code);
                returnMap.put("message", message);
            }else {
                String object = returnData.get("object").toString();
        
                //Parse into the Object section of JSON HashMap to get the specific property
                HashMap<String, Object> returnObj = (HashMap<String, Object>) gson.fromJson(object, HashMap.class);
        
                String authToken = returnObj.get("authToken").toString();
                String secretCode = returnObj.get("secretCode").toString();

                returnMap.put("code", code);
                returnMap.put("message", message);
                returnMap.put("authToken", authToken);
                returnMap.put("secretCode", secretCode);
            }

        }else {
            returnMap.put("code", Integer.toString(statusCode));
        }
       
        client.close();
        return returnMap;
    }

    //User Registration API, used to register a new user in Centagate Console. 
    public int userApiRegistration(String firstname, String lastname, String username, String email) throws NoSuchAlgorithmException, 
            InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        int returnCode = 0;
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://cloud.centagate.com/CentagateWS/webresources/user/registerUserActivate/winter");
    
        String secretCode = "";
        String authToken = "";
        String firstName = firstname;
        String lastName = lastname;
        String userName = username;
        String userApp = "simpleweb";

        Random random = new Random();
        int min = 10000;
        int max = 100000;

        String userUniqueId = "UID" + Integer.toString(random.ints(min, max).findFirst().getAsInt());
        String userClientId = "U" + Integer.toString(random.ints(min, max).findFirst().getAsInt());
        String userEmail = email;
        String roles = "3";
        String userGroup = "usertest";

        HashMap<String, String> map = new HashMap();
        map = adminApiAuthentication();

        String code = map.get("code");
        if(Integer.parseInt(code) == 0) {
            authToken = map.get("authToken");
            secretCode = map.get("secretCode");
        }else {
            returnCode = -1;
        }

        Gson gson = new Gson();
        
        String cenToken = convertHmacSha256(secretCode, "winter" + authToken);

        HashMap<String, String> useMap = new HashMap<String, String>();
        useMap.put("firstName", firstName);
        useMap.put("lastName", lastName);
        useMap.put("username", userName);
        useMap.put("userApp", userApp);
        useMap.put("userUniqueId", userUniqueId);
        useMap.put("userClientId", userClientId);
        useMap.put("userEmail", userEmail);
        useMap.put("roles", roles);
        useMap.put("userGroup", userGroup);
        useMap.put("cenToken", cenToken);
        
        String json = gson.toJson(useMap);

        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        Response response = target.request(mediaType).put(Entity.entity(json, mediaType));
        
        int statusCode = response.getStatus();
        String retJson = response.readEntity(String.class);

        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);
        returnCode = Integer.parseInt(returnData.get("code").toString());
    
        client.close();
        return returnCode;
    }

    //Convert secretkey and the required parameter into hmac 256
    public String convertHmacSha256(String secretKey, String params) throws NoSuchAlgorithmException, 
            InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
	    try
	    {
		    final SecretKeySpec secret_key = new SecretKeySpec ( StringUtils.getBytesUtf8 ( secretKey ) , "HmacSHA256" );
		    final Mac mac = Mac.getInstance ( "HmacSHA256" );
		    mac.init ( secret_key );
		    final byte[] bytes = mac.doFinal ( StringUtils.getBytesUtf8 ( params ) );
		    return Hex.encodeHexString ( bytes );
	    }
	    catch ( NoSuchAlgorithmException e )
	    {
		    throw new NoSuchAlgorithmException ( e );
        }
	    catch ( InvalidKeyException e )
	    {
		    throw new InvalidKeyException ( e );
	    }
	    catch ( IllegalStateException e )
	    {
		    throw new IllegalStateException ( e );
	    }
	    catch ( Exception e )
	    {
		    throw new Exception ( e );
	    }
    }

    //Request Question List API, used to get all the questions from Centagate Console
    public HashMap<String, String> getQuestionList() {
        HashMap<String, String> questions = new HashMap();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://cloud.centagate.com/CentagateWS/webresources/security/getQuestion");
        
        Gson gson = new Gson();

        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        Response response = target.request(mediaType).get();
        int statusCode = response.getStatus();
        String retJson = response.readEntity(String.class);

        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);
       
        client.close();

        List<Map<String, String>> questionList = (List<Map<String,String>>) returnData.get("object");

        for(Map<String, String> questionMap : questionList) {
            String id = questionMap.get("id");
            String question = questionMap.get("question");
            questions.put(id, question);
        }

        return questions;
    }

    //Token Registration (One Time Pin) API, used to bind a device in Centagate Application, return a QR code string and passcode
    public HashMap<String, String> getUserQR(String username) throws NoSuchAlgorithmException, 
            InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        HashMap<String, String> resultMap = new HashMap();
        HashMap<String, String> hMap = adminApiAuthentication();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://cloud.centagate.com/CentagateWS/webresources/device/register/onetimepin/winter");

        Gson gson = new Gson();

        String authToken = hMap.get("authToken");
        String adminUsername = "winter";
        String secretCode = hMap.get("secretCode");
        String cenToken = convertHmacSha256(secretCode, adminUsername + authToken);
        
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("status", "2");
        map.put("validity", "");
        map.put("cenToken", cenToken);

        String json = gson.toJson(map);

        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        Response response = target.request(mediaType).put(Entity.entity(json, mediaType));
        
        int statusCode = response.getStatus();
        String retJson = response.readEntity(String.class);

        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
	    String message = returnData.get("message").toString();
	    String object = returnData.get("object").toString();

        if(Integer.parseInt(code) != 0) {
            resultMap.put("code", code);
            resultMap.put("message", message);
        }else {
            HashMap<String, Object> returnObj = (HashMap<String, Object>) gson.fromJson(object, HashMap.class);
        
            String qrString = returnObj.get("qr").toString();
            String passcode = returnObj.get("passcode").toString();

            resultMap.put("code", code);
            resultMap.put("message", message);
            resultMap.put("qr", qrString);
            resultMap.put("passcode", passcode);
        }

        client.close();

        return resultMap;
    }

    //Username and Password Authentication API, used by user to perform password login and retrieve available authentication methods
    public HashMap<String, String> userApiAuthentication(String user, String pass) throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        String username = user;
        String password = pass;
        String integrationKey = "0fe831638cc01e3f5a6e8e9340dac280597d5e527f79648a065ea96769422fa4";
        
        Long timeStamp = Instant.now().getEpochSecond();
        String unixTimestamp = Long.toString(timeStamp);

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://cloud.centagate.com/v2/CentagateWS/webresources/auth/authBasic");
        
        Gson gson = new Gson();

        String hmac = convertHmacSha256("pGY9Jua6fpgm",username + password + integrationKey + unixTimestamp);
        
        HashMap<String, String> map = new HashMap<String, String>();
	    map.put("username", username);
	    map.put("password", password);
	    map.put("integrationKey", integrationKey);
        map.put("unixTimestamp", unixTimestamp);
	    map.put("ipAddress", ""); 
	    map.put("userAgent", ""); 
	    map.put("browserFp", ""); 
	    map.put("supportFido", ""); 
	    map.put("hmac", hmac); 

        String json = gson.toJson(map);

        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        Response response = target.request(mediaType).post(Entity.entity(json, mediaType));
        
        int statusCode = response.getStatus();
        String retJson = response.readEntity(String.class);
        
        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
	    String message = returnData.get("message").toString();
	    String object = returnData.get("object").toString();
        
        HashMap<String, String> returnMap = new HashMap();

        if(Integer.parseInt(code) == 0) {
            HashMap<String, Object> returnObj = (HashMap<String, Object>) gson.fromJson(object, HashMap.class);
            String authMethods = returnObj.get("authMethods").toString();
            
            returnMap.put("code", code);
            returnMap.put("authMethods", authMethods);
        }else {
            returnMap.put("code", code);
        }
       
        client.close();

        return returnMap;
    }

    //Update User Password API, used for update the user password in Centagate Console 
    public int changeUserPassword(String username, String userpassword, String usernewpassword) {

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://cloud.centagate.com/CentagateWS/webresources/password/updatesimple/" + username);

        String secretKey = "pGY9Jua6fpgm";
        String password = userpassword;
        String newPassword = usernewpassword;

        String encryptedPassword = encryptPassword(password, secretKey);
        String encryptedNewPassword = encryptPassword(newPassword, secretKey);

        Gson gson = new Gson();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("password", encryptedPassword);
	    map.put("newPassword", encryptedNewPassword);
	    map.put("integrationKey","0fe831638cc01e3f5a6e8e9340dac280597d5e527f79648a065ea96769422fa4");

        String json = gson.toJson(map);

        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        Response response = target.request(mediaType).put(Entity.entity(json, mediaType));

        int statusCode = response.getStatus();
        String retJson = response.readEntity(String.class);

        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
	    String message = returnData.get("message").toString();
	    String object = returnData.get("object").toString();

        client.close();

        return Integer.parseInt(code);

    }

    //Encrypt the password with AES using The Secret Key as Encryption Key And encoded with Base64 format
    private String encryptPassword(String content, String key) {    	
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] aesKey = digest.digest(key.getBytes());
            SecretKey secretKey = new SecretKeySpec(aesKey,"AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    //Token Registration (PKI) API, used to register user's phone number in Centagate Console (Can be used for the SMS Token)
    public int userTokenRegistration(String username, String phonenumber) throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        HashMap<String, String> hMap = adminApiAuthentication();
        
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://cloud.centagate.com/CentagateWS/webresources/token/registerActiveToken/winter");

        Gson gson = new Gson();

        String authToken = hMap.get("authToken");
        String adminUsername = "winter";
        String secretCode = hMap.get("secretCode");
        String cenToken = convertHmacSha256(secretCode, adminUsername + authToken);

        HashMap<String, String> map = new HashMap<String, String>();
	    map.put("username", username);
	    map.put("tokenSn", phonenumber);
	    map.put("tokenType", "1");
	    map.put("cenToken", cenToken);  

        String json = gson.toJson(map);

        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        Response response = target.request(mediaType).put(Entity.entity(json, mediaType));

        int statusCode = response.getStatus();
        String retJson = response.readEntity(String.class);

        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
	    String message = returnData.get("message").toString();
	    String object = returnData.get("object").toString();

        client.close();

        return Integer.parseInt(code);
    }

    //Adaptive Authentication API, used to check for the user available Authentication Method and the User Information
    public HashMap<String, String> checkUserAvailableAuthMethod(String username) throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {   
        HashMap<String, String> returnMap = new HashMap();

        Long timeStamp = Instant.now().getEpochSecond();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://cloud.centagate.com/v2/CentagateWS/webresources/auth/adaptive");

        Gson gson = new Gson();

        String secretkey = "pGY9Jua6fpgm";
        String authResult = "true";
        String integrationKey = "0fe831638cc01e3f5a6e8e9340dac280597d5e527f79648a065ea96769422fa4";
        String unixTimestamp = Long.toString(timeStamp);

        String hmac = convertHmacSha256(secretkey, username + authResult + integrationKey + unixTimestamp);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("authResult", authResult);
        map.put("integrationKey", integrationKey);
	    map.put("unixTimestamp", unixTimestamp);
	    map.put("authToken", "");
	    map.put("ipAddress", ""); 
	    map.put("userAgent", ""); 
	    map.put("browserFp", ""); 
	    map.put("supportFido", ""); 
	    map.put("hmac", hmac); 

        String json = gson.toJson(map);

        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        Response response = target.request(mediaType).post(Entity.entity(json, mediaType));

        int statusCode = response.getStatus();
        String retJson = response.readEntity(String.class);

        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);
        
        String code = returnData.get("code").toString();
	    String message = returnData.get("message").toString();
	    String object = returnData.get("object").toString();

        client.close();

        if(Integer.parseInt(code) == 0) {
            HashMap<String, Object> returnObj = (HashMap<String, Object>) gson.fromJson(object, HashMap.class);

            String authToken = returnObj.get("authToken").toString();
            String devaccid = returnObj.get("defAccId").toString();

            returnMap.put("code", code);
            returnMap.put("authToken", authToken);
            returnMap.put("defAccId", devaccid);
        }else {
            returnMap.put("code", code);
        }
        return returnMap;
    }

    /*Check Authentication State API, used to check the PUSH Notification Authentication/QR Notification Authentication status (Approved/Pending/Reject)
      This method also used to check the Request PUSH CR OTP and Request QR Transaction Signing
    */
    public int checkAuthMethod(String user, String method, String authtoken) throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        Long timeStamp = Instant.now().getEpochSecond();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://cloud.centagate.com/CentagateWS/webresources/session/statecheck");

        Gson gson = new Gson();

        String secretKey = "pGY9Jua6fpgm";
        String username = user;
        String authMethod = method;
        String authToken = authtoken;
        String integrationKey = "0fe831638cc01e3f5a6e8e9340dac280597d5e527f79648a065ea96769422fa4";
        String unixTimestamp = Long.toString(timeStamp);

        String hmac = convertHmacSha256(secretKey, username + authMethod + integrationKey + unixTimestamp + authToken);

        HashMap<String, String> map = new HashMap<String, String>();
	    map.put("username", username);
	    map.put("authMethod", authMethod); 
	    map.put("authToken", authToken);
	    map.put("integrationKey", integrationKey);
    	map.put("unixTimestamp", unixTimestamp);
	    map.put("ipAddress", ""); 
	    map.put("userAgent", ""); 
	    map.put("browserFp", ""); 
	    map.put("supportFido", "");
	    map.put("hmac", hmac);

        String json = gson.toJson(map);

        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        Response response = target.request(mediaType).post(Entity.entity(json, mediaType));

        int statusCode = response.getStatus();
        String retJson = response.readEntity(String.class);

        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);
        
        String code = returnData.get("code").toString();
	    String message = returnData.get("message").toString();
	    String object = returnData.get("object").toString();

        client.close();

        return Integer.parseInt(code);
    }

   /*------------------------------------------------Request Authentications---------------------------------------*/

    //Request OTP Challenge API, used to request the Challenge code to let user type inside the Centagate Application
    public String requestCROTPAuthentication(String user) throws NoSuchAlgorithmException, 
            InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        String challenge = "";

        Long timeStamp = Instant.now().getEpochSecond();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://cloud.centagate.com/v2/CentagateWS/webresources/req/requestOtpChallenge");

        Gson gson = new Gson();
        
        String secretKey = "pGY9Jua6fpgm";
        String username = user;
        String devAccId = "7931d42bf49ad68d4e86cd3bbf3b722d";
        String integrationKey = "0fe831638cc01e3f5a6e8e9340dac280597d5e527f79648a065ea96769422fa4";
        String unixTimestamp = Long.toString(timeStamp);

        String hmac = convertHmacSha256(secretKey, username + devAccId + integrationKey + unixTimestamp);
    
        HashMap<String, String> map = new HashMap<String, String>();
	    map.put("otpType", "");
	    map.put("tokenId", ""); 
	    map.put("username", username);
	    map.put("devAccId", devAccId);
	    map.put("authToken", "");
	    map.put("integrationKey", integrationKey);
	    map.put("unixTimestamp", unixTimestamp);
	    map.put("ipAddress", ""); 
	    map.put("userAgent", ""); 
	    map.put("browserFp", ""); 
	    map.put("supportFido", "");
	    map.put("hmac", hmac);

        String json = gson.toJson(map);

        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        Response response = target.request(mediaType).post(Entity.entity(json, mediaType));

        int statusCode = response.getStatus();
        String retJson = response.readEntity(String.class);

        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);
        
        String code = returnData.get("code").toString();
	    String message = returnData.get("message").toString();
	    String object = returnData.get("object").toString();

        client.close();

        if(Integer.parseInt(code) == 0) {
            HashMap<String, Object> returnObj = (HashMap<String, Object>) gson.fromJson(object, HashMap.class);
            challenge = returnObj.get("otpChallenge").toString();
        }
        return challenge;
    }

     //Request QR Code Authentication API, used to request and generate QR string for user to scan in Centagate App
    public HashMap <String, String> requestQRAuthentication(String user, String devaccid) throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        Long timeStamp = Instant.now().getEpochSecond();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://cloud.centagate.com/v2/CentagateWS/webresources/req/requestQrCode");

        Gson gson = new Gson();

        String secretKey = "pGY9Jua6fpgm";
        String username = user;
        String devAccId = devaccid;
        String details = "VXNlclFSQ29kZUxvZ2lu";
        String integrationKey = "0fe831638cc01e3f5a6e8e9340dac280597d5e527f79648a065ea96769422fa4";
        String unixTimestamp = Long.toString(timeStamp);

        String hmac = convertHmacSha256(secretKey, username + devAccId + details + integrationKey + unixTimestamp);

        HashMap<String, String> map = new HashMap<String, String>();
	    map.put("username", username);
	    map.put("devAccId", devAccId);
	    map.put("details", details);
	    map.put("authToken", "");
	    map.put("integrationKey", integrationKey);
	    map.put("unixTimestamp", unixTimestamp);
	    map.put("ipAddress", ""); 
	    map.put("userAgent", ""); 
	    map.put("browserFp", ""); 
    	map.put("supportFido", "");
	    map.put("hmac", hmac); 

        String json = gson.toJson(map);

        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        Response response = target.request(mediaType).post(Entity.entity(json, mediaType));

        int statusCode = response.getStatus();
        String retJson = response.readEntity(String.class);

        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);
        
        String code = returnData.get("code").toString();
	    String message = returnData.get("message").toString();
	    String object = returnData.get("object").toString();

        client.close();

        HashMap<String, Object> returnObj = (HashMap<String, Object>) gson.fromJson(object, HashMap.class);

        HashMap<String, String> returnMap = new HashMap();

        if(Integer.parseInt(code) == 0) {
            String qrcode = returnObj.get("qrCode").toString();
            String authToken = returnObj.get("authToken").toString();
            String otpChallenge = returnObj.get("otpChallenge").toString();

            returnMap.put("code", code);
            returnMap.put("qrCode", qrcode);
            returnMap.put("authToken", authToken);
            returnMap.put("otpChallenge", otpChallenge);
        }else {
            returnMap.put("code", code);
        }

        return returnMap;
    }

    //Request Mobile Push CR OTP Authentication API, used to request Push Notification from Centagate Application to User Device 
    public HashMap<String, String> pushAuthentication(String user) throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        HashMap<String, String> returnMap = new HashMap();
        HashMap<String, String> hMap = adminApiAuthentication();

        Long timeStamp = Instant.now().getEpochSecond();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://cloud.centagate.com/v2/CentagateWS/webresources/req/requestMobilePushCR");

        Gson gson = new Gson();

        String secretKey = "pGY9Jua6fpgm";
        String username = user;
        String details = "UHVzaE5vdGlmaWNhdGlvblRvTG9naW4=";
        String integrationKey = "0fe831638cc01e3f5a6e8e9340dac280597d5e527f79648a065ea96769422fa4";
        String unixTimestamp = Long.toString(timeStamp);

        String hmac = convertHmacSha256(secretKey, username + details + integrationKey + unixTimestamp);

        HashMap<String, String> map = new HashMap<String, String>();
	    map.put("username", username);
	    map.put("details", details);
	    map.put("authToken", "");
	    map.put("integrationKey", integrationKey);
	    map.put("unixTimestamp", unixTimestamp);
	    map.put("ipAddress", ""); 
	    map.put("userAgent", ""); 
	    map.put("browserFp", ""); 
	    map.put("supportFido", "");
	    map.put("hmac", hmac); 

        String json = gson.toJson(map);

        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        Response response = target.request(mediaType).post(Entity.entity(json, mediaType));

        int statusCode = response.getStatus();
        String retJson = response.readEntity(String.class);

        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);
        
        String code = returnData.get("code").toString();
	    String message = returnData.get("message").toString();
	    String object = returnData.get("object").toString();

        client.close();
        
        if(Integer.parseInt(code) == 0) {
            HashMap<String, Object> returnObj = (HashMap<String, Object>) gson.fromJson(object, HashMap.class);
            String authToken = returnObj.get("authToken").toString();
            
            returnMap.put("code", code);
            returnMap.put("authToken", authToken);
        }else {
            returnMap.put("code", code);
        }
        
       return returnMap;
    }

    /*------------------------------------------------Request Authentications---------------------------------------*/


    /*-----------------------------------------------Authorize Authentications---------------------------------------*/

    //OTP Authentication API, used to authorize user OTP retrieved from Centagate Application
    public int authenticationOTP(String user, String otp) throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {   
        Long timeStamp = Instant.now().getEpochSecond();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://cloud.centagate.com/v2/CentagateWS/webresources/auth/authOtp");

        Gson gson = new Gson();

        String secretKey = "pGY9Jua6fpgm";
        String username = user;
        String OTP = otp;
        String integrationKey = "0fe831638cc01e3f5a6e8e9340dac280597d5e527f79648a065ea96769422fa4";
        String unixTimestamp = Long.toString(timeStamp);

        String hmac = convertHmacSha256(secretKey, username + OTP + integrationKey + unixTimestamp);

        HashMap<String, String> map = new HashMap<String, String>();
	    map.put("username", username);
	    map.put("devAccId", "");
	    map.put("otp", OTP);
	    map.put("authToken", "");
	    map.put("integrationKey", integrationKey);
	    map.put("unixTimestamp", unixTimestamp);
	    map.put("ipAddress", ""); 
	    map.put("userAgent", ""); 
	    map.put("browserFp", ""); 
	    map.put("supportFido", ""); 
	    map.put("hmac", hmac);

        String json = gson.toJson(map);

        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        Response response = target.request(mediaType).post(Entity.entity(json, mediaType));

        int statusCode = response.getStatus();
        String retJson = response.readEntity(String.class);

        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);
        
        String code = returnData.get("code").toString();
	    String message = returnData.get("message").toString();
	    String object = returnData.get("object").toString();
        
        client.close();
        
        return Integer.parseInt(code); 
    }

    /*Authorize CR OTP, use after request CR OTP challenge and get user input to validate the login
    Sequence: 1) Request CR OTP and get the challengeOTP, Output to User and tell them to submit in Centagate App
              2) Get the User Input (Tell user to submit the OTP from the Centagate App)
              3) Call this function to check validation 
    */
    public int authenticationCROTP(String user, String challenges, String otp, String devaccid) throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        Long timeStamp = Instant.now().getEpochSecond();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://cloud.centagate.com/v2/CentagateWS/webresources/auth/authCrOtp");

        Gson gson = new Gson();

        String secretKey = "pGY9Jua6fpgm";
        String username = user;
        String challenge = challenges;
        String crOtp = otp;
        String devAccId = devaccid;
        String integrationKey = "0fe831638cc01e3f5a6e8e9340dac280597d5e527f79648a065ea96769422fa4";
        String unixTimestamp = Long.toString(timeStamp);
        
        String hmac = convertHmacSha256(secretKey, username + devAccId + crOtp + challenge + integrationKey + unixTimestamp);

        HashMap<String, String> map = new HashMap<String, String>();
	    map.put("username", username);
	    map.put("devAccId", devAccId);
	    map.put("crOtp", crOtp);
	    map.put("challenge", challenge); //Challenge OTP that will generate during the Req Cr OTP
	    map.put("otpType", ""); //Insert 'online' if mobile token and 'offline' if hardware token
	    map.put("tokenId", ""); //Need insert hardware token serial number, if user register more than one hardware token and then need to specify the hardware token serial number
	    map.put("authToken", "");
	    map.put("integrationKey", integrationKey);
	    map.put("unixTimestamp", unixTimestamp);
	    map.put("ipAddress", ""); 
	    map.put("userAgent", ""); 
	    map.put("browserFp", ""); 
	    map.put("supportFido", ""); 
	    map.put("hmac", hmac);

        String json = gson.toJson(map);

        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        Response response = target.request(mediaType).post(Entity.entity(json, mediaType));

        int statusCode = response.getStatus();
        String retJson = response.readEntity(String.class);

        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);
        
        String code = returnData.get("code").toString();
	    String message = returnData.get("message").toString();
	    String object = returnData.get("object").toString();

        client.close();

        return Integer.parseInt(code);
    }


    /*-----------------------------------------------Authorize Authentications---------------------------------------*/


    /*----------------------------------------------- Request Transaction Signing---------------------------------------*/

    //Request Mobile Push CR OTP Transaction Signing API, used to prompt login notification in user Centagate Application
    public HashMap<String, String> RequestCROTPSign(String user, String detail) throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        Long timeStamp = Instant.now().getEpochSecond();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://cloud.centagate.com/v2/CentagateWS/webresources/req/requestMobilePushCR");

        Gson gson = new Gson();

        String username = user;
        String details = detail;
        String secretKey = "pGY9Jua6fpgm";
        String integrationKey = "0fe831638cc01e3f5a6e8e9340dac280597d5e527f79648a065ea96769422fa4";
        String unixTimestamp = Long.toString(timeStamp);

        String hmac = convertHmacSha256(secretKey, username + details + integrationKey + unixTimestamp);
    
        HashMap<String, String> map = new HashMap<String, String>();
	    map.put("username", username);
	    map.put("details", details);
	    map.put("authToken", "");
	    map.put("integrationKey", integrationKey);
	    map.put("unixTimestamp", unixTimestamp);
	    map.put("ipAddress", ""); 
	    map.put("userAgent", ""); 
	    map.put("browserFp", ""); 
	    map.put("supportFido", "");
	    map.put("hmac", hmac);

        String json = gson.toJson(map);

        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        Response response = target.request(mediaType).post(Entity.entity(json, mediaType));

        int statusCode = response.getStatus();
        String retJson = response.readEntity(String.class);

        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);
        
        String code = returnData.get("code").toString();
	    String message = returnData.get("message").toString();
	    String object = returnData.get("object").toString();

        client.close();

        HashMap<String, String> returnMap = new HashMap();
        
        if(Integer.parseInt(code) == 0) {
            HashMap<String, Object> returnObj = (HashMap<String, Object>) gson.fromJson(object, HashMap.class);
            String authToken = returnObj.get("authToken").toString();
        
            returnMap.put("code", code);
            returnMap.put("authToken", authToken);
        }else {
            returnMap.put("code", code);
        }

        return returnMap;
    }

    //Request QR Code Transaction Signing API, used to request QR Code Transaction Signing which return a QR string for user to scan in Centagate Application
    public HashMap<String, String> RequestQRSign(String user, String detail, String devaccid) throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        Long timeStamp = Instant.now().getEpochSecond();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://cloud.centagate.com/v2/CentagateWS/webresources/req/requestQrCode");

        Gson gson = new Gson();

        String username = user;
        String details = detail;
        String devAccId = devaccid;
        String secretKey = "pGY9Jua6fpgm";
        String integrationKey = "0fe831638cc01e3f5a6e8e9340dac280597d5e527f79648a065ea96769422fa4";
        String unixTimestamp = Long.toString(timeStamp);

        String hmac = convertHmacSha256(secretKey, username + devAccId + details + integrationKey + unixTimestamp);

        HashMap<String, String> map = new HashMap<String, String>();
	    map.put("username", username);
        map.put("devAccId", devAccId);
	    map.put("details", details);
	    map.put("authToken", "");
	    map.put("integrationKey", integrationKey);
	    map.put("unixTimestamp", unixTimestamp);
	    map.put("ipAddress", ""); 
	    map.put("userAgent", ""); 
	    map.put("browserFp", ""); 
	    map.put("supportFido", "");
	    map.put("hmac", hmac);

        String json = gson.toJson(map);

        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        Response response = target.request(mediaType).post(Entity.entity(json, mediaType));

        int statusCode = response.getStatus();
        String retJson = response.readEntity(String.class);

        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);
        
        String code = returnData.get("code").toString();
	    String message = returnData.get("message").toString();
	    String object = returnData.get("object").toString();
    
        client.close();

        HashMap<String, String> returnMap = new HashMap();

        if(Integer.parseInt(code) == 0) {
            HashMap<String, Object> returnObj = (HashMap<String, Object>) gson.fromJson(object, HashMap.class);

            String qr = returnObj.get("qrCode").toString();
            String authToken = returnObj.get("authToken").toString();
            String email = returnObj.get("email").toString();
            String usr = returnObj.get("username").toString();
            
            returnMap.put("code", code);
            returnMap.put("qr", qr);
            returnMap.put("authToken", authToken);
            returnMap.put("email", email);
            returnMap.put("username", usr);
        }else {
            returnMap.put("code", code);        
        }

        return returnMap;
    }

    //Encode a string message and return encoded base64 string
    public String base64Encode(String text) {
        return Base64.getUrlEncoder().encodeToString(text.getBytes());
    }

    /*----------------------------------------------- Request Transaction Signing---------------------------------------*/



    //Hash the password using PBKDF2 with SHA512 hashing (used to store the password in database)
    public String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeyException,
     InvalidKeySpecException 
    {
        String salt = "1234";
        int iterations = 1000;
        int keyLength = 512;
        char [] passwordChar = password.toCharArray();
        byte[] saltBytes = salt.getBytes();

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(passwordChar, saltBytes, iterations, keyLength);
        SecretKey key = factory.generateSecret(keySpec); 
        byte[] hash = key.getEncoded();
        String hashPass = Hex.encodeHexString(hash);
        return hashPass;
    }

    //API that use to save the user security question (not working)
    public int saveQuestion(String username, String data) throws NoSuchAlgorithmException, 
            InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        HashMap<String, String> hMap = adminApiAuthentication();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://cloud.centagate.com/CentagateWS/webresources/security/saveUserQuestions/winter");

        Gson gson = new Gson();

        String authToken = hMap.get("authToken");
        String adminUsername = "winter";
        String secretCode = hMap.get("secretCode");
        String cenToken = convertHmacSha256(secretCode, adminUsername + authToken);

        HashMap<String, String> map = new HashMap<String, String>();
	    map.put("username", username);
	    map.put("data", data);
	    map.put("cenToken", cenToken); 

        String json = gson.toJson(map);

        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        Response response = target.request(mediaType).put(Entity.entity(json, mediaType));

        int statusCode = response.getStatus();
        String retJson = response.readEntity(String.class);

        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
	    String message = returnData.get("message").toString();
	    String object = returnData.get("object").toString();

        client.close();
        return Integer.parseInt(code);
    }
}