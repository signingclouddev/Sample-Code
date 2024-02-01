/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.web.api;

import com.google.gson.Gson;
import com.securemetric.web.servlet.RestfulUtil;
import java.util.HashMap;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 *
 * @author auyong
 */
public class FidoAuth {
    String SUPPORT_FIDO_KEY = "true"; 
    public HashMap<String , String> Authenticate(String username,String fidoPublicKeyCredential, String authToken, String ipAddress,String userAgent) 
    {
        /*authSmsOtp*/

        String authString = null;
        HashMap<String , String> responseMap = new HashMap( ) ; 
        try {
        /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
        String integrationKey = APIController.getIntegrationKey();   
        /* The current time in second (GMT+00:00) */ 
        String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
        /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
        String secretKey = APIController.getSecretKey();
            
        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ( "username" , username ) ; 	
        map.put ( "integrationKey" , integrationKey ) ; 
        map.put ( "unixTimestamp" , unixTimestamp ) ; 
        map.put ( "ipAddress" , ipAddress ) ; /* The client’s IP address */ 
        map.put ( "userAgent" , userAgent ) ; /* The client’s user agent */ 
        map.put ( "supportFido" , SUPPORT_FIDO_KEY ) ; 
        map.put("authToken", authToken);
        map.put("assertion", fidoPublicKeyCredential);
        map.put ( "hmac" ,APIController.calculateHmac256 (
                   secretKey , 
                   username + integrationKey + unixTimestamp + authToken + SUPPORT_FIDO_KEY + ipAddress + userAgent));

        Gson gson = new Gson (); /*GSON library*/
        com.sun.jersey.api.client.Client client = RestfulUtil.buildClient();//com.sun.jersey.api.client.Client.create ( config );
        /* Send the POST request for authentication */ 
        WebResource service = client.resource ( APIController.getAuthURI()+  "/auth/authFido");
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );
        System.out.println("DEBUG Message[fidoAuth] : Call getStatus " +  response.getStatus());
        System.out.println("DEBUG Message[fidoAuth] : Call API " + response.toString());
        System.out.println("DEBUG Message[fidoAuth] : Call API " + gson.toJson(map));
         /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity ( String.class ) ; 
             System.out.println("DEBUG Message[FidoAuth] : return json : " + authString);
            responseMap = gson.fromJson ( authString , HashMap.class );
        }
           
        }catch (Exception e){
            System.out.println("Error");
            System.out.println("DEBUG Message[FidoAuth] : Failed with exception " + e.getMessage());
            e.printStackTrace();
        } 

        return responseMap; 
    }
    
    
    public HashMap<String , String> AuthoriseFidoTrans(String username,String fidoPublicKeyCredential, String authToken, String ipAddress,String userAgent, String details, String transactionId, String fidoChallenge) 
    {
        /*authSmsOtp*/

        String authString = null;
        HashMap<String , String> responseMap = new HashMap( ) ; 
        try {
        /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
        String integrationKey = APIController.getIntegrationKey();   
        /* The current time in second (GMT+00:00) */ 
        String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
        /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
        String secretKey = APIController.getSecretKey();
        transactionId = "1234567890ABC";       
        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ( "username" , username ) ; 	
        map.put ( "integrationKey" , integrationKey ) ; 
        map.put ( "unixTimestamp" , unixTimestamp ) ; 
        map.put ( "ipAddress" , ipAddress ) ; /* The client’s IP address */ 
        map.put ( "userAgent" , userAgent ) ; /* The client’s user agent */ 
        map.put ( "supportFido" , SUPPORT_FIDO_KEY ) ; 
        map.put("authToken", authToken);
        map.put("details", details);
        map.put("transactionId", transactionId);
        map.put("fidoChallenge", fidoChallenge);
        map.put("assertion", fidoPublicKeyCredential);
        map.put ( "hmac" ,APIController.calculateHmac256 (
                   secretKey , 
                   username + transactionId + details + fidoChallenge + integrationKey + unixTimestamp + authToken + SUPPORT_FIDO_KEY + ipAddress + userAgent));

        Gson gson = new Gson (); /*GSON library*/
        com.sun.jersey.api.client.Client client = RestfulUtil.buildClient();//com.sun.jersey.api.client.Client.create ( config );
        /* Send the POST request for authentication */ 
        WebResource service = client.resource ( APIController.getAuthURI()+  "/auth/authFidoTrans");
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );
        System.out.println("DEBUG Message[AuthoriseFidoTrans] : Call getStatus " +  response.getStatus());
        System.out.println("DEBUG Message[AuthoriseFidoTrans] : Call API " + response.toString());
        System.out.println("DEBUG Message[AuthoriseFidoTrans] : Call API " + gson.toJson(map));
         /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity ( String.class ) ; 
             System.out.println("DEBUG Message[AuthoriseFidoTrans] : return json : " + authString);
            responseMap = gson.fromJson ( authString , HashMap.class );
        }
           
        }catch (Exception e){
            System.out.println("Error");
            System.out.println("DEBUG Message[AuthoriseFidoTrans] : Failed with exception " + e.getMessage());
            e.printStackTrace();
        } 

        return responseMap;  
    }
    
//    public static void main(String[] args) throws Exception {
//
//        String details = "30AAAABBBB2017-09-25";
//        String transId = "";
//        HashMap<String , String> authString;
//        SMSTransAuth smsAuth = new SMSTransAuth();
//        
//        String username = JOptionPane.showInputDialog("Please enter your username:");
//        authString = smsAuth.requestSmsOtp(username, details, transId);
//
//        /* Read the output returned from request sms code */ 
//        int code = Integer.parseInt(authString.get ( "code" ));  /* Return status code */
//        System.out.println("Request SMS status code = " + code);
//        System.out.println("Request SMS message = " + authString.get ( "message" )); /* Return message */
//        String authToken = authString.get("authToken");
//        String secretCode = authString.get("secretCode");
//        if( code == 0) { /* status code 0 = Success */
//            
//            authString = null; /* initialize Hashmap to empty */
//            String inputSMSOtp = JOptionPane.showInputDialog("Please enter the sms code :");
//            
//            authString = smsAuth.Authenticate(username, inputSMSOtp, details, transId);
//            
//            System.out.println("SMS Authentication status = " + authString.get ( "code" ));/* Return status code */
//            System.out.println("SMS Authentication return message = " + authString.get ( "message" )); /* Return message */
//            String authJson = authString.get ( "object" );  /* Return object on json format */
//            
//            if( authJson != null ) {
//                
//                /* Parse the result of the authentication to String object*/ 
//                Gson gson = new Gson();
//                HashMap<String , String> authMap = gson.fromJson ( authJson , HashMap.class );
//                Iterator it = authMap.keySet().iterator();
//
//                /* List out return attributes */
//                while (it.hasNext())
//                {
//                    Object key = it.next();
//                    String val = authMap.get(key);
//                    System.out.println("Attributes : "+ key +", Value :" + val);
//                }
//            }
//        }
//    }
}
