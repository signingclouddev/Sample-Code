/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.api.user;

import com.securemetric.api.auth.*;
import com.google.gson.Gson;
import com.securemetric.web.util.APIController;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.File;
import java.util.HashMap;
import javax.ws.rs.core.MediaType;
import java.util.Iterator;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/**
 *
 * @author auyong
 */
public class UserManagement {

    public HashMap<String , String>  add(String loginEmail, String centoken) 
    {
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;
        String firstName = "auyong4";
        String lastName = "jinyoo";
        String username = "auyong4";
        String userEmail = "auyong4@test.com";
        String address = "";
        String city = "";
        String zip = "";
        String state = "";
        String userCountryId = "MY";
        String userTimeZoneId = "1";
        String roles = "3";
        String userGroupName = "";
        String userCompanyName = "";

        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ( "firstName" , firstName);
        map.put ("lastName" , lastName);
        map.put ("username" , "abcd123456789qwertyauyong");
        map.put ("userEmail" , userEmail);
//        map.put ("address" , address);
//        map.put ("city" , city);
//        map.put ("zip" , zip);
//        map.put ("state" , state);
//        map.put ("userCountryId" , userCountryId);
//        map.put ("userTimeZoneId" , userTimeZoneId);
//        map.put ("roles" , roles);
//        map.put ("userGroupName" , userGroupName);
//        map.put ("userCompanyName" , userCompanyName);
        map.put ("userApp" , "qwerty");
        map.put ("userUniqueId" , "abcd123456789");
        map.put ("userClientId" , "auyong");
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
        
        WebResource service = client.resource ( APIController.getSecureURI()).path("user").path("registerUserActivate").path(loginEmail);
        StringBuilder pathBuilder = new StringBuilder();
       // pathBuilder.append(centoken);
       // pathBuilder.append("/");
        /* Send the POST request for authentication */ 
        ClientResponse response = service.path(pathBuilder.toString()).accept ( MediaType.APPLICATION_JSON ).put ( ClientResponse.class , gson.toJson ( map ) );
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
    
    public HashMap<String , String>  requestmobileprovision(String loginEmail, String centoken, String email) 
    {
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;

        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ("appId" , "qwerty");
        map.put ("uniqueId" , "abcd123456789");
        map.put ("clientId" , "auyong");
        map.put ("authToken" , centoken);
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
        
        WebResource service = client.resource ( APIController.getSecureURI()).path("device").path("requestmobileprovision").path(email);
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
        String email = "handara@securemetric.com";
        SimplePKIAuth simplePKIAuth = new SimplePKIAuth();
       // String username = JOptionPane.showInputDialog("Please enter username :"); /* input username */
        String username="auyong";
        String pkiPublicCert = "C:\\\\Users\\\\auyong\\\\Desktop\\\\auyong.cer"; 
        
        
//        JFileChooser fileChooser = new JFileChooser();
//        int returnValue = fileChooser.showOpenDialog(null);
//        if (returnValue == JFileChooser.APPROVE_OPTION) {
//          File selectedFile = fileChooser.getSelectedFile();
//          System.out.println("PKI Cert path : " + selectedFile.getAbsoluteFile());
//          pkiPublicCert = selectedFile.getAbsolutePath();
//        }
//        
//        if (pkiPublicCert == "") { /* exit if no cert choose */
//            System.out.println("Not cerfificate has been choose" ); 
//            System.exit(0); 
//        }
        
        authString = simplePKIAuth.Authenticate(username, pkiPublicCert,"");
        System.out.println("Simple PKI Auth status code = " + authString);
        code = Integer.parseInt(authString.get ( "code" ));  /* Return status code */
        System.out.println("Simple PKI Auth status code = " + code);
        System.out.println("Simple PKI Auth message = " + authString.get ( "message" )); /* Return message */
        authJson = authString.get ( "object" );  /* Return object on json format */
        
        
        if( code != 0 ) {
            System.exit(0); 
        }
        
        /* Parse the result of the authentication to String object*/ 
        Gson gson = new Gson();
        HashMap<String , String> authMap = gson.fromJson ( authJson , HashMap.class );
        String authToken = authMap.get("authToken");
        String secretCode = authMap.get("secretCode");
        email = authMap.get("email");
        String userId = authMap.get("userId");
       
        System.out.println("userId = " + userId);
        System.out.println("secretCode = " + secretCode);
        System.out.println("email = " + email);
        System.out.println("authToken = " + authToken);
        String cenToken = APIController.generateCenToken(secretCode,email + authToken);
        System.out.println("cenToken = " + cenToken);

        UserManagement user = new UserManagement();
        
        //authString = user.add(email, cenToken);
        
        authString = user.requestmobileprovision(email, cenToken, email);

        if(authString != null ) {

            code = Integer.parseInt(authString.get ( "code" ));  /* Return status code */
            System.out.println("Add User status = " + code);/* Return status code */
            System.out.println("Add User return message = " + authString.get ( "message" )); /* Return message */
             System.out.println("Add User smsCode = " + authString.get ( "smsCode" )); /* Return message */
            authJson = authString.get ( "object" );  /* Return object on json format */
            
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
        }
    }
}