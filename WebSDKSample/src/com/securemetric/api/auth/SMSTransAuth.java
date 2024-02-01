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
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 *
 * @author auyong
 */
public class SMSTransAuth {

    public HashMap<String , String> requestSmsOtp(String username , String details, String transId) 
    {
        /*requestSmsOtp*/
        String authString = null;
        HashMap<String , String> responseMap = new HashMap( ) ; 
        try {
        /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
        String integrationKey = APIController.getIntegrationKey(); ;  
        /* The current time in second (GMT+00:00) */ 
        String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
        unixTimestamp = "1591833600";
        /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
        String secretKey = APIController.getSecretKey();

        String ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
        String userAgent = "My user agent"; /* The client’s user agent. Optional Field */
        
        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ( "username" , username ) ; 	
        map.put ( "integrationKey" , integrationKey ) ; 
        map.put ( "unixTimestamp" , unixTimestamp ) ; 
        map.put ( "ipAddress" , ipAddress ) ; /* The client’s IP address */ 
        map.put ( "userAgent" , userAgent ) ; /* The client’s user agent */ 
        map.put ( "details" , details ) ; /* The client’s user agent */ 
        map.put ( "transactionId" , transId ) ; /* The client’s user agent */ 
        map.put ( "hmac" , APIController.calculateHmac256 (
                   secretKey , 
                   username + integrationKey + unixTimestamp + ipAddress + details + transId + userAgent  ));
          /* Send the POST request for authentication */ 
        
            try {
            APIController.TrustAllService();
        }
        catch (Exception e) {
            
        }
        
        Gson gson = new Gson (); /*GSON library*/
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        /* Send the POST request for authentication */ 
        WebResource service = client.resource ( APIController.getBaseURI()+  "/auth/requestSmsTransOtp"  );
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );
         System.out.println(gson.toJson ( map ));
         /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity ( String.class ) ;  
            System.out.println(authString);
            responseMap = gson.fromJson ( authString , HashMap.class );
        }
           
        }catch (Exception e){
            e.printStackTrace();
        } 

        return responseMap;
    }
    
    public HashMap<String , String> Authenticate(String username, String smsOtp, String details, String transId) 
    {
        /*authSmsOtp*/

        String authString = null;
        HashMap<String , String> responseMap = new HashMap( ) ; 
        try {
        /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
        String integrationKey = APIController.getIntegrationKey();   
        /* The current time in second (GMT+00:00) */ 
        String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
        unixTimestamp = "1591833600";
        /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
        String secretKey = APIController.getSecretKey();
        
        String ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
        String userAgent = "My user agent"; /* The client’s user agent. Optional Field */
        
        
        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ( "username" , username ) ; 
        map.put ( "smsOtp" , smsOtp ) ; 	
        map.put ( "integrationKey" , integrationKey ) ; 
        map.put ( "unixTimestamp" , unixTimestamp ) ; 
        map.put ( "ipAddress" , ipAddress ) ; /* The client’s IP address */ 
        map.put ( "userAgent" , userAgent ) ; /* The client’s user agent */ 
        map.put ( "details" , details ) ; 
        map.put ( "transactionId" , transId ) ; 
        map.put ( "hmac" ,APIController.calculateHmac256 (
                   secretKey , 
                   username + transId + details  + smsOtp + integrationKey + unixTimestamp + ipAddress + userAgent));
        
          try {
            APIController.TrustAllService();
        }
        catch (Exception e) {
            
        }
        
        Gson gson = new Gson (); /*GSON library*/
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        /* Send the POST request for authentication */ 
        WebResource service = client.resource ( APIController.getBaseURI()+  "/trans/authoriseSmsTrans/"+ username +"/");
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );
        
         /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity ( String.class ) ; 
            responseMap = gson.fromJson ( authString , HashMap.class );
        }
           
        }catch (Exception e){
            System.out.println("Error");
            e.printStackTrace();
        } 

        return responseMap; 
    }
    
    public static void main(String[] args) throws Exception {

        String details = "30AAAABBBB2017-09-25";
        String transId = "abcd1234";
        HashMap<String , String> authString;
        SMSTransAuth smsAuth = new SMSTransAuth();
        
        String username = JOptionPane.showInputDialog("Please enter your username:");
        authString = smsAuth.requestSmsOtp(username, details, transId);

        /* Read the output returned from request sms code */ 
        int code = Integer.parseInt(authString.get ( "code" ));  /* Return status code */
        System.out.println("Request SMS status code = " + code);
        System.out.println("Request SMS message = " + authString.get ( "message" )); /* Return message */
        String authToken = authString.get("authToken");
        String secretCode = authString.get("secretCode");
        if( code == 0) { /* status code 0 = Success */
            
            authString = null; /* initialize Hashmap to empty */
            String inputSMSOtp = JOptionPane.showInputDialog("Please enter the sms code :");
            
            authString = smsAuth.Authenticate(username, inputSMSOtp, details, transId);
            
            System.out.println("SMS Authentication status = " + authString.get ( "code" ));/* Return status code */
            System.out.println("SMS Authentication return message = " + authString.get ( "message" )); /* Return message */
            String authJson = authString.get ( "object" );  /* Return object on json format */
            
            if( authJson != null ) {
                
                /* Parse the result of the authentication to String object*/ 
                Gson gson = new Gson();
                HashMap<String , String> authMap = gson.fromJson ( authJson , HashMap.class );
                Iterator it = authMap.keySet().iterator();

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
