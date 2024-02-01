/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
    package com.securemetric.api.user;

import com.google.gson.Gson;
import com.securemetric.api.auth.BasicAuth;
import com.securemetric.api.auth.SimplePKIAuth;
import com.securemetric.web.util.APIController;
import com.securemetric.web.util.EnglishNumberToWords;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.util.HashMap;
import javax.ws.rs.core.MediaType;


/**
 *
 * @author auyong
 */
public class AddUserToolBPI {
    private String authToken ="";
    private String secretCode = "";
    private String ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
    private String userAgent = "My user agent"; /* The client’s user agent. Optional Field */
            
    public String  add( String admin_username, String centoken, String firstName, String lastName,
                        String username, String userApp, String userGroup,String userUniqueId, String userClientId) 
    {
        String addStatus = "";
        /*POST API REQUEST*/
        String authString = null;

        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
//        map.put ( "firstName" , firstName);
//        map.put ("lastName" , lastName);
        map.put ("username" , username);
        map.put ("cenToken" , centoken);
//        map.put ("userApp" , userApp); // APPID
//        map.put("userGroup",userGroup);
//        map.put ("userUniqueId" , userUniqueId); // RM Number
//        map.put ("userClientId" , userClientId); // EOL ID
//        map.put ( "userAdditionalData1","1" );
//        map.put ( "userAdditionalData2","2" );
//        map.put ( "userAdditionalData3","3" );
//        map.put ( "userAdditionalData4","4" );
//        map.put ( "userAdditionalData5","5" );
        
        map.put ( "newPassword","foo123" );
   //     map.put ( "userAdditionalData5","5" );
        
        try {
            APIController.TrustAllService();
        }
        catch (Exception e) {
            
        }
        
        /* Send the POST request for authentication */ 
        Gson gson = new Gson (); /*GSON library*/     
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        
       // WebResource service = client.resource ( APIController.getSecureURI()).path("user").path("registerUserActivate").path(admin_username);
        WebResource service = client.resource ( APIController.getSecureURI()).path("password").path("resetbyusername").path(admin_username);
        /* Send the POST request for authentication */ 
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).put ( ClientResponse.class , gson.toJson ( map ) );
        
        /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            
            authString = response.getEntity(String.class);
            HashMap<String , String> responseMap = gson.fromJson ( authString , HashMap.class );
            System.out.println(authString);
                    
            String code = responseMap.get ( "code" );
            String message = responseMap.get ( "message" );

