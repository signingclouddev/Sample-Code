/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.api.auth;

import com.google.gson.Gson;
import com.securemetric.web.util.APIController;
import java.util.HashMap;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.util.Base64;
import java.util.Iterator;
import javax.swing.JOptionPane;

/**
 *
 * @author auyong
 */
public class CROTPAuth {

    public HashMap<String , String> requestOtpChallenge(String username) 
    {
        /*requestOtpChallenge*/
        String authString = null;
        HashMap<String , String> responseMap = new HashMap( ) ; 
        try {

        /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
        String integrationKey = APIController.getIntegrationKey();  
        /* The current time in second (GMT+00:00) */ 
        String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
        /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
        String secretKey = APIController.getSecretKey(); 
        
        String ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
        String userAgent = "My user agent"; /* The client’s user agent. Optional Field */
        String authToken = ""; /* The previous generated authToken. This is optional. You can leave it empty*/ 
        
        String originalInput = "test input";
        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
        String transactionId = "test1234";
        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ( "username" , username ) ; 	
        map.put ( "details" , encodedString ) ; 
        map.put ( "transactionId" , transactionId ) ; /* The client’s IP address */ 
        map.put ( "integrationKey" , integrationKey ) ; 
        map.put ( "unixTimestamp" , unixTimestamp ) ; 
        map.put ( "authToken" , authToken ) ; 
        map.put ( "ipAddress" , ipAddress ) ; /* The client’s IP address */ 
        map.put ( "userAgent" , userAgent ) ; /* The client’s user agent */ 
        map.put ( "hmac" , APIController.calculateHmac256 (
                   secretKey , 
                   username + encodedString + transactionId + integrationKey + unixTimestamp + authToken + ipAddress + userAgent));

        /* Read the output returned from the authentication */ 
        Gson gson = new Gson (); /*GSON library*/
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        WebResource service = client.resource ( APIController.getBaseURI()+  "/req/requestTransactionSigningChallenge"  );
        /* Send the POST request for authentication */ 
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity ( String.class ) ;  
            responseMap = gson.fromJson ( authString , HashMap.class );
        }
           
        }catch (Exception e){
            e.printStackTrace();
        } 

        return responseMap; 
    }
    
    public HashMap<String , String> Authenticate(String username, String crOtp, String challenge) 
    {
        /*POST API REQUEST*/
        
        String authString = null;
        HashMap<String , String> responseMap = new HashMap( ) ; 
        try {

        /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
        String integrationKey = APIController.getIntegrationKey();   
        /* The current time in second (GMT+00:00) */ 
        String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
        /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
        String secretKey = APIController.getSecretKey();
        
        String ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
        String userAgent = "My user agent"; /* The client’s user agent. Optional Field */
        String authToken = ""; /* The previous generated authToken. This is optional. You can leave it empty*/ 
        String supportFido = ""; /* Put in the value “true” or “false”. Or leave it empty */
        String originalInput = "test input";
        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
        String transactionId = "test1234";
        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ( "username" , username ) ; 
        map.put ( "details" , encodedString ) ; 
        map.put ( "transactionId" , transactionId ) ; 
        map.put ( "otp" , crOtp ) ; 	
        map.put ( "integrationKey" , integrationKey ) ; 
        map.put ( "unixTimestamp" , unixTimestamp ) ; 
        map.put ( "authToken" , authToken ) ;
        map.put ( "supportFido" , supportFido ) ;
        map.put ( "ipAddress" , ipAddress ) ; /* The client’s IP address */ 
        map.put ( "userAgent" , userAgent ) ; /* The client’s user agent */ 
        map.put ( "hmac" , APIController.calculateHmac256 (
                   secretKey , 
                  username + crOtp + encodedString + transactionId + integrationKey + unixTimestamp + authToken + supportFido + ipAddress + userAgent));
  
        /* Read the output returned from the authentication */ 
        Gson gson = new Gson (); /*GSON library*/
        
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        WebResource service = client.resource ( APIController.getBaseURI()+  "/trans/authTransactionSigningCrOtp" );
        /* Send the POST request for authentication */ 
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );
        
        /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity ( String.class ) ;  
            responseMap = gson.fromJson ( authString , HashMap.class );
        }
          
        System.out.println(gson.toJson ( map ));
        
        }catch (Exception e){
            System.out.println("Error");
            e.printStackTrace();
        } 


        return responseMap; 
    }
    
    public static void main(String[] args) throws Exception {

        HashMap<String , String> authString;
        CROTPAuth crOtpAuth = new CROTPAuth();
        Gson gson = new Gson();
        //String username = JOptionPane.showInputDialog("Please enter your username:");
        String username = "testuser";
        authString = crOtpAuth.requestOtpChallenge(username);
        System.out.println("authString = " + authString);
        /* Read the output returned from request sms code */ 
        int code = Integer.parseInt(authString.get ( "code" ));  /* Return status code */
        System.out.println("Request OTP Challenge status code = " + code);
        System.out.println("Request OTP Challenge message = " + authString.get ( "message" )); /* Return message */
        
        String authJson = authString.get ( "object" );  /* Return object on json format */
        
        if( code == 0) { /* status code 0 = Success */
            HashMap<String , String> authMap = gson.fromJson ( authJson , HashMap.class );
            String challenge = authMap.get("otpChallenge");

             Iterator it = authMap.keySet().iterator();

                /* List out return attributes */
                while (it.hasNext())
                {
                    Object key = it.next();
                    String val = authMap.get(key);
                    System.out.println("Attributes : "+ key +", Value :" + val);
                }
            
            authString = null; /* initialize Hashmap to empty */
            String crOtp = JOptionPane.showInputDialog("OTP Challenge :"+challenge+"\n\nPlease enter the otp code :");

            authString = crOtpAuth.Authenticate(username, crOtp, challenge);
            System.out.println("Authentication CR OTP Challenge authString = " + authString);
            System.out.println("CR OTP Authentication status = " + authString.get ( "code" ));/* Return status code */
            System.out.println("CR OTP Authentication return message = " + authString.get ( "message" )); /* Return message */
            authJson = authString.get ( "object" );  /* Return object on json format */
            
            if( authJson != null ) {
                
                /* Parse the result of the authentication to String object*/         
                authMap = gson.fromJson ( authJson , HashMap.class );
                it = authMap.keySet().iterator();

                /* List out return attributes */
                while (it.hasNext())
                {
                    Object key = it.next();
                    String val = authMap.get(key);
                    System.out.println("Attributes : "+ key +", Value :" + val);
                }
            }
        }
    }
}
