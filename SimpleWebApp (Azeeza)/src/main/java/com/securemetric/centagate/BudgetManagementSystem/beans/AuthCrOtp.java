package com.securemetric.centagate.BudgetManagementSystem.beans;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.apache.commons.codec.binary.Hex;

import javax.faces.bean.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.core.MediaType;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

@ManagedBean(name="authCrOtp")
@SessionScoped
public class AuthCrOtp implements Serializable {
    private static final long serialVersionUID = 1094801825228386363L;
    private String message;
    private String username;
    private String otpChallenge;
    private String crOtp;
    private String devAccId;
    private String authToken;
    private String secretCode;
    private String userId;

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getDevAccId() {
        return devAccId;
    }
    public void setDevAccId(String devAccId) {
        this.devAccId = devAccId;
    }

    public AuthCrOtp() {
        this.username = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
        this.devAccId = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("devAccId").toString();
        this.otpChallenge = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("otpChallenge").toString();
    }
    public String getOtpChallenge() {
        return otpChallenge;
    }
    public void setOtpChallenge(String otpChallenge) {
        this.otpChallenge = otpChallenge;
    }

    public String getCrOtp() {
        return crOtp;
    }
    public void setCrOtp(String crOtp) {
        this.crOtp = crOtp;
    }

    public String getAuthToken() {
        return authToken;
    }
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getSecretCode() {
        return secretCode;
    }
    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCrOtpAuthApi() throws Exception {

        ClientConfig config = new DefaultClientConfig ();
        Client client = Client.create (config);
        WebResource service = client.resource ("https://cloud.centagate.com/v2/CentagateWS/webresources");

        //String devAccId = "758f85209c3fa16d719071f46f432f6c";
        String integrationKey = "8a21874b64d9bb5bd4b02400aa014223fb2170782c41d532fea37c07c0ffe8c6";
        String secretKey = "Hrz3G92PO2hN";

        Long timeStamp = Instant.now().getEpochSecond();
        String unixTimestamp = Long.toString(timeStamp);

        Gson gson = new Gson();

        String hmac = convertHmacSha256(secretKey, username + devAccId + crOtp + otpChallenge + integrationKey + unixTimestamp);

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("devAccId", devAccId);
        map.put("crOtp", crOtp);
        map.put("challenge", otpChallenge); //Challenge OTP that will generate during the Req Cr OTP
        map.put("secretKey", secretKey);
        map.put("otpType", ""); //Insert 'online' if mobile token and 'offline' if hardware token
        map.put("tokenId", ""); //Need insert hardware token serial number, if user register more than one hardware token and then need to specify the hardware token serial number
        map.put("authToken", "");
        map.put("integrationKey", integrationKey);
        map.put("unixTimestamp", unixTimestamp);
        map.put("ipAddress", "");
        map.put("userAgent", "");
        map.put("browserFp", "");
        map.put("supportFido", "");
        map.put("hmac", hmac);

        String json = gson.toJson(map);

        ClientResponse response = service.path ("auth").path("authCrOtp").accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, gson.toJson(map));

        String retJson = response.getEntity(String.class);

        @SuppressWarnings("unchecked")
        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
        String message = returnData.get("message").toString();
        String object = returnData.get("object").toString();
        JsonObject objectJson = gson.fromJson(object, JsonObject.class);
        //this.otpChallenge = objectJson.get("otpChallenge").getAsString();

        if("0".equals(code)) {
            //Login Success, go to Welcome Page
            this.secretCode = objectJson.get("secretCode").getAsString();
            this.authToken = objectJson.get("authToken").getAsString();
            this.userId = objectJson.get("userId").getAsString();
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            System.out.println("Secret Code: " + secretCode);
            System.out.println("Auth Token: " + authToken);
            //System.out.println("OTP Challenge: " + otpChallenge);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("secretCode", secretCode);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("authToken", authToken);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userId", userId);
            FacesContext.getCurrentInstance().getExternalContext().redirect("welcome.xhtml");
            return null;
        }
        else {
            //Unseccessful, Display Error
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "Please Try Again.");
            FacesContext.getCurrentInstance().addMessage(null, mssg);
            //FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
            return null;
        }
    }

    public static String convertHmacSha256(String secretKey, String params) throws NoSuchAlgorithmException,
            InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        try
        {
            final SecretKeySpec secret_key = new SecretKeySpec (StringUtils.getBytesUtf8 (secretKey), "HmacSHA256" );
            final Mac mac = Mac.getInstance ("HmacSHA256" );
            mac.init (secret_key);
            final byte[] bytes = mac.doFinal (StringUtils.getBytesUtf8(params));
            return Hex.encodeHexString (bytes);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new NoSuchAlgorithmException (e);
        }
        catch (InvalidKeyException e)
        {
            throw new InvalidKeyException (e);
        }
        catch (IllegalStateException e)
        {
            throw new IllegalStateException (e);
        }
        catch (Exception e)
        {
            throw new Exception (e);
        }
    }

}

