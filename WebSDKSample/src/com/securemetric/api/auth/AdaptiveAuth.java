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

/**
 *
 * @author auyong
 */
public class AdaptiveAuth {

    public HashMap<String , String>  Authenticate(String username, String authResult) 
    {
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;

        /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
        String integrationKey = APIController.getIntegrationKey();  
        /* The current time in second (GMT+00:00) */ 
        String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
        /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
        String secretKey = APIController.getSecretKey() ;  

        String ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
        String userAgent = "My user agent"; /* The client’s user agent. Optional Field */
        String supportFido = "true"; /* Put in the value “true” or “false”. Or leave it empty */
        
        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ( "username" , username ) ; 
        map.put ( "authResult" , authResult ) ;
        map.put ( "integrationKey" , integrationKey ) ; 
        map.put ( "unixTimestamp" , unixTimestamp ) ; 
        map.put ( "ipAddress" , ipAddress ) ; /* The client’s IP address. Optional Field */ 
        map.put ( "userAgent" , userAgent ) ; /* The client’s user agent. Optional Field */ 
        map.put ( "hmac" ,  APIController.calculateHmac256 (
                   secretKey , 
                   username + authResult + integrationKey + unixTimestamp + ipAddress + userAgent)) ; 

        Gson gson = new Gson (); /*GSON library*/     
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );

        WebResource service = client.resource ( APIController.getBaseURI()+  "/auth/adaptive" );
        /* Send the POST request for authentication */ 
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );
        System.out.println(response);
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
        AdaptiveAuth basicAuth = new AdaptiveAuth();
        String username = JOptionPane.showInputDialog("Please enter username :"); /* input username */
        String password = JOptionPane.showInputDialog("Please enter password :"); /* input password */

        authString = basicAuth.Authenticate(username, password);

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