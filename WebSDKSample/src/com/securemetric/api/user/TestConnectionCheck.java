/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
    package com.securemetric.api.user;

import com.google.gson.Gson;
import com.securemetric.web.util.APIController;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.net.URI;
import java.util.HashMap;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;


/**
 *
 * @author auyong
 */
public class TestConnectionCheck {
    private String authToken ="";
    private String secretCode = "";
    private String username = "";
    private String ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
    private String userAgent = "My user agent"; /* The client’s user agent. Optional Field */
            
    
    public HashMap<String , String>  Authenticate(String username, String pkiPublicCert) 
    {
        
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;
        try {
            
            /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
            String integrationKey = TestConnectionCheck.getIntegrationKey();  
            /* The current time in second (GMT+00:00) */ 
            String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
            /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
            String secretKey = TestConnectionCheck.getSecretKey() ;  

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

            WebResource service = client.resource ( this.getBaseURI()+  "/auth/authBasic" );
            /* Send the POST request for authentication */ 
            ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );
            System.out.println(response);
            /* Read the output returned from the authentication */ 
            if(response.getStatus() == 200)
            {    
               
                authString = response.getEntity ( String.class ) ;  
                 System.out.println(authString);
                responseMap = gson.fromJson ( authString , HashMap.class );
            }
        
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return responseMap; 
    }
    
     public void sessioncheck (String username, String cenToken)
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
        WebResource service = client.resource ( TestConnectionCheck.getBaseURI());

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

                System.out.println ( "Session is active." );
            }
            else
            {
                System.out.println ( "Session not active/not found:" + message );
            }
        }
    }

    
    public void loginAndChecksession (String username, String password) throws Exception {
        
        
        HashMap<String , String> authString; 
        authString = this.Authenticate(username, password);;
        String authJson = authString.get ( "object" );  /* Return object on json format */
        if( authJson != null ) {
            
            System.out.println ( authJson );
            
            /* Parse the result of the authentication to String object*/ 
            Gson gson = new Gson();
            HashMap<String , String> authMap = gson.fromJson ( authJson , HashMap.class );
            this.username = username;
            authToken = authMap.get("authToken");
            secretCode = authMap.get("secretCode");
            
            System.out.println ( "authToken ... = " + authToken);
            System.out.println ( "secretCode ... = " + secretCode);
            
            String cenToken = APIController.generateCenToken(secretCode,username + authToken);
            System.out.println ( "generateCenToken success." );
           
            sessioncheck(username,cenToken);

        }
        else {
            System.out.println ( "Authenticate Failed ..." );
        }
    
    }
    
    public void logoutAndChecksession (String username, String secretCode, String authToken) throws Exception {
        
        
        HashMap<String , String> authString; 
        authString = this.logout(username, authToken);
        String authJson = authString.get ( "object" );  /* Return object on json format */
        if( authJson != null ) {
            
            System.out.println ( "Logout Successful" );

            String cenToken = APIController.generateCenToken(secretCode,username + authToken);
            System.out.println ( "generateCenToken success." );
        
            sessioncheck(username,cenToken);
        }
        else {
            System.out.println ( "Logout Failed ..." );
        }
    }    
    
    public HashMap<String , String>  logout(String username, String authToken) 
    {
        
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;
        try {
            
            /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
            String integrationKey = TestConnectionCheck.getIntegrationKey();  
            /* The current time in second (GMT+00:00) */ 
            String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
            /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
            String secretKey = TestConnectionCheck.getSecretKey() ;  

            ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
            userAgent = "My user agent"; /* The client’s user agent. Optional Field */

            /* Put all the required parameters for basic authentication */ 
            HashMap<String,String> map = new HashMap( ) ; 
            map.put ( "username" , username ) ; 
            map.put ( "integrationKey" , integrationKey ) ; 
            map.put ( "unixTimestamp" , unixTimestamp ) ; 
            map.put ( "authToken" , authToken ) ;  
            map.put ( "hmac" ,  APIController.calculateHmac256 (
                       secretKey , 
                       username +  integrationKey + unixTimestamp + authToken )) ; 

            Gson gson = new Gson (); /*GSON library*/     
            ClientConfig config = new DefaultClientConfig ();
            com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );

            WebResource service = client.resource ( this.getBaseURI()+  "/auth/logout" );
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
    
    public static URI getBaseURI ()
    {
       // return UriBuilder.fromUri ( "https://192.168.6.13/CentagateWS/webresources" ).build ();
        return UriBuilder.fromUri ( "https://office.securemetric.com:444/CentagateWS/webresources" ).build ();
    }

    public static String getIntegrationKey ()
    {
        //return "de95daeabf5f03d875f631f02b69f931b3b3c4d5e7f43a1a77e3e85bdc2471fb"; 
        return "2f6ab70aa10288cca3b9a135241cdc5c44ae9404c06dcda0abf4216e5956220b";
    }
     
    public static String getSecretKey()
    {
        ///return "QpHu9hXUo0mc";
        return "DfbXKTexp4eR";
    }
    
    public static void main(String[] args) throws Exception {

        //
      //  AddUserTool user = new AddUserTool();
        String username = "auyong"; /* input username */
        String password = "abcd12345"; 

        TestConnectionCheck testConnectionCheck = new TestConnectionCheck();
        testConnectionCheck.loginAndChecksession(username, password);
        
        //logout require input secretCode and authToken during your login just now
       // testConnectionCheck.logoutAndChecksession(username, "secretCode", "authToken");
        
        System.exit(0); 
    }
}