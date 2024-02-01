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
import com.securemetric.web.util.SecureRestClientTrustManager;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 *
 * @author auyong
 */
public class BasicAuth {
 private String strHMAC = null;
    public HashMap<String , String>  Authenticate(String username, String password, String ipAddress, String userAgent, String browserFp) 
    {
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;
        try {
            
            SecureRestClientTrustManager secureRestClientTrustManager = new SecureRestClientTrustManager();
            /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
            String integrationKey = APIController.getIntegrationKey();  
            /* The current time in second (GMT+00:00) */ 
            String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
            /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
            String secretKey = APIController.getSecretKey() ;  
            /* Put all the required parameters for basic authentication */ 
            HashMap<String,String> map = new HashMap( ) ; 
            map.put ( "username" , username ) ; 
            map.put ( "password" , password ) ;
            map.put ( "integrationKey" , integrationKey ) ; 
            map.put ( "unixTimestamp" , unixTimestamp ) ; 
            map.put ( "ipAddress" , ipAddress ) ; /* The client’s IP address */ 
            map.put ( "userAgent" , userAgent ) ; /* The client’s user agent */ 
            map.put ( "browserFp" , browserFp ) ;
            map.put ( "supportFido" , "true" ) ;
            map.put ( "hmac" ,  secureRestClientTrustManager.calculateHmac256 (
                       secretKey , 
                       username + password + integrationKey + unixTimestamp + "true" + ipAddress + userAgent +  browserFp )) ; 

            
            Gson gson = new Gson (); /*GSON library*/     
            com.sun.jersey.api.client.Client client = RestfulUtil.buildClient();//com.sun.jersey.api.client.Client.create ( config );
            
            WebResource service = client.resource ( APIController.getAuthURI()+  "/auth/authBasic" );
            
             System.out.println("DEBUG Message[BasicAuth] : service " + service.toString()); 
            /* Send the POST request for authentication */ 
            ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );
            
            /* Read the output returned from the authentication */ 
            if(response.getStatus() == 200)
            {    
                authString = response.getEntity ( String.class ) ;  
                responseMap = gson.fromJson ( authString , HashMap.class );
                System.out.println(authString);
            }
           
        }catch (Exception e){
            System.out.println("DEBUG Message[BasicAuth] : Failed with exception " + e.getMessage()); 
            e.printStackTrace();
        } 

        return responseMap; 
    }
}