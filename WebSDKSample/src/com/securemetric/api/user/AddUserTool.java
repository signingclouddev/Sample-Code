/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
    package com.securemetric.api.user;

import com.securemetric.api.auth.*;
import com.google.gson.Gson;
import com.securemetric.web.util.APIController;
import com.securemetric.web.util.RandomNameGenerator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import javax.ws.rs.core.MediaType;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/**
 *
 * @author auyong
 */
public class AddUserTool {
    private String authToken ="";
    private String secretCode = "";
    private String username = "";
    private String ipAddress = "172.18.14.15";  /* The client’s IP address. Optional Field */ 
    private String userAgent = "My user agent"; /* The client’s user agent. Optional Field */
    private String formUsername = "";        
    public String  add(String loginEmail, String centoken, int usercount, String name) 
    {
        String addStatus = "";
        /*POST API REQUEST*/
        String authString = null;

        String firstName = name.substring(0, name.indexOf("_"));
        String lastName = name.substring(name.indexOf("_")+1);
        String username = firstName+usercount;
        String userEmail = username+"@testserver.com";
        String address = "";
        String city = "";
        String zip = "";
        String state = "";
        String userCountryId = "108";
        String userTimeZoneId = "";
        String roles = "3";
        String userGroupName = "default";
        String userCompanyName = "";

        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ( "firstName" , firstName);
        map.put ("lastName" , lastName);
        map.put ("username" , username);
        map.put ("userEmail" , userEmail);
        map.put ("address" , address);
        map.put ("city" , city);
        map.put ("zip" , zip);
        map.put ("state" , state);
        map.put ("userCountryId" , userCountryId);
        map.put ("userTimeZoneId" , userTimeZoneId);
        map.put ("roles" , roles);
        map.put ("userGroupName" , userGroupName);
        map.put ("userCompanyName" , userCompanyName);
        this.formUsername = username;
        try {
            APIController.TrustAllService();
        }
        catch (Exception e) {
            
        }
        
        /* Send the POST request for authentication */ 
        Gson gson = new Gson (); /*GSON library*/     
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        
        WebResource service = client.resource ( APIController.getSecureURI()).path("user").path("add").path(loginEmail);
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(centoken);
        pathBuilder.append("/");
        /* Send the POST request for authentication */ 
        ClientResponse response = service.path(pathBuilder.toString()).accept ( MediaType.APPLICATION_JSON ).put ( ClientResponse.class , gson.toJson ( map ) );
    //    ClientResponse clientResponse = service.path ( "user" ).path ( "read" ).path ( sysConfig.getSystemProp ( Config.INTEGRATION_KEY_KEY ) ).path ( 1 ).accept ( MediaType.APPLICATION_JSON ).get ( ClientResponse.class );
    
        /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            
            authString = response.getEntity(String.class);
            HashMap<String , String> responseMap = gson.fromJson ( authString , HashMap.class );

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
            System.out.println(response.getStatus());
            addStatus = username + " added failed";
        }

