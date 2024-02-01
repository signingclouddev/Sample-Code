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
import java.security.InvalidKeyException;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.ws.rs.client.Client;

/**
 *
 * @author auyong
 */
public class RegisterPkiPkcs7 {
     private  String authToken ="";
     private  String secretCode = "";
     private  String username = "";
     public HashMap<String , String>  Authenticate(String username, String password) 
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

        String ipAddress = "192.168.0.1";  /* The client’s IP address. Optional Field */ 
        String userAgent = "My user agent"; /* The client’s user agent. Optional Field */
        String supportFido = "true"; /* Put in the value “true” or “false”. Or leave it empty */
        

        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ( "username" , username ) ;
        map.put ( "password" , password ) ;
        map.put ( "integrationKey" , integrationKey ) ; 
        map.put ( "unixTimestamp" , unixTimestamp ) ; 
      //  map.put ("authToken",authToken);
        map.put ( "ipAddress" , ipAddress ) ; /* The client’s IP address. Optional Field */ 
        map.put ( "userAgent" , userAgent ) ; /* The client’s user agent. Optional Field */ 
        map.put ( "hmac" ,  APIController.calculateHmac256 (
                   secretKey , 
                   username  + password  + integrationKey + unixTimestamp +ipAddress + userAgent)) ; 
        
        try {
            APIController.TrustAllService();
        }
        catch (Exception e) {
            
        }
        
