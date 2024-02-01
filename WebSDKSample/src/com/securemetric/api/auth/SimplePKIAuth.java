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
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author auyong
 */
public class SimplePKIAuth {

    public HashMap<String , String>  Authenticate(String username, String pkiPublicCert, String authToken) 
    {
        
        /*POST API REQUEST*/
        HashMap<String , String> responseMap = new HashMap( ) ; 
        String authString = null;
        try {
            
            /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
          //  String integrationKey = APIController.getIntegrationKey();  
            String integrationKey = "abcdefghijklmnop";
            /* The current time in second (GMT+00:00) */ 
            String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
            /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
           // String secretKey = APIController.getSecretKey() ;  
           // String secretKey = "cPijtwWmtKtX"; 
            String secretKey = "12345678"; 
            String ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
            String userAgent = "My user agent"; /* The client’s user agent. Optional Field */
            String supportFido = ""; /* Put in the value “true” or “false”. Or leave it empty */

        //    String pkiPublicCert = "C:\\Users\\USER\\Desktop\\pkibox-3-150 - v8\\tmsAdmin02.crt";
//            FileInputStream fis = new FileInputStream(pkiPublicCert);
//
//            java.security.cert.CertificateFactory cf = java.security.cert.CertificateFactory.getInstance("X.509");
//            java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate)cf.generateCertificate(fis);
//            fis.close();
//            String certFingerprintSha1  = APIController.getThumbPrint(cert, "SHA-1");
            String certFingerprintSha1  = pkiPublicCert;
            System.out.println(certFingerprintSha1);
            /* Put all the required parameters for basic authentication */ 
            HashMap<String,String> map = new HashMap( ) ; 
            map.put ( "username" , username ) ; 
            map.put ( "certFingerprintSha1" , certFingerprintSha1 ) ;
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

            WebResource service = client.resource ( APIController.getBaseURI()+  "/auth/authPki" );
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

        HashMap<String , String> authString; 
        SimplePKIAuth simplePKIAuth = new SimplePKIAuth();
        String username = JOptionPane.showInputDialog("Please enter username :"); /* input username */
        String pkiPublicCert = ""; 
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          System.out.println("PKI Cert path : " + selectedFile.getAbsoluteFile());
          pkiPublicCert = selectedFile.getAbsolutePath();
        }
        
        if (pkiPublicCert == "") { /* exit if no cert choose */
            System.out.println("Not cerfificate has been choose" ); 
            System.exit(0); 
        }
        authString = simplePKIAuth.Authenticate(username, pkiPublicCert, "");

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