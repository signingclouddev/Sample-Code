/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.web.controller;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
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

    private String strHMAC = null;

    private static final String BASE_URI = "https://office.securemetric.com:444/CentagateWS/webresources";
    private static final String INTEGRATIONKEY = "d0cb9ad19c23a1011608b0020c5a6297bc05de5af59db928289b8664eaf2dbf3";
    private static final String SECRETKEY = "WzMgj00QbAAn";
    
    public HashMap<String, String> Authenticate(String username, String plainText, String signature, String algo, String authToken, String ipAddress, String userAgent) {

        HashMap<String, String> authString = new HashMap();

        Pkcs7Auth pkcs7Auth = new Pkcs7Auth();
        authString = pkcs7Auth.Authenticate(username, plainText, signature, algo, authToken, ipAddress, userAgent);

        return authString;
    }

    private void setHash(String hash) {
        strHMAC = hash;
    }

    public String getHash() {
        return (strHMAC);
    }

    public static URI getBaseURI() {                
        return UriBuilder.fromUri(BASE_URI).build();
    }

    public static String getIntegrationKey() {
        return INTEGRATIONKEY;
    }

    public static String getSecretKey() {
        return SECRETKEY;
    }

    public static String calculateHmac256(String secret, String message) {
        String hash = null;
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            hash = Hex.encodeHexString(sha256_HMAC.doFinal(message.getBytes()));

        } catch (Exception e) {
            System.out.println("Error");
            System.out.println("DEBUG Message[calculateHmac256] : Failed with exception " + e.getMessage());
            e.printStackTrace();
        } finally {
            return hash;
        }
    }

    public static String genLoginID(String secret, String message) {
        String hash = null;
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA1");
            sha256_HMAC.init(secret_key);

            hash = Hex.encodeHexString(sha256_HMAC.doFinal(message.getBytes()));

        } catch (Exception e) {
            System.out.println("Error");
            System.out.println("DEBUG Message[genLoginID] : Failed with exception " + e.getMessage());
            e.printStackTrace();
        } finally {
            return hash;
        }
    }

    public static String generateCenToken(String key, String plainText) throws NoSuchAlgorithmException,
            InvalidKeyException,
            IllegalStateException,
            UnsupportedEncodingException,
            SignatureException,
            NoSuchProviderException,
            Exception {
        final SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        final Mac mac = Mac.getInstance("HmacSHA256");

        mac.init(secretKey);

        final byte[] bytes = mac.doFinal(plainText.getBytes("UTF-8"));

        return Hex.encodeHexString(bytes);
    }

    public static void TrustAllService() throws KeyManagementException, Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                // Trust always
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                // Trust always
            }
        }};
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
            DefaultClientConfig config = new DefaultClientConfig();
            //bypass ssl checking
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


    public String convertWebSafeBase64ToNormalBase64(String websafeBase64) {                
        return websafeBase64.replaceAll("\\-", "+").replaceAll("_", "/") + "===".substring(0, (3 * websafeBase64.length()) % 4);
    }
}
