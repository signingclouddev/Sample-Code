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

/**
 *
 * @author auyong
 */
public class CROTPTrans {
     private String strHMAC = null;
    public HashMap<String , String> Authenticate(String username, String otp, String challenge, String authToken, String details,String ipAddress, String userAgent, String browserFp) 
    {
        String authString = null;
        HashMap<String , String> responseMap = new HashMap( ) ; 
        try {
        SecureRestClientTrustManager secureRestClientTrustManager = new SecureRestClientTrustManager(); 
        /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
        String integrationKey = APIController.getIntegrationKey();   
        /* The current time in second (GMT+00:00) */ 
        String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
        /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
        String secretKey = APIController.getSecretKey();
        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ( "username" , username ) ; 
        map.put ( "transactionId" , "1" ) ;
        map.put ( "otp" , otp ) ; 	
        map.put ( "challenge" , challenge ) ; 
        map.put ( "details" , details ) ;
        map.put ( "integrationKey" , integrationKey ) ; 
        map.put ( "unixTimestamp" , unixTimestamp ) ;
        map.put ( "supportFido" , "false" ) ;
        map.put ( "authToken" , authToken ) ;
        map.put ( "ipAddress", ipAddress ) ;
        map.put ( "userAgent", userAgent ) ;
        map.put ( "browserFp", browserFp ) ; 
        String hmac = secureRestClientTrustManager.calculateHmac256 (
                   secretKey , 
                   username + otp + details + "1" + integrationKey + unixTimestamp + authToken + "false" + ipAddress + userAgent + browserFp );
        map.put ( "hmac", hmac ) ;
        /* Read the output returned from the authentication */ 
        Gson gson = new Gson (); /*GSON library*/        
        com.sun.jersey.api.client.Client client = RestfulUtil.buildClient();//com.sun.jersey.api.client.Client.create ( config );
        WebResource service = client.resource ( APIController.getAuthURI()+  "/trans/authTransactionSigningCrOtp");
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );
        System.out.println("DEBUG Message[CROTPTrans] : call API" + response.toString());
        System.out.println("DEBUG Message[CROTPTrans] : call API" + gson.toJson ( map ));        
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity ( String.class ) ;  
            System.out.println("DEBUG Message[CROTPTrans] : call authString" + authString);
            responseMap = gson.fromJson ( authString , HashMap.class );
        }
        else {
            System.out.println("DEBUG Message[CROTPTrans] : call getEntity" + response.getEntity ( String.class ));
        }
        }catch (Exception e){
            System.out.println("Error");
             System.out.println("DEBUG Message[CROTPTrans] : Failed with exception " + e.getMessage()); 
            e.printStackTrace();
        } 


        return responseMap; 
    }
}
