/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.api.auth;

import com.google.gson.Gson;
import com.securemetric.web.util.APIController;
import com.securemetric.web.util.SecureRestClientTrustManager;
import java.util.HashMap;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.util.Iterator;
import javax.swing.JOptionPane;

/**
 *
 * @author auyong
 */
public class OTPAuth {

    public HashMap<String , String>  Authenticate(String username, String otp) 
    {
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;
        SecureRestClientTrustManager secureRestClientTrustManager = new SecureRestClientTrustManager(); 
        /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
        String integrationKey = APIController.getIntegrationKey();  
        /* The current time in second (GMT+00:00) */ 
        String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
        /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
        String secretKey = APIController.getSecretKey() ;  

        String ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
        String userAgent = "My user agent"; /* The client’s user agent. Optional Field */
        String authToken = ""; /* The previous generated authToken. This is optional. You can leave it empty*/ 
        String supportFido = ""; /* Put in the value “true” or “false”. Or leave it empty */
        String devAccId = "";
        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ( "username" , username ) ; 
   //     map.put("OtpType","online");
        map.put ( "devAccId" , "" ) ; 
        map.put ( "otp" , otp ) ;
        map.put ( "integrationKey" , integrationKey ) ; 
        map.put ( "unixTimestamp" , unixTimestamp ) ; 
        map.put ( "authToken" , authToken ) ; 
        map.put ( "supportFido" , supportFido ) ;  
        map.put ( "ipAddress" , ipAddress ) ; /* The client’s IP address. Optional Field */ 
        map.put ( "userAgent" , userAgent ) ; /* The client’s user agent. Optional Field */ 
        map.put ( "hmac" ,  secureRestClientTrustManager.calculateHmac256 (
                   secretKey , 
                   username + otp + devAccId + integrationKey + unixTimestamp + authToken + supportFido + ipAddress + userAgent)) ; 

        Gson gson = new Gson (); /*GSON library*/     
    //    ClientConfig config = new DefaultClientConfig ();
     //   com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        com.sun.jersey.api.client.Client client = APIController.buildClient();
        WebResource service = client.resource ( APIController.getBaseURI()+  "/auth/authOtp" );
        /* Send the POST request for authentication */ 
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );

        /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity ( String.class ) ;  
            responseMap = gson.fromJson ( authString , HashMap.class );
        }

        return responseMap; 
    }
    
    public static void main(String[] args) throws Exception {

        HashMap<String , String> authString; 
        OTPAuth otpAuth = new OTPAuth();
        String username = JOptionPane.showInputDialog("Please enter username :"); /* input username */
        String otp = JOptionPane.showInputDialog("Please enter otp :"); /* input otp */
        authString = otpAuth.Authenticate(username, otp);
        System.out.println("authString = " + authString); /* Return status code */
        /* Read the output returned from the authentication */ 
        System.out.println("Status = " + authString.get ( "code" )); /* Return status code */
        System.out.println("Message = " + authString.get ( "message" )); /* Return message */
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