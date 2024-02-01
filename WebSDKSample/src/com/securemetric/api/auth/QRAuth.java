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
public class QRAuth {

    public HashMap<String , String> requestQR(String username) 
    {
        /*requestSmsOtp*/
        String authString = null;
        HashMap<String , String> responseMap = new HashMap( ) ; 
        try {
        /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
        String integrationKey = APIController.getIntegrationKey(); ;  
        /* The current time in second (GMT+00:00) */ 
        String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
        /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
        String secretKey = APIController.getSecretKey();

        String ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
        String userAgent = "My user agent"; /* The client’s user agent. Optional Field */
        String details = "Request QR for authentication"; /* The transaction details encoded using Base64 */
        details = Base64.encodeBase64String ( details.getBytes ( "UTF-8" ) ).replaceAll ( "(\r|\n)" , "" ).trim ();
        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ( "username" , username );
        map.put ( "details" ,details);
       // map.put ( "authToken" , authToken );
        map.put ( "integrationKey" , integrationKey );
        map.put ( "unixTimestamp" , unixTimestamp );
        map.put ( "ipAddress" , ipAddress ) ; /* The client’s IP address */ 
        map.put ( "userAgent" , userAgent ) ; /* The client’s user agent */ 
        map.put ( "hmac" ,  APIController.calculateHmac256 (
                   secretKey , 
                   username + details + integrationKey + unixTimestamp + ipAddress + userAgent)) ;  
        
        try {
            APIController.TrustAllService();
        }
        catch (Exception e) {
            
        }
        
        
        Gson gson = new Gson (); /*GSON library*/
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
         
        /* Send the POST request for authentication */ 
        WebResource service = client.resource ( APIController.getBaseURI()+  "/req/requestQrCode"  );
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
    
    public HashMap<String , String> Authenticate(String username, String otp, String challenge, String plainText) 
    {
        /*authSmsOtp*/
        
        String authString = null;
        HashMap<String , String> responseMap = new HashMap( ) ; 
        try {
        /* The integration key. This is the key you get from the update app page inside the CENTAGATE */ 
        String integrationKey = APIController.getIntegrationKey();   
        /* The current time in second (GMT+00:00) */ 
        String unixTimestamp = String.valueOf ( System.currentTimeMillis ( ) / 1000L ) ;  
        /* The secret key. This is the key you get from the update app page inside the CENTAGATE */ 
        String secretKey = APIController.getSecretKey();
        
        String ipAddress = "192.168.6.72";  /* The client’s IP address. Optional Field */ 
        String userAgent = "My user agent"; /* The client’s user agent. Optional Field */
        String details = "Request QR for authentication"; /* The transaction details encoded using Base64 */
        String authToken = ""; /* The previous generated authToken. This is optional. You can leave it empty*/ 
        
        /* Put all the required parameters for basic authentication */ 
        HashMap<String,String> map = new HashMap( ) ; 
        map.put ( "username" , username );
        map.put ( "otp" , otp );
        map.put ( "challenge" , challenge );
        map.put ( "details" ,plainText);
        map.put ( "integrationKey" , integrationKey );
        map.put ( "unixTimestamp" , unixTimestamp );
        map.put ( "authToken" , authToken);
        map.put ( "ipAddress" , ipAddress ) ; /* The client’s IP address */ 
        map.put ( "userAgent" , userAgent ) ; /* The client’s user agent */ 
        map.put ( "hmac" , APIController.calculateHmac256 (
                   secretKey , 
                   username + otp + challenge + plainText + integrationKey + unixTimestamp + authToken + ipAddress + userAgent)); 


        Gson gson = new Gson (); /*GSON library*/
        ClientConfig config = new DefaultClientConfig ();
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create ( config );
        /* Send the POST request for authentication */ 
        WebResource service = client.resource ( APIController.getBaseURI()+  "/auth/authQrCode"  );
        ClientResponse response = service.accept ( MediaType.APPLICATION_JSON ).post ( ClientResponse.class , gson.toJson ( map ) );
        
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
        QRAuth qrAuth = new QRAuth();
        Gson gson = new Gson();
        String username = JOptionPane.showInputDialog("Please enter your username:");
        authString = qrAuth.requestQR(username);
        
        System.out.println("Return authString= " + authString);
        /* Read the output returned from request sms code */ 
        int code = Integer.parseInt(authString.get ( "code" ));  /* Return status code */
        System.out.println("Request QR status code = " + code);
        System.out.println("Request QR image = " + authString.get ( "message" )); /* Return message */
        
        String authJson = authString.get ( "object" );  /* Return object on json format */
        System.out.println(authJson);
        if( code == 0) { /* status code 0 = Success */

            HashMap<String , String> authMap = gson.fromJson ( authJson , HashMap.class );
            String qrCode = authMap.get("qrCode");
            String otpChallenge = authMap.get("otpChallenge"); 
            String plainText = authMap.get("plainText"); 
            Hashtable < EncodeHintType, ErrorCorrectionLevel > hintMap = new Hashtable < EncodeHintType, ErrorCorrectionLevel > (); 
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
           
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(qrCode
                                        , BarcodeFormat.QR_CODE, 250, 250, hintMap);
            
            int width = matrix.getWidth();
            BufferedImage image = new BufferedImage(width, width, BufferedImage.TYPE_4BYTE_ABGR); 
            image.createGraphics();
            Graphics2D graphics = (Graphics2D) image.getGraphics(); 
            graphics.setColor(Color.WHITE); 
            graphics.fillRect(0, 0, width, width); 
            graphics.setColor(Color.BLACK);
            
            for (int i = 0; i < width; i++) { 
                for (int j = 0; j < width; j++) { 
                    if (matrix.get(i, j)) { 
                        graphics.fillRect(i, j, 1, 1); 
                    } 
                } 
            }

            ImageIcon icon = new ImageIcon();
            icon.setImage(image);

            JOptionPane.showMessageDialog(
                        null,
                        "Scan QR code with your device",
                        "QR Code", JOptionPane.INFORMATION_MESSAGE,
                        icon);
//            
            authString = null; /* initialize Hashmap to empty */
            authMap = null; /* initialize Hashmap to empty */
            
            String crOtp = JOptionPane.showInputDialog("Please enter the otp code :");
            
            authString = qrAuth.Authenticate(username, crOtp, otpChallenge, plainText);
            
            System.out.println("QR Authentication status = " + authString.get ( "code" ));/* Return status code */
            System.out.println("QR Authentication return message = " + authString.get ( "message" )); /* Return message */
            authJson = authString.get ( "object" );  /* Return object on json format */
            
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
       }
    }
}
