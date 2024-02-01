/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.web.util;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.codec.binary.Hex;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

/**
 *
 * @author auyong
 */
public class APIController {

    public static URI getBaseURI ()
    {
       // return UriBuilder.fromUri ( "https://cloud.centagate.com/v2/CentagateWS/webresources" ).build ();
         return UriBuilder.fromUri ( "https://172.18.14.22/CentagateWS/webresources" ).build ();
        //return UriBuilder.fromUri ( "https://demo.securemetric.com/v2/CentagateWS/webresources" ).build ();
    }
    
    public static URI getSecureURI ()
    {
       return UriBuilder.fromUri ( "https://poc.centagate.com/CentagateWS/webresources" ).build ();
    }
    
    
    public static String getIntegrationKey ()
    {
       // return "6dc4a2fa605b8c85807c6dbdd71d4c773a731ccb9dd711784e54b7c2aeca592a"; 
        return "911662dd49e45af77ed3e045825851404e1753891af0098a377dc3dd83f9159e";
    }
     
    public static String getSecretKey()
    {
       // return "xUpfkzuqZUlT";
        return "ZTt9mhrXCVKI";
    }
    
    public static String calculateHmac256(String secret,String message)
    {
        String hash = null;
        try {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        hash = Hex.encodeHexString(sha256_HMAC.doFinal(message.getBytes()));

        }
        catch (Exception e){
            System.out.println("Error");
            e.printStackTrace();
        }
        finally
        {    
            return hash;
        }
    }
    
    public static String getThumbPrint(X509Certificate cert, String pAlgo)
        throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance(pAlgo);
        byte[] der = cert.getEncoded();
        md.update(der);
        byte[] digest = md.digest();
        return hexify(digest);
    }
    public static String hexify (byte bytes[]) {

        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7',
                        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        StringBuffer buf = new StringBuffer(bytes.length * 2);

        for (int i = 0; i < bytes.length; ++i) {
                buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
            buf.append(hexDigits[bytes[i] & 0x0f]);
        }

        return buf.toString();
    }
    
    public static String generateCenToken ( String key , String plainText)   throws NoSuchAlgorithmException, 
                                                                             InvalidKeyException, 
                                                                             IllegalStateException, 
                                                                             UnsupportedEncodingException, 
                                                                             SignatureException,
                                                                             NoSuchProviderException,
                                                                             Exception
    {
        final SecretKeySpec secretKey = new SecretKeySpec ( key.getBytes ( ) , "HmacSHA256" ) ;
        final Mac mac = Mac.getInstance ( "HmacSHA256" ) ;

        mac.init ( secretKey ) ;

        final byte [ ] bytes = mac.doFinal ( plainText.getBytes ( "UTF-8" ) ) ;

        return Hex.encodeHexString ( bytes ) ;
    }
    
    public static void TrustAllService() throws KeyManagementException, Exception {
         // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                // Trust always
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                // Trust always
            }
        } };
        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        // Create empty HostnameVerifier
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        };

        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
    public static Client buildClient() {
        try {
            System.setProperty("jsse.enableSNIExtension", "false");
            DefaultClientConfig config = new DefaultClientConfig();


                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                }}, new java.security.SecureRandom());

                Map<String, Object> properties = config.getProperties();
                HTTPSProperties httpsProperties = new HTTPSProperties(
                        new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                }, sslcontext
                );
                properties.put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, httpsProperties);
                config.getClasses().add(JacksonJsonProvider.class);
            
            return new Client(new URLConnectionClientHandler(
                    new HttpURLConnectionFactory() {
                Proxy p = null;

                @Override
                public HttpURLConnection getHttpURLConnection(URL url)
                        throws IOException {
                    if (p == null) {
                            p = Proxy.NO_PROXY;
                    }
                    return (HttpURLConnection) url.openConnection(p);
                }
            }), config);
            //return ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier().build();
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
