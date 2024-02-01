/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.api.user;

import com.securemetric.api.auth.*;
import com.google.gson.Gson;
import com.securemetric.web.util.APIController;
import com.securemetric.web.util.EnglishNumberToWords;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.util.HashMap;
import javax.ws.rs.core.MediaType;
import java.util.Iterator;


/**
 *
 * @author auyong
 */
public class OneTimePinTest {

    
    public HashMap<String , String>  requestmobileprovision(String loginEmail, String centoken, String email) 
    {
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;

        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ("username" , "auyong2");
        map.put ("status" , "1");
        map.put ("cenToken" , centoken);
        try {
            APIController.TrustAllService();
        }
        catch (Exception e) {
            responseMap.put ( "code" , "1");
            responseMap.put ( "message" , "" );
            responseMap.put ( "object" , "" );
            return responseMap; 
        }
        /* Send the POST request for authentication */ 
        Gson gson = new Gson (); /*GSON library*/     
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        
        WebResource service = client.resource ( APIController.getSecureURI()).path("device").path("register").path("onetimepin").path(email);
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).put ( ClientResponse.class , gson.toJson ( map ) );
    //    ClientResponse clientResponse = service.path ( "user" ).path ( "read" ).path ( sysConfig.getSystemProp ( Config.INTEGRATION_KEY_KEY ) ).path ( 1 ).accept ( MediaType.APPLICATION_JSON ).get ( ClientResponse.class );
    
