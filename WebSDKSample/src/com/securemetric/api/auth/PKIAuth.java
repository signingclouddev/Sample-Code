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
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Iterator;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author auyong
 */
public class PKIAuth {
    private Object keyStore;

    public HashMap<String , String>  Authenticate(String username, String pkiCert, String pkiPassword) 
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

            String ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
            String userAgent = "My user agent"; /* The client’s user agent. Optional Field */
            String authToken = ""; /* The previous generated authToken. This is optional. You can leave it empty*/ 
            String supportFido = ""; /* Put in the value “true” or “false”. Or leave it empty */

            FileInputStream fis = new FileInputStream(pkiCert);
            java.security.KeyStore keys = KeyStore.getInstance("PKCS12");
            keys.load(fis,  pkiPassword.toCharArray());
            fis.close();
            
            //check the alias
            String alias = "";
            Enumeration<String> enumeration = keys.aliases();
            while(enumeration.hasMoreElements()){
                alias = enumeration.nextElement();
                System.out.println("alias = "+alias);
            }
            
            

            PrivateKey userKey = (PrivateKey) keys.getKey(alias, "12345678".toCharArray());
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initSign(userKey); 
            signature.update(this.requestRandomString(username).getBytes());
            byte[] signedData = signature.sign();
            
            String signatureString =  Base64.encodeBase64String(signedData);
            System.out.println("signatureString = " + signatureString);
            
            java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) keys.getCertificate ( alias );
            String certFingerprintSha1  = APIController.getThumbPrint(cert, "SHA-1");
            System.out.println("certFingerprintSha1 = " + certFingerprintSha1);
            if(certFingerprintSha1 == "") {
                return null;
            }

            /* Put all the required parameters for basic authentication */ 
            HashMap<String,String> map = new HashMap( ) ; 
            map.put ( "username" , username ) ; 
            map.put ( "certFingerprintSha1" , certFingerprintSha1 ) ;
            map.put ( "signature", signatureString);
            map.put ( "integrationKey" , integrationKey ) ; 
            map.put ( "unixTimestamp" , unixTimestamp ) ; 
           
            map.put ( "supportFido" , supportFido ) ;  
            map.put ( "ipAddress" , ipAddress ) ; /* The client’s IP address. Optional Field */ 
            map.put ( "userAgent" , userAgent ) ; /* The client’s user agent. Optional Field */ 
            map.put ( "hmac" ,  APIController.calculateHmac256 (
                       secretKey , 
                       username + certFingerprintSha1 + signatureString + integrationKey + unixTimestamp + authToken + supportFido + ipAddress + userAgent)) ; 
            map.put ( "authToken" , authToken ) ; 
             
            Gson gson = new Gson (); /*GSON library*/     
            ClientConfig config = new DefaultClientConfig ();
            com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );

            WebResource service = client.resource ( APIController.getBaseURI()+  "/auth/authPkiWithSignature" );
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
    
    public String requestRandomString( String username){
        String randomString="";
        try {

            /* Executing put */
            /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
            String integrationKey = APIController.getIntegrationKey();  
            /* The current time in second (GMT+00:00) */ 
            String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
            /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
            String secretKey = APIController.getSecretKey() ;  

            String ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
            String userAgent = "My user agent"; /* The client’s user agent. Optional Field */
            String authToken = ""; /* The previous generated authToken. This is optional. You can leave it empty*/ 

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("username",  username);
            map.put("integrationKey", integrationKey);            
            map.put("unixTimestamp", unixTimestamp);
            map.put("authToken",  authToken);
            map.put("ipAddress", ipAddress);
            map.put("userAgent", userAgent);
            map.put("hmac", APIController.calculateHmac256(secretKey, username + integrationKey + unixTimestamp+ authToken + ipAddress+ userAgent));

            Gson gson = new Gson (); /*GSON library*/     
            ClientConfig config = new DefaultClientConfig ();
            com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );

            WebResource service = client.resource ( APIController.getBaseURI()+  "/auth/requestRandomString" );
            /* Send the POST request for authentication */ 
            ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );

            /* Read the output returned from the authentication */ 
            String outputJson = response.getEntity(String.class);

            HashMap<String, String> returnMap = (HashMap<String, String>) new Gson().fromJson(outputJson, HashMap.class);
            HashMap<String, String> returnMap2 = (HashMap<String, String>) new Gson().fromJson(returnMap.get("object"), HashMap.class);
            
            randomString = returnMap2.get("randomString");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return randomString;
    }
    
    public static void main(String[] args) throws Exception {

        HashMap<String , String> authString; 
        PKIAuth otpAuth = new PKIAuth();
        String username = JOptionPane.showInputDialog("Please enter username :"); /* input username */
        String pkiCert = ""; 
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          System.out.println("PKI Cert path : " + selectedFile.getAbsoluteFile());
          pkiCert = selectedFile.getAbsolutePath();
        }
        
        if (pkiCert == "") { /* exit if no cert choose */
            System.out.println("Not cerfificate has been choose" ); 
            System.exit(0); 
        }
        
        String pkiPassword = JOptionPane.showInputDialog("Type the password for the private key :"); /* input password */
        if (pkiPassword == "") { /* exit if no pkiPassword has input */
            System.out.println("Invalid input password" ); 
            System.exit(0); 
        }
        authString = otpAuth.Authenticate(username, pkiCert,pkiPassword);

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