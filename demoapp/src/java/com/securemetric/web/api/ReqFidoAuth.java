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
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author auyong
 */
public class ReqFidoAuth {

    private String strHMAC = null;

    public HashMap<String, String> Authenticate(String username, String authToken, String ipAddress, String userAgent) {
        String authString = null;
        HashMap<String, String> responseMap = new HashMap();
        try {
            SecureRestClientTrustManager secureRestClientTrustManager = new SecureRestClientTrustManager();
            /* The integration key. This is the key you get from the update app page inside the CENTAGATE */
            String integrationKey = APIController.getIntegrationKey();;
            /* The current time in second (GMT+00:00) */
            String unixTimestamp = String.valueOf(System.currentTimeMillis() / 1000L);
            /* The secret key. This is the key you get from the update app page inside the CENTAGATE */
            String secretKey = APIController.getSecretKey();
            /* Put all the required parameters for basic authentication */
            HashMap<String, String> map = new HashMap();
           // if (multiStep.equals("true")) {

            map.put("username", username);
            map.put("integrationKey", integrationKey);
            map.put("unixTimestamp", unixTimestamp);
            map.put("ipAddress", ipAddress);
            map.put("userAgent", userAgent);
            map.put("authToken", authToken);
            map.put("supportFido", "true");
            map.put("hmac", secureRestClientTrustManager.calculateHmac256(secretKey, username + integrationKey + unixTimestamp + authToken + "true" + ipAddress + userAgent ));

            

            /* Read the output returned from the authentication */
            Gson gson = new Gson();
            /*GSON library*/

            com.sun.jersey.api.client.Client client = RestfulUtil.buildClient();//com.sun.jersey.api.client.Client.create ( config );
            WebResource service = client.resource(APIController.getAuthURI() + "/req/requestAssertionOption");
            ClientResponse response = service.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, gson.toJson(map));
            System.out.println("DEBUG Message[ReqFidoAuth] : " + map.toString()); 
            System.out.println("DEBUG Message[ReqFidoAuth] : call API" + response.toString());
            if (response.getStatus() == 200) {
                authString = response.getEntity(String.class);
                responseMap = gson.fromJson(authString, HashMap.class);
                System.out.println("DEBUG Message[ReqFidoAuth] : " + authString);
            }

        } catch (Exception e) {
            System.out.println("Error");
            System.out.println("DEBUG Message[ReqFidoAuth] : Failed with exception " + e.getMessage());
            e.printStackTrace();
        }
        return responseMap;
    }
    
    
      public HashMap<String , String> requestFidoTrans(String username , String details, String transactionId, String ipAddress,String userAgent) 
    {
        /*requestSmsOtp*/
        String authString = null;
        HashMap<String , String> responseMap = new HashMap( ) ; 
        try {
        /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
        String integrationKey = APIController.getIntegrationKey(); ;  
        /* The current time in second (GMT+00:00) */ 
        String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
        /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
        String secretKey = APIController.getSecretKey();
        /* Put all the required parameters for basic authentication */ 
        transactionId = "1234567890ABC"; 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ( "username" , username ) ; 	
        map.put ( "integrationKey" , integrationKey ) ; 
        map.put ( "unixTimestamp" , unixTimestamp ) ; 
        map.put ( "ipAddress" , ipAddress ) ; /* The client’s IP address */ 
        map.put ( "userAgent" , userAgent ) ; /* The client’s user agent */ 
        map.put ( "supportFido" , "true" ) ; 
        map.put ( "details" , details ) ;
        map.put ( "transactionId" , transactionId ) ;
        map.put ( "hmac" , APIController.calculateHmac256 (
                   secretKey , 
                   username + integrationKey + unixTimestamp + "true" + details + transactionId + ipAddress  + userAgent  ));
        
          /* Send the POST request for authentication */ 
        
        com.sun.jersey.api.client.Client client = RestfulUtil.buildClient();//com.sun.jersey.api.client.Client.create ( config );
        /* Send the POST request for authentication */ 
  
        Gson gson = new Gson (); /*GSON library*/
        /* Send the POST request for authentication */ 
        WebResource service = client.resource ( APIController.getAuthURI()+  "/req/requestFidoTrans"  );
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );
        System.out.println("DEBUG Message[requestFidoTrans] : " + map.toString()); 
        System.out.println("DEBUG Message[requestFidoTrans] : call API" + response.toString());
         /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity ( String.class ) ;  
             System.out.println("DEBUG Message[requestFidoTrans] : " + authString);
                
            responseMap = gson.fromJson ( authString , HashMap.class );
        }
           
        }catch (Exception e){
                       System.out.println("Error");
            System.out.println("DEBUG Message[requestFidoTrans] : Failed with exception " + e.getMessage()); 
            e.printStackTrace();
        } 

        return responseMap;
    }
}