        Gson gson = new Gson (); /*GSON library*/     
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );

        WebResource service = client.resource ( APIController.getBaseURI()+  "/auth/authBasic" );
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
    
    
    public HashMap<String , String>  register() throws Exception 
    {
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;


        /* The current time in second (GMT+00:00) */ 
        String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  

        
        String signature = "MIIFlgYJKoZIhvcNAQcCoIIFhzCCBYMCAQExDzANBgkqhkiG9w0BAQUFADATBgkqhkiG9w0BBwGgBgQEMTExMaCCA7swggO3MIICn6ADAgECAggfTP+kOkAtizANBgkqhkiG9w0BAQsFADBgMR0wGwYDVQQDDBRBbUJhbmsgTWFuYWdlbWVudCBDQTEcMBoGA1UECwwTU2VjdXJpdHkgT3BlcmF0aW9uczEUMBIGA1UECgwLQU1CQU5LR1JPVVAxCzAJBgNVBAYTAk1ZMB4XDTE0MDQyNDA2MTAxNloXDTE5MDQyNDA2MTAxNlowVjETMBEGA1UEAwwKU3VwZXJBZG1pbjEcMBoGA1UECwwTU2VjdXJpdHkgT3BlcmF0aW9uczEUMBIGA1UECgwLQU1CQU5LR1JPVVAxCzAJBgNVBAYTAk1ZMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtX/T8kXohg1BLJgRnixTZVK5mYNIfFZNIRwHtHoCy+yfpz2x53X8Q7ao5+1BMWLCtBeA4r5+jM5uZF5kDpwzVjaQ/feT6MF9+JjHTyMm1XlNF0TeXCRf33Bb6LBkyk5f7B6XAGNvsUG8UzWP9rBQPDcJmzm1Yu2wWbNCl4txT2YuHWQkdgM9YbOTG3FVqtu2yfEQ7EWtIbGrgShhn2qZOp9FIWw+st71RRvfLlhwrifEISmfRcuxpQ9vd35T+7OnDwzX4F3t+VewzoI/ApSoQ/0tbOQb/vqQyLNYS0ILfXMPHNPZy+/OGUEX4/PyMIXiamto1Gs38YMyXOa4eWqROQIDAQABo38wfTAdBgNVHQ4EFgQUQIB19Ilc19UFNQb9w0M5sza/bdMwDAYDVR0TAQH/BAIwADAfBgNVHSMEGDAWgBQJa+42mczudWvWzcW4LMGGUV13pzAOBgNVHQ8BAf8EBAMCBeAwHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMEMA0GCSqGSIb3DQEBCwUAA4IBAQAA3TqyBNELUhCaToMIrkJ//4ebpfjGB9xKja2/8454AkSjtoZ+lR0QCcu7sJuFq4CTp6MZaVHKPqR+ebRfu0HuwfVa9udGAIMwxLsAzhINeNrq0ZgG2ur3sUIsGkCGpu4qLAi3dVE0pydlAj3Szl0jRazF1H5jSYDBXPqkCmjYLVtK2r9V67cHOGoqqIuNkRQbM9ud4phoiZWbJE0sZ2cBxiTZf7cS8YCt42+HR7MzCALG5NxWfvPK5oCX4C8S8aCN2GsumTnwHJPOA2OxGYfC1gZ+Cc9DZ3zNeeNPSWuvb4KiW2rqOxubQLCgnMUAyy2X+iMpuCq41xekAshiyMErMYIBlzCCAZMCAQEwbDBgMR0wGwYDVQQDDBRBbUJhbmsgTWFuYWdlbWVudCBDQTEcMBoGA1UECwwTU2VjdXJpdHkgT3BlcmF0aW9uczEUMBIGA1UECgwLQU1CQU5LR1JPVVAxCzAJBgNVBAYTAk1ZAggfTP+kOkAtizANBgkqhkiG9w0BAQUFADANBgkqhkiG9w0BAQEFAASCAQAPipJEorT/dNpQyFKDe02ku6DbM32l14BDP0tkd7wjUqekizI0iOi24DmqLVBolgs6BCvZeMbDXTTnSXc4idfoWF9OUfldX9fG38nobWj/jhjeIwg3+1LJMOgsQO83E6DvMY6NAknP7EpSTuPqN4sNvxAmVu9tWbsktph7iNFAZp485GAeFGY8nJJe/SHFGhXotLbITGxFVp/w2kwrXLzuLSQR37zXs3PbRxhlatmgvpMLystB3UBOq34GjonbUpz2ZzM0NLYkbfiXOI10MENWKQGRSgn7p4fOzSLNAsSUhK8Q3sGQ6NuW8ELGJ+HN580e8I6kx6NYwspnQnBecrus";
        
        System.out.println ( secretCode );
        System.out.println ( username );
        System.out.println ( authToken );
        String cenToken = APIController.generateCenToken(secretCode,username + authToken);
        System.out.println ( authToken );
        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ( "username" , username ) ;
        map.put ( "signature" , signature ) ;
        map.put ( "algorithm" , "0" ) ; 
        map.put ( "timestamp" , unixTimestamp ) ; 
      //  map.put ("authToken",authToken);
        map.put ( "status" , "2" ) ; /* The client’s IP address. Optional Field */ 
        map.put ( "cenToken" ,  cenToken) ; 
        
        try {
            APIController.TrustAllService();
        }
        catch (Exception e) {
            
        }
        
        Gson gson = new Gson (); /*GSON library*/     
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );

        WebResource service = client.resource ( APIController.getBaseURI()+  "/cert/register/pkcs7/"+username );
        /* Send the POST request for authentication */ 
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).put ( ClientResponse.class , gson.toJson ( map ) );
        System.out.println(gson.toJson ( map ));
        /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity ( String.class ) ;  
            responseMap = gson.fromJson ( authString , HashMap.class );
        }

        return responseMap; 
    }
    
    public void process (String username, String password) throws Exception {
        
        
        HashMap<String , String> authString; 
        authString = this.Authenticate(username, password);
       // authString = this.logout(username);
        String authJson = authString.get ( "object" );  /* Return object on json format */
        if( authJson != null ) {
            
            System.out.println ( authJson );
            
            /* Parse the result of the authentication to String object*/ 
            Gson gson = new Gson();
            HashMap<String , String> authMap = gson.fromJson ( authJson , HashMap.class );
            this.username = username;
            authToken = authMap.get("authToken");
            secretCode = authMap.get("secretCode");
  
          //  Timer timer = new Timer();
         //   timer.schedule(task, 45000);


          //  String cenToken = APIController.generateCenToken(secretCode,username + authToken);
           // System.out.println ( "generateCenToken success." );
          //  String rtnMessage = "";

            authString = register();
            System.out.println ( authString );
            authJson = authString.get ( "object" );  /* Return object on json format */
            if( authJson != null ) {
                System.out.println ( authJson );
            }

        }
        else {
            System.out.println ( "Authenticate Failed ..." );
        }
    }
    
    
    public static void main(String[] args) throws Exception {

        HashMap<String , String> authString; 
        RegisterPkiPkcs7 basicAuth = new RegisterPkiPkcs7();
        String username = JOptionPane.showInputDialog("Please enter username :"); /* input username */
        String password = JOptionPane.showInputDialog("Please enter password :"); /* input password */
        
        basicAuth.process(username,password);
        
//        String test = "1530177396982";
//        
//        System.out.println( test.substring(0, 10) );
        System.exit(0); 
        
    }
}