            if(Integer.parseInt(code) != 0) {
                addStatus = username + " added failed. " + message;
            }
            else {
                addStatus = username + " added succesfully";
            }
            
        }
        else {
            addStatus = username + " added failed";
        }

        return addStatus;
    }
    
    public String  registerSMSToken( String admin_username, String centoken, String username,String tokenSn, String tokenType) 
    {
        String addStatus = "";
        /*POST API REQUEST*/
        String authString = null;

        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ("username" , username);
        map.put ("cenToken" , centoken);
        map.put ("tokenSn" , tokenSn); // APPID
        map.put("tokenType",tokenType);
        
        try {
            APIController.TrustAllService();
        }
        catch (Exception e) {
            
        }
        
        /* Send the POST request for authentication */ 
        Gson gson = new Gson (); /*GSON library*/     
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        
        WebResource service = client.resource ( APIController.getSecureURI()).path("token").path("registerActiveToken").path(admin_username);
        /* Send the POST request for authentication */ 
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).put ( ClientResponse.class , gson.toJson ( map ) );

        /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            
            authString = response.getEntity(String.class);
            HashMap<String , String> responseMap = gson.fromJson ( authString , HashMap.class );
            System.out.println(authString);
                    
            String code = responseMap.get ( "code" );
            String message = responseMap.get ( "message" );

            if(Integer.parseInt(code) != 0) {
                addStatus = username + " added failed. " + message;
            }
            else {
                addStatus = username + " added succesfully";
            }
            
        }
        else {
            addStatus = username + " added failed";
        }

        return addStatus;
    }
    
     public String  resetPassword( String admin_username, String centoken, String username) 
    {
        String addStatus = "";
        /*POST API REQUEST*/
        String authString = null;

        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ("username" , username);
        map.put ("cenToken" , centoken);
        map.put ("newPassword" , "foo123"); // APPID
      //  map.put("notifyUser","");
        
        try {
            APIController.TrustAllService();
        }
        catch (Exception e) {
            
        }
        
        /* Send the POST request for authentication */ 
        Gson gson = new Gson (); /*GSON library*/     
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        
        WebResource service = client.resource ( APIController.getSecureURI()).path("password").path("resetbyusername").path(admin_username);
        /* Send the POST request for authentication */ 
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).put ( ClientResponse.class , gson.toJson ( map ) );

        /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            
            authString = response.getEntity(String.class);
            HashMap<String , String> responseMap = gson.fromJson ( authString , HashMap.class );
            System.out.println(authString);
                    
            String code = responseMap.get ( "code" );
            String message = responseMap.get ( "message" );

            if(Integer.parseInt(code) != 0) {
                addStatus = username + " added failed. " + message;
            }
            else {
                addStatus = username + " added succesfully";
            }
            
        }
        else {
            addStatus = username + " added failed";
        }

        return addStatus;
    }

    public void process (String admin_username, String admin_password, String pkiPublicCert) throws Exception {
        
        
        HashMap<String , String> authString; 
        BasicAuth basicAuth = new BasicAuth();
        authString = basicAuth.Authenticate(admin_username, admin_password);
        String authJson = authString.get ( "object" );  /* Return object on json format */
        if( authJson != null ) {
            
            System.out.println ( "Authenticate succesfully ..." );
            
            /* Parse the result of the authentication to String object*/ 
            Gson gson = new Gson();
            HashMap<String , String> authMap = gson.fromJson ( authJson , HashMap.class );
            authToken = authMap.get("authToken");            
            secretCode = authMap.get("secretCode");
            System.out.println ( "secretCode = " + secretCode );
            if(secretCode == null || secretCode.isEmpty()) {
            
                //perform MFA
                SimplePKIAuth simplePKIAuth = new SimplePKIAuth();
                authString = simplePKIAuth.Authenticate(admin_username, pkiPublicCert, authToken);

                authJson = authString.get ( "object" );  /* Return object on json format */
                if( authJson != null ) {
                     /* Parse the result of the authentication to String object*/ 
                    authMap = gson.fromJson ( authJson , HashMap.class );
                    authToken = authMap.get("authToken");            
                    secretCode = authMap.get("secretCode");
                    String cenToken = APIController.generateCenToken(secretCode,admin_username + authToken);
                    System.out.println ( "generateCenToken success." );
                    String rtnMessage = "";
                    System.out.println ( "Start add user ..." );

                    String firstName = "test1";
                    String lastName = "test1";
                    String username = "aybpitest2";
                    String userApp = "APP1";
                    String userGroup = "APP1";
                    String userUniqueId = "00831212779912";
                    String userClientId = "EOLTEST55";

//                    rtnMessage = this.add(admin_username, cenToken, firstName, lastName, username, userApp, userGroup, userUniqueId, userClientId );
                    
                     rtnMessage = this.resetPassword(admin_username, cenToken, username);

                    System.out.println(rtnMessage);

                    System.out.println ( "Complete add user ..." );
                }
                else {
                    System.out.println ( "Authenticate Failed ..." );
                }
            } else {
                authJson = authString.get ( "object" );  /* Return object on json format */
                if( authJson != null ) {
                     /* Parse the result of the authentication to String object*/ 
                    authMap = gson.fromJson ( authJson , HashMap.class );
                    authToken = authMap.get("authToken");            
                    secretCode = authMap.get("secretCode");
                    String cenToken = APIController.generateCenToken(secretCode,admin_username + authToken);
                    System.out.println ( "generateCenToken success." );
                    String rtnMessage = "";
                   // System.out.println ( "Start add user ..." );

                    String firstName = "secure";
                    String lastName = "user";
                    String username = "secureuser";
                    String userGroup = "User";
                    String userApp = "Application1";
                    String userUniqueId = "12345678";
                    String userClientId = "SECUREMETRIC";
                    String numberInWord = "";
                    String tokenType="1";
 //                   rtnMessage = this.resetPassword(admin_username, cenToken, "ios1");
                    for(int usercount = 0;usercount <=100;usercount++) {
                    
                        numberInWord = EnglishNumberToWords.convert(usercount);    
                        rtnMessage = this.add(admin_username, cenToken, firstName, lastName + " " + numberInWord , username.concat(String.valueOf(usercount)), userApp, userGroup, userUniqueId, userClientId );
                       // rtnMessage = this.registerSMSToken(admin_username, cenToken, username.concat(String.valueOf(usercount)), "+601212345"+usercount, tokenType);
                      //  rtnMessage = this.resetPassword(admin_username, cenToken, username.concat(String.valueOf(usercount)));
                        System.out.println(rtnMessage);
                    }

                    System.out.println ( "resetPassword ..." );
                }
            }
        }
        else {
            System.out.println ( "Authenticate Failed ..." );
        }
    }
    
    public static void main(String[] args) throws Exception {

        
        AddUserToolBPI user = new AddUserToolBPI();
        String admin_username = "auyong"; /* input username */
        String admin_password = "abcd123456"; /* input username */
        String pkiPublicCert = "fb2d903e41e39ce685b261d4876c29223ed7d7b7"; 

        user.process(admin_username, admin_password, pkiPublicCert);

        System.exit(0); 
    }
}