        return addStatus;
    }
    
    public HashMap<String , String>  Authenticate(String username, String pkiPublicCert) 
    {
        
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;
        try {
            
            try {
                APIController.TrustAllService();
            }
            catch (Exception e) {

            }
            
            /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
            String integrationKey = APIController.getIntegrationKey();  
            /* The current time in second (GMT+00:00) */ 
            String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
            /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
            String secretKey = APIController.getSecretKey() ;  

            ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
            userAgent = "My user agent"; /* The client’s user agent. Optional Field */
            String authToken = ""; /* The previous generated authToken. This is optional. You can leave it empty*/ 
            String supportFido = ""; /* Put in the value “true” or “false”. Or leave it empty */

            String certFingerprintSha1  = pkiPublicCert;

            /* Put all the required parameters for basic authentication */ 
            HashMap<String,String> map = new HashMap( ) ; 
            map.put ( "username" , username ) ; 
          //  map.put ( "certFingerprintSha1" , certFingerprintSha1 ) ;
            map.put ( "password" , certFingerprintSha1 ) ;
            map.put ( "integrationKey" , integrationKey ) ; 
            map.put ( "unixTimestamp" , unixTimestamp ) ; 
            map.put ( "authToken" , authToken ) ; 
            map.put ( "supportFido" , supportFido ) ;  
            map.put ( "ipAddress" , ipAddress ) ; /* The client’s IP address. Optional Field */ 
            map.put ( "userAgent" , userAgent ) ; /* The client’s user agent. Optional Field */ 
            map.put ( "hmac" ,  APIController.calculateHmac256 (
                       secretKey , 
                       username + certFingerprintSha1 + integrationKey + unixTimestamp + authToken + supportFido + ipAddress + userAgent)) ; 

            Gson gson = new Gson (); /*GSON library*/     
            ClientConfig config = new DefaultClientConfig ();
            com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );

            WebResource service = client.resource ( APIController.getBaseURI()+  "/auth/authBasic" );
            /* Send the POST request for authentication */ 
            ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );

            /* Read the output returned from the authentication */ 
            if(response.getStatus() == 200)
            {    
                authString = response.getEntity ( String.class ) ;  
                responseMap = gson.fromJson ( authString , HashMap.class );
            }
        
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return responseMap; 
    }
    
     public void refreshAuthToken (String username, String cenToken)
    {
        ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
        userAgent = "My user agent"; /* The client’s user agent. Optional Field */

        String unixTimestamp = String.valueOf ( System.currentTimeMillis () / 1000L );
     //   String authToken = "fb9ewIhSytdxLaDImt6+u7dANxToQoYjVGyqjm1HTIc=";
     //   String secretKey = APIController.getSecretKey() ; 
        
        HashMap<String , String> map = new HashMap<String , String> ();
        map.put ( "username" , username );
        map.put ( "authToken" , authToken );
        map.put ( "unixTimestamp" , String.valueOf ( unixTimestamp ) );
        map.put ( "ipAddress" , ipAddress );
        map.put ( "userAgent" , userAgent );

        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        WebResource service = client.resource ( APIController.getBaseURI());

        Gson gson = new Gson ();
      //  String cenToken = APIController.calculateHmac256 ( secretKey , username +  authToken ) ;
        ClientResponse clientResponse = service.path ( "auth" ).path ( "sessioncheck" ).path(username).path(cenToken).accept ( MediaType.APPLICATION_JSON ).get ( ClientResponse.class  );
        System.out.println(clientResponse);
        if ( clientResponse.getStatus () == ClientResponse.Status.OK.getStatusCode () )
        {
            String json = clientResponse.getEntity ( String.class );
            HashMap<String , String> responseMap = gson.fromJson ( json , HashMap.class );
            System.out.println(responseMap);
            String code = responseMap.get ( "code" );
            String message = responseMap.get ( "message" );

            if ( code.equals ( "0" ) )
            {
                String authJson = responseMap.get ( "object" );
                HashMap<String , String> authMap = gson.fromJson ( authJson , HashMap.class );
                authToken = authMap.get ( "authToken" );
                secretCode = authMap.get ( "secretCode" );

                System.out.println ( "Renew token success." );
            }
            else
            {
                System.out.println ( "Renew token error:" + message );
            }
        }
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
    
//    TimerTask task = new TimerTask() {
//
//        @Override
//        public void run() {
//            refreshAuthToken(username);
//        }
//    };
    
    public void process (String username, String pkiPublicCert) throws Exception {
        
        
        HashMap<String , String> authString; 
        authString = this.Authenticate(username, pkiPublicCert);
       // authString = this.logout(username);
        String authJson = authString.get ( "object" );  /* Return object on json format */
        System.out.println ( authJson );
//      //  authJson = null;
//        if( authJson != null ) {
//            
//          
//            
//            /* Parse the result of the authentication to String object*/ 
//            Gson gson = new Gson();
//            HashMap<String , String> authMap = gson.fromJson ( authJson , HashMap.class );
//            this.username = username;
//            authToken = authMap.get("authToken");
//            secretCode = authMap.get("secretCode");
//            String email = authMap.get("email");
//
//          //  Timer timer = new Timer();
//         //   timer.schedule(task, 45000);
//
//
//            String cenToken = APIController.generateCenToken(secretCode,username + authToken);
//            System.out.println ( "generateCenToken success." );
//            String rtnMessage = "";
          //  System.out.println ( "Start add user ..." );
            //10000000
          //  refreshAuthToken(username,"ac850aa4c06cedb579d3a80392cbcbd7b1f61672513d1cf4a0fcc98f73fe5533");
            
//            registerSMSToken(username, cenToken, username, "+971971971", "1");
            RandomNameGenerator rnd = new RandomNameGenerator(1818);
            
            for(int usercount = 1;usercount <=1;usercount++) {

              //  rtnMessage = this.add(username, cenToken, usercount, rnd.next());
             //   rtnMessage =  registerSMSToken(username, cenToken, this.formUsername , "+601213"+usercount, "1");
                System.out.println(rnd.next());

            }
            System.out.println ( "Complete add user ..." );
//        }
//        else {
//            System.out.println ( "Authenticate Failed ..." );
//        }
    }
    
    public HashMap<String , String>  logout(String username) 
    {
        
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;
        try {
            
            /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
            String integrationKey = APIController.getIntegrationKey();  
            /* The current time in second (GMT+00:00) */ 
            String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
            /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
            String secretKey = APIController.getSecretKey() ;  

            ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
            userAgent = "My user agent"; /* The client’s user agent. Optional Field */
            String authToken = "UqMZDLXpgETnGTfFZmpIHgyr2OBnsy9rOqJ3JSaRTsw="; /* The previous generated authToken. This is optional. You can leave it empty*/ 
            String supportFido = ""; /* Put in the value “true” or “false”. Or leave it empty */

          //  String certFingerprintSha1  = pkiPublicCert;

            /* Put all the required parameters for basic authentication */ 
            HashMap<String,String> map = new HashMap( ) ; 
            map.put ( "username" , username ) ; 
          //  map.put ( "certFingerprintSha1" , certFingerprintSha1 ) ;
          //  map.put ( "password" , certFingerprintSha1 ) ;
            map.put ( "integrationKey" , integrationKey ) ; 
            map.put ( "unixTimestamp" , unixTimestamp ) ; 
            map.put ( "authToken" , authToken ) ; 
       //     map.put ( "supportFido" , supportFido ) ;  
         //   map.put ( "ipAddress" , ipAddress ) ; /* The client’s IP address. Optional Field */ 
         //   map.put ( "userAgent" , userAgent ) ; /* The client’s user agent. Optional Field */ 
            map.put ( "hmac" ,  APIController.calculateHmac256 (
                       secretKey , 
                       username +  integrationKey + unixTimestamp + authToken )) ; 

            Gson gson = new Gson (); /*GSON library*/     
            ClientConfig config = new DefaultClientConfig ();
            com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );

            WebResource service = client.resource ( APIController.getBaseURI()+  "/auth/logout" );
            /* Send the POST request for authentication */ 
            ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );

            /* Read the output returned from the authentication */ 
            if(response.getStatus() == 200)
            {    
                authString = response.getEntity ( String.class ) ;  
                responseMap = gson.fromJson ( authString , HashMap.class );
            }
        
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return responseMap; 
    }
    
    public static void main(String[] args) throws Exception {

        
      //  AddUserTool user = new AddUserTool();
        String username = "auyong"; /* input username */
       // String pkiPublicCert = "42 22 28 12 * root /opt/jboss/bin/management/script/management/schedule_backup.sh 3354241235751663a933eba9eb8f8e5a13f7eab339e443b7c9f3be99da2dcff3 1"; 
       
       // System.out.println(pkiPublicCert.substring(pkiPublicCert.length()));
        AddUserTool adduser = new AddUserTool();
        //adduser.refreshAuthToken(username,"d4eb030132afa8c501bcb11577e0f1dcddb8e351bd908324223bb740edcacde8");
        adduser.process(username, "abcd123456");
        
        System.exit(0); 
    }
}