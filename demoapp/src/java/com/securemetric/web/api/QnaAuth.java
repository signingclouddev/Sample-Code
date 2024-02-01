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
public class QnaAuth {
 private String strHMAC = null;
    public HashMap<String , String>  Authenticate(String username,String authToken, String ipAddress, String userAgent, String browserFp, int quesSize, String[][] qnaAns ) 
    {
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;
        StringBuilder strBuild = new StringBuilder();

        try {
            
            for (int i = 0; i<quesSize; i++){
                strBuild.append(Base64.encodeBase64String((qnaAns[i][0] + ":" + qnaAns[i][1]).getBytes()));
                strBuild.append(",");
            }
            strBuild.deleteCharAt(strBuild.length()-1) ;
            
            String data = strBuild.toString();
            
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
        map.put ( "integrationKey" , integrationKey ) ; 
        map.put ( "unixTimestamp" , unixTimestamp ) ; 
         map.put ( "authToken" , authToken ) ; 
        map.put ( "data" , data ) ; 
        map.put ( "ipAddress" , ipAddress ) ; /* The client’s IP address */ 
        map.put ( "userAgent" , userAgent ) ; /* The client’s user agent */ 
        map.put ( "browserFp" , browserFp ) ;
        map.put ( "hmac" ,  secureRestClientTrustManager.calculateHmac256 (
                   secretKey , 
                   username + integrationKey + unixTimestamp + data + authToken+ ipAddress + userAgent +  browserFp )) ; 

        Gson gson = new Gson (); /*GSON library*/     
       
        com.sun.jersey.api.client.Client client = RestfulUtil.buildClient();//com.sun.jersey.api.client.Client.create ( config );

        WebResource service = client.resource ( APIController.getAuthURI()+  "/auth/authQna" );
        /* Send the POST request for authentication */ 
        //System.out.println(service.getURI().toString());
        //sSystem.out.println(map.toString());
        
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );
        System.out.println(map.toString());
        System.out.println("DEBUG Message[BasicAuth] : call API" + response.toString());
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