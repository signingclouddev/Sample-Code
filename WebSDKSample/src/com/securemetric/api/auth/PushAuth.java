/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.api.auth;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.securemetric.web.util.APIController;
import java.util.HashMap;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author auyong
 */
public class PushAuth {

    public HashMap<String , String> requestMobilePush(String username) 
    {
        /*requestSmsOtp*/
        String authString = null;
        HashMap<String , String> responseMap = new HashMap( ) ; 
        try {
        /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
        String integrationKey = "f0a19b67255d4559cd219659da03b5130fffde5fbc7a26495c1259f3b6f0bc41";//APIController.getIntegrationKey(); 
        /* The current time in second (GMT+00:00) */ 
        String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
        /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
        String secretKey = "q8btTtrfr0H0";//APIController.getSecretKey();

        String ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
        String userAgent = "My user agent"; /* The client’s user agent. Optional Field */
        String details = "Request QR for authentication"; /* The transaction details encoded using Base64 */
        details = Base64.encodeBase64String ( details.getBytes ( "UTF-8" ) ).replaceAll ( "(\r|\n)" , "" ).trim ();
        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put("username", username);
        map.put("devAccId", "");
        map.put("details", details);
        map.put("integrationKey", integrationKey);
        map.put("unixTimestamp", unixTimestamp);
        map.put("ipAddress", ipAddress);
        /* The client’s IP address */
        map.put("userAgent", userAgent);
        /* The client’s user agent */
        map.put("hmac", APIController.calculateHmac256 (
                secretKey,
                username +  integrationKey + unixTimestamp + ipAddress + userAgent));
        
        try {
            APIController.TrustAllService();
        }
        catch (Exception e) {
            
        }
  
        Gson gson = new Gson (); /*GSON library*/
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        /* Send the POST request for authentication */ 
        WebResource service = client.resource ( APIController.getBaseURI()+  "/auth/requestMobilePushCR"  );
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );
        System.out.println(response);
         /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity ( String.class ) ;  
            responseMap = gson.fromJson ( authString , HashMap.class );
        }
           
        }catch (Exception e){
            e.printStackTrace();
        } 

        return responseMap;
    }
    
    public HashMap<String , String> checkStatus(String username, String authToken) 
    {
        /*authSmsOtp*/
        
        String authString = null;
        HashMap<String , String> responseMap = new HashMap( ) ; 
        try {
        /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
        String integrationKey = "f0a19b67255d4559cd219659da03b5130fffde5fbc7a26495c1259f3b6f0bc41";//APIController.getIntegrationKey();   
        /* The current time in second (GMT+00:00) */ 
        String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
        /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
        String secretKey = "q8btTtrfr0H0"; //APIController.getSecretKey();
        
        String ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
        String userAgent = "My user agent"; /* The client’s user agent. Optional Field */
        String details = "Request QR for authentication"; /* The transaction details encoded using Base64 */
       // String authToken = ""; /* The previous generated authToken. This is optional. You can leave it empty*/ 
        String authObMethod = "PUSH";
        /* Put all the required parameters for basic authentication */ 
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("authToken", authToken);
        map.put("authMethod", authObMethod);
        map.put("integrationKey", integrationKey);
        map.put("unixTimestamp", unixTimestamp);
        //    map.put ( "ipAddress" , ipAddress );
        map.put("userAgent", userAgent);
        map.put("hmac", APIController.calculateHmac256(
                secretKey,
                username + authObMethod + integrationKey + unixTimestamp + authToken + userAgent));
      
        Gson gson = new Gson (); /*GSON library*/
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        /* Send the POST request for authentication */ 
        WebResource service = client.resource ( APIController.getSecureURI() );
        ClientResponse response = service.path("session").path("statecheck").accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, gson.toJson(map));    
        System.out.println(response);
        /* Read the output returned from the authentication */ 
        if(response.getStatus() == 200)
        {    
            authString = response.getEntity ( String.class ) ; 
            responseMap = gson.fromJson ( authString , HashMap.class );
        }
           
        }catch (Exception e){
            System.out.println("Error");
            e.printStackTrace();
        } 

        return responseMap; 
    }
    
    public static void main(String[] args) throws Exception {

        HashMap<String , String> authString;
        PushAuth pushAuth = new PushAuth();
        Gson gson = new Gson();
        String username = JOptionPane.showInputDialog("Please enter your username:");
        authString = pushAuth.requestMobilePush(username);

        /* Read the output returned from request sms code */ 
        int code = Integer.parseInt(authString.get ( "code" ));  /* Return status code */
        System.out.println("Request Push status code = " + code);
        System.out.println("Request Push = " + authString.get ( "message" )); /* Return message */
        
        String authJson = authString.get ( "object" );  /* Return object on json format */
        if( code == 0) { /* status code 0 = Success */

            HashMap<String , String> authMap = gson.fromJson ( authJson , HashMap.class );
           
            authString = null; /* initialize Hashmap to empty */
           
            int retrycount = 0;
            
            while(authJson!=null) {
                JOptionPane.showMessageDialog(null,"Press ok to check status");

                authString = pushAuth.checkStatus(username,authMap.get("authToken"));

                System.out.println("Push Authentication status = " + authString.get ( "code" ));/* Return status code */
                System.out.println("Push Authentication return message = " + authString.get ( "message" )); /* Return message */
                authJson = authString.get ( "object" );  /* Return object on json format */
                retrycount++;
                if(retrycount == 5) {
                    break;
                }
            }
            
            authMap = null; /* initialize Hashmap to empty */
            if( authJson != null ) {
                
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
            else {
                System.out.println("Push Authentication no response");
            }
       }
    }
}