        /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity(String.class);
            responseMap = gson.fromJson ( authString , HashMap.class );
        }
        else {
            System.out.println("response.getStatus() = "+response);
            System.out.println("response.getStatus() = "+response.getStatus());
            responseMap.put ( "code" , String.valueOf ( response.getStatus()) );
            responseMap.put ( "message" , "" );
            responseMap.put ( "object" , "" );
        }

        return responseMap; 
    }
    
    
    public HashMap<String , String>  getStatusReport(String loginEmail, String centoken, String email) 
    {
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;

        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ("username" , "auyong2");
   //     map.put ("status" , "1");
        map.put ("cenToken" , centoken);
        try {
            APIController.TrustAllService();
        }
        catch (Exception e) {
            responseMap.put ( "code" , "1");
            responseMap.put ( "message" , "" );
            responseMap.put ( "object" , "" );
            return responseMap; 
        }
        /* Send the POST request for authentication */ 
        Gson gson = new Gson (); /*GSON library*/     
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        
        WebResource service = client.resource ( APIController.getSecureURI()).path("token").path("getStatus").path("report").path(email);
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).put ( ClientResponse.class , gson.toJson ( map ) );
    //    ClientResponse clientResponse = service.path ( "user" ).path ( "read" ).path ( sysConfig.getSystemProp ( Config.INTEGRATION_KEY_KEY ) ).path ( 1 ).accept ( MediaType.APPLICATION_JSON ).get ( ClientResponse.class );
    
        /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity(String.class);
            responseMap = gson.fromJson ( authString , HashMap.class );
        }
        else {
            System.out.println("response.getStatus() = "+response);
            System.out.println("response.getStatus() = "+response.getStatus());
            responseMap.put ( "code" , String.valueOf ( response.getStatus()) );
            responseMap.put ( "message" , "" );
            responseMap.put ( "object" , "" );
        }

        return responseMap; 
    }
    
    public HashMap<String , String>  requestPUKCode(String loginEmail, String centoken, String email) 
    {
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;

        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ("username" , "auyong");
        map.put ("status" , "1");
        map.put ("cenToken" , centoken);
        try {
            APIController.TrustAllService();
        }
        catch (Exception e) {
            responseMap.put ( "code" , "1");
            responseMap.put ( "message" , "" );
            responseMap.put ( "object" , "" );
            return responseMap; 
        }
        /* Send the POST request for authentication */ 
        Gson gson = new Gson (); /*GSON library*/     
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        
        WebResource service = client.resource ( APIController.getSecureURI()).path("token").path("requestPukCode").path("email").path(centoken);
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).put ( ClientResponse.class , gson.toJson ( map ) );
    //    ClientResponse clientResponse = service.path ( "user" ).path ( "read" ).path ( sysConfig.getSystemProp ( Config.INTEGRATION_KEY_KEY ) ).path ( 1 ).accept ( MediaType.APPLICATION_JSON ).get ( ClientResponse.class );
    
        /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity(String.class);
            responseMap = gson.fromJson ( authString , HashMap.class );
        }
        else {
            System.out.println("response.getStatus() = "+response);
            System.out.println("response.getStatus() = "+response.getStatus());
            responseMap.put ( "code" , String.valueOf ( response.getStatus()) );
            responseMap.put ( "message" , "" );
            responseMap.put ( "object" , "" );
        }

        return responseMap; 
    }
    
    public HashMap<String , String>  registerActiveToken(String loginEmail, String centoken, String email) 
    {
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;

        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ("username" , "auyong2");
        map.put ("tokenType" , "2");
        map.put ("tokenSn" , "3608634100004");
        map.put ("cenToken" , centoken);
        map.put ("activateToken" , "1");
        try {
            APIController.TrustAllService();
        }
        catch (Exception e) {
            responseMap.put ( "code" , "1");
            responseMap.put ( "message" , "" );
            responseMap.put ( "object" , "" );
            return responseMap; 
        }
        /* Send the POST request for authentication */ 
        Gson gson = new Gson (); /*GSON library*/     
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        
        WebResource service = client.resource ( APIController.getSecureURI()).path("token").path("registerActiveToken").path(loginEmail);
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).put ( ClientResponse.class , gson.toJson ( map ) );
    //    ClientResponse clientResponse = service.path ( "user" ).path ( "read" ).path ( sysConfig.getSystemProp ( Config.INTEGRATION_KEY_KEY ) ).path ( 1 ).accept ( MediaType.APPLICATION_JSON ).get ( ClientResponse.class );
    
        /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity(String.class);
            responseMap = gson.fromJson ( authString , HashMap.class );
        }
        else {
            System.out.println("response.getStatus() = "+response);
            System.out.println("response.getStatus() = "+response.getStatus());
            responseMap.put ( "code" , String.valueOf ( response.getStatus()) );
            responseMap.put ( "message" , "" );
            responseMap.put ( "object" , "" );
        }

        return responseMap; 
    }
    
     public HashMap<String , String>  revokedToken(String loginEmail, String centoken, String email) 
    {
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;

        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ("username" , "authtest");
      //  map.put ("tokenType" , "2");
//        map.put ("tokenSn" , "3608634100004");
        map.put ("cenToken" , centoken);
//        map.put ("activateToken" , "1");
        try {
            APIController.TrustAllService();
        }
        catch (Exception e) {
            responseMap.put ( "code" , "1");
            responseMap.put ( "message" , "" );
            responseMap.put ( "object" , "" );
            return responseMap; 
        }
        /* Send the POST request for authentication */ 
        Gson gson = new Gson (); /*GSON library*/     
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        
        WebResource service = client.resource ( APIController.getSecureURI()).path("token").path("revoked").path(loginEmail);
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).put ( ClientResponse.class , gson.toJson ( map ) );
    //    ClientResponse clientResponse = service.path ( "user" ).path ( "read" ).path ( sysConfig.getSystemProp ( Config.INTEGRATION_KEY_KEY ) ).path ( 1 ).accept ( MediaType.APPLICATION_JSON ).get ( ClientResponse.class );
    
        /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity(String.class);
            responseMap = gson.fromJson ( authString , HashMap.class );
        }
        else {
            System.out.println("response.getStatus() = "+response);
            System.out.println("response.getStatus() = "+response.getStatus());
            responseMap.put ( "code" , String.valueOf ( response.getStatus()) );
            responseMap.put ( "message" , "" );
            responseMap.put ( "object" , "" );
        }

        return responseMap; 
    }
   
     public HashMap<String , String>  unbindanddelete(String loginEmail, String centoken, String email) 
    {
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;

        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ("username" , "auyonguser");
      //  map.put ("tokenType" , "2");
//        map.put ("tokenSn" , "3608634100004");
        map.put ("cenToken" , centoken);
//        map.put ("activateToken" , "1");
        try {
            APIController.TrustAllService();
        }
        catch (Exception e) {
            responseMap.put ( "code" , "1");
            responseMap.put ( "message" , "" );
            responseMap.put ( "object" , "" );
            return responseMap; 
        }
        /* Send the POST request for authentication */ 
        Gson gson = new Gson (); /*GSON library*/     
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        
        WebResource service = client.resource ( APIController.getSecureURI()).path("user").path("unbindAndDelete").path(loginEmail);
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).put ( ClientResponse.class , gson.toJson ( map ) );
    //    ClientResponse clientResponse = service.path ( "user" ).path ( "read" ).path ( sysConfig.getSystemProp ( Config.INTEGRATION_KEY_KEY ) ).path ( 1 ).accept ( MediaType.APPLICATION_JSON ).get ( ClientResponse.class );
    
        /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity(String.class);
            responseMap = gson.fromJson ( authString , HashMap.class );
        }
        else {
            System.out.println("response.getStatus() = "+response);
            System.out.println("response.getStatus() = "+response.getStatus());
            responseMap.put ( "code" , String.valueOf ( response.getStatus()) );
            responseMap.put ( "message" , "" );
            responseMap.put ( "object" , "" );
        }

        return responseMap; 
    }
     
    public static void main(String[] args) throws Exception {

        HashMap<String , String> authString; 
        int code;
        String authJson;
        BasicAuth basicAuth = new BasicAuth();
        String admin_username = "auyong";
        String admin_password = "abcd123456";
        authString = basicAuth.Authenticate(admin_username, admin_password);
        code = Integer.parseInt(authString.get ( "code" ));  /* Return status code */
        authJson = authString.get ( "object" );  /* Return object on json format */
        
        if( code != 0 ) {
            System.exit(0); 
        }
        
        /* Parse the result of the authentication to String object*/ 
        Gson gson = new Gson();
        HashMap<String , String> authMap = gson.fromJson ( authJson , HashMap.class );
        String authToken = authMap.get("authToken");
        String secretCode = authMap.get("secretCode");
        String userId = authMap.get("userId");
       
        System.out.println("userId = " + userId);
        System.out.println("secretCode = " + secretCode);
        System.out.println("username = " + admin_username);
        System.out.println("authToken = " + authToken);
        String cenToken = APIController.generateCenToken(secretCode,admin_username + authToken);
        System.out.println("cenToken = " + cenToken);

        OneTimePinTest user = new OneTimePinTest();
        
        //authString = user.add(email, cenToken);
        
        authString = user.unbindanddelete(admin_username, cenToken, admin_username);
         System.out.println("authString = " + authString);
        if(authString != null ) {

            code = Integer.parseInt(authString.get ( "code" ));  /* Return status code */
            System.out.println("Add User status = " + code);/* Return status code */ 
            System.out.println("Add User smsCode = " + authString.get ( "smsCode" )); /* Return message */
            authJson = authString.get ( "object" );  /* Return object on json format */
            System.out.println("Add User object = " + authString.get ( "object" )); /* Return message */
            if( code == 0 ) {

                /* Parse the result of the authentication to String object*/         
                authMap = gson.fromJson ( authJson , HashMap.class );
                Iterator it = authMap.keySet().iterator();

                /* List out return attributes */
                while (it.hasNext())
                {
                    Object key = it.next();
                    String val = authMap.get(key);
                    System.out.println("Attributes : "+ key +", Value :" + val);
                }
            }
            System.out.println("Add User return message = " + authString.get ( "message" )); /* Return message */
//           
        }
    }
}