package com.securemetric.web.api;

import com.securemetric.web.util.Config;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Properties;
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

/**
 *
 * @author auyong
 */
public class APIController {

    private String strHMAC = null;

    public HashMap<String, String> RequestAuth(String username, String devAccId, String reqMethod, String authToken, String ipAddress, String userAgent, String details, String multiStep) {
        if (reqMethod == null) {
            return null;
        }
        HashMap<String, String> authString = new HashMap();

        if (reqMethod.equals("2")) {
            ReqSMSAuth smsAuth = new ReqSMSAuth();
            authString = smsAuth.Authenticate(username, devAccId, ipAddress, userAgent);
        } else if (reqMethod.equals("3"))//since the demo using the OTP so we no need request chanlenge code
        {
            ReqOTPAuth otpAuth = new ReqOTPAuth();
            authString = otpAuth.requestOTP(username, devAccId, ipAddress, userAgent,authToken,multiStep);
        } else if (reqMethod.equals("5")) {
            ReqCROTPAuth otpAuth = new ReqCROTPAuth();
            authString = otpAuth.Authenticate(username, devAccId, ipAddress, userAgent,authToken);
        } else if (reqMethod.equals("4")) {
            ReqQrAuth qrAuth = new ReqQrAuth();
            authString = qrAuth.Authenticate(username, devAccId, authToken, ipAddress, userAgent, details, multiStep);
        } else if (reqMethod.equals("7")) {
            ReqQna qna = new ReqQna();
            authString = qna.Authenticate(username, authToken, ipAddress, userAgent);
        } else if (reqMethod.equals("8")) {
            ReqMobilePushCr reqMobilePushCr = new ReqMobilePushCr();
            authString = reqMobilePushCr.Authenticate(username, devAccId, authToken, ipAddress, userAgent, details, multiStep);
        } else if (reqMethod.equals("9")) {
            ReqFidoAuth reqFidoAuth = new ReqFidoAuth();
            if(details!=null && details.length() > 0) {
               authString = reqFidoAuth.requestFidoTrans(username, details, details, ipAddress, userAgent);
            }else {
               authString = reqFidoAuth.Authenticate(username, authToken, ipAddress, userAgent); 
            }
        } else if (reqMethod.equals("10")) {
            ReqMobileSoftCert reqMobileSoftCert = new ReqMobileSoftCert();
            authString = reqMobileSoftCert.Authenticate(username, devAccId, authToken, ipAddress, userAgent, details, multiStep);
        }

        return authString;
    }

    public HashMap<String, String> Authenticate(String username, String devAccId,String password, String authMethod, String otp, String otpChallenge, String authToken, String ipAddress, String userAgent, String browserFp, String certFingerprint, int quesSize, String[][] qnaAns) {
        if (authMethod == null) {
            return null;
        }

        HashMap<String, String> authString = new HashMap();

        if (authMethod.equals("2")) {
            SMSAuth smsAuth = new SMSAuth();
            authString = smsAuth.Authenticate(username, otp, authToken, ipAddress, userAgent, browserFp, "");
        } else if (authMethod.equals("3")) {
            OTPAuth otpAuth = new OTPAuth();
            authString = otpAuth.Authenticate(username, devAccId, otp, authToken, ipAddress, userAgent, browserFp , "");
        } else if (authMethod.equals("5")) {
            CROTPAuth otpAuth = new CROTPAuth();
            authString = otpAuth.Authenticate(username, devAccId , otp, otpChallenge, authToken, ipAddress, userAgent, browserFp, "");
        } else if (authMethod.equals("1")) {
            BasicAuth basicAuth = new BasicAuth();
            authString = basicAuth.Authenticate(username, password, ipAddress, userAgent, browserFp);
        } else if (authMethod.equals("6")) {
            PkiAuth pkiAuth = new PkiAuth();
            authString = pkiAuth.Authenticate(username, certFingerprint, authToken, ipAddress, userAgent, browserFp, "");
        } else if (authMethod.equals("7")) {
            QnaAuth qnaAuth = new QnaAuth();
            authString = qnaAuth.Authenticate(username, authToken, ipAddress, userAgent, browserFp, quesSize, qnaAns );
        } else if (authMethod.equals("9")) {
            FidoAuth fidoAuth = new FidoAuth();
            authString = fidoAuth.Authenticate(username, otpChallenge, authToken, ipAddress, userAgent);
        }

        return authString;
    }
    
    
    public HashMap<String, String> RequestTrans(String username, String devAccId, String reqMethod, String authToken, String ipAddress, String userAgent, String details, String multiStep) {
        if (reqMethod == null) {
            return null;
        }
        HashMap<String, String> authString = new HashMap();

        if (reqMethod.equals("2")) {
            SMSTransAuth smsTransAuth = new SMSTransAuth();
            authString = smsTransAuth.requestSmsOtp(username, details, devAccId, ipAddress, userAgent);
        }else if (reqMethod.equals("5"))//since the demo using the OTP so we no need request chanlenge code
        {
            ReqCROTPTrans crOTPAuth = new ReqCROTPTrans();
            authString = crOTPAuth.reqcrOTPTrans(username, devAccId, details, ipAddress, userAgent,authToken);
        }

        return authString;
    }

    public HashMap<String, String> Transaction(String username, String devAccId, String authMethod, String otp, String otpChallenge, String authToken, String qrPlainText, String ipAddress, String userAgent, String browserFp, String transValue, String details) {
        if (authMethod == null) {
            return null;
        }
        
        HashMap<String, String> authString = new HashMap();
        if (authMethod.equals("2")) {
            SMSTransAuth smsTransAuth = new SMSTransAuth();
            authString = smsTransAuth.Authenticate(username, otp, details, devAccId, ipAddress, userAgent);
        } else if (authMethod.equals("3")) {
            Double dblTrans = Double.parseDouble(transValue);
            OTPAuth otpAuth = new OTPAuth();
            authString = otpAuth.Authenticate(username, devAccId, otp, authToken, ipAddress, userAgent, browserFp, dblTrans.toString());
        } else if (authMethod.equals("4")) {
            Double dblTrans = Double.parseDouble(transValue);
            QROTPAuth qrOTPAuth = new QROTPAuth();
            authString = qrOTPAuth.Authenticate(username, devAccId, otp, otpChallenge, authToken, qrPlainText, ipAddress, userAgent, browserFp, dblTrans.toString());
        } else if (authMethod.equals("5")) {
            CROTPTrans  crOTPTrans = new CROTPTrans();
            authString = crOTPTrans.Authenticate(username, otp, otpChallenge, authToken, details, ipAddress, userAgent, browserFp);
        } else if (authMethod.equals("6")) {
            Pkcs7Auth pkcs7Auth = new Pkcs7Auth();
            String plainText = qrPlainText;
            String signature = details;
            String algo = "0";
            authString = pkcs7Auth.Authenticate(username, plainText, signature, algo, authToken, ipAddress, userAgent);
        } else if (authMethod.equals("9")) {
            String fidoChallenge = qrPlainText;
            FidoAuth fidoAuth = new FidoAuth();
            authString = fidoAuth.AuthoriseFidoTrans(username, otpChallenge, authToken, ipAddress, userAgent, details, details, fidoChallenge);
        }

        return authString;
    }

    private void setHash(String hash) {
        strHMAC = hash;
    }

    public String getHash() {
        return (strHMAC);
    }
    
    public static URI getAuthURI() {
        // return UriBuilder.fromUri ( "http://cloud.centagate.com:8080/CentagateWS/webresources" ).build ();
        Config config = new Config();
        Properties sysProp = config.getConfig(Config.TYPE_SYSTEM);
        //   ResourceBundle resource = ResourceBundle.getBundle("system");
        return UriBuilder.fromUri(sysProp.getProperty(Config.AUTH_URL)).build();
    }

    public static URI getBaseURI() {
        // return UriBuilder.fromUri ( "http://cloud.centagate.com:8080/CentagateWS/webresources" ).build ();
        Config config = new Config();
        Properties sysProp = config.getConfig(Config.TYPE_SYSTEM);
        //   ResourceBundle resource = ResourceBundle.getBundle("system");
        return UriBuilder.fromUri(sysProp.getProperty(Config.API_URL)).build();
    }

    public static String getIntegrationKey() {
        // return "d0212cac5b7848c3e3a90b0d78769cbd4d2209513bbeee1f93aa12db7a39b5d1"; // cloud
        Config config = new Config();
        Properties sysProp = config.getConfig(Config.TYPE_SYSTEM);
        //ResourceBundle resource = ResourceBundle.getBundle("system");
        return sysProp.getProperty(Config.INTEGRATION_KEY);
    }

    public static String getSecretKey() {
        // return "cPijtwWmtKtX";// cloud
        Config config = new Config();
        Properties sysProp = config.getConfig(Config.TYPE_SYSTEM);
        // ResourceBundle resource = ResourceBundle.getBundle("system");
        return sysProp.getProperty(Config.SECRET_KEY);//resource.getString("secretkey");
    }

    public static String calculateHmac256(String secret, String message) {
        String hash = null;
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            hash = Hex.encodeHexString(sha256_HMAC.doFinal(message.getBytes()));

        } catch (IllegalStateException e) {
            System.out.println("Error");
            System.out.println("DEBUG Message[calculateHmac256] : Failed with exception " + e.getMessage());
        } catch (InvalidKeyException e) {
            System.out.println("Error");
            System.out.println("DEBUG Message[calculateHmac256] : Failed with exception " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error");
            System.out.println("DEBUG Message[calculateHmac256] : Failed with exception " + e.getMessage());
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
        String hash = null;
        try {
            
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            hash = Hex.encodeHexString(sha256_HMAC.doFinal(plainText.getBytes()));
            return hash;
         } catch (Exception e) {
            System.out.println("Error");
            System.out.println("DEBUG Message[generateCenToken] : Failed with exception " + e.getMessage());
            e.printStackTrace();
            return hash;
        } 
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
    
     public String convertWebSafeBase64ToNormalBase64(String websafeBase64) {                
        return websafeBase64.replaceAll("\\-", "+").replaceAll("_", "/") + "===".substring(0, (3 * websafeBase64.length()) % 4);
    }
     
     public HashMap<String, String> Authenticate(String username, String plainText, String signature, String algo, String authToken, String ipAddress, String userAgent) {

        HashMap<String, String> authString = new HashMap();

        Pkcs7Auth pkcs7Auth = new Pkcs7Auth();
        authString = pkcs7Auth.Authenticate(username, plainText, signature, algo, authToken, ipAddress, userAgent);

        return authString;
    }

}
