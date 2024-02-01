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

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
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

@ManagedBean(name="authTransactionSigningCrOtp")
@SessionScoped
public class AuthTransactionSigningCrOtp implements Serializable {
    private static final long serialVersionUID = 1094801825228386363L;
    private String message;
    private String username;
    private String details;
    private String transactionId;
    private String otp;
    private String challenge;
    private String authToken;

    public AuthTransactionSigningCrOtp() {
        this.authToken = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authToken").toString();
    }

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

    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }

    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOtp() {
        return otp;
    }
    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getChallenge() {
        return challenge;
    }
    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public String getAuthToken() {
        return authToken;
    }
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthTransactionSigningCrOtpApi() throws Exception {

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource("https://cloud.centagate.com/v2/CentagateWS/webresources");

        String integrationKey = "8a21874b64d9bb5bd4b02400aa014223fb2170782c41d532fea37c07c0ffe8c6";
        String secretKey = "Hrz3G92PO2hN";
        String details = "Hello";
        String authMethod = "PUSH";

        Long timeStamp = Instant.now().getEpochSecond();
        String unixTimestamp = Long.toString(timeStamp);

        Gson gson = new Gson();

        String hmac = convertHmacSha256(secretKey, username + otp + details + transactionId + authToken + integrationKey + unixTimestamp);

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("details", details);
        map.put("transactionId", transactionId);
        map.put("otp", otp);
        map.put("challenge", challenge);
        map.put("authToken", authToken);
        map.put("integrationKey", integrationKey);
        map.put("unixTimestamp", unixTimestamp);
        map.put("ipAddress", "");
        map.put("userAgent", "");
        map.put("browserFp", "");
        map.put("supportFido", "");
        map.put("hmac", hmac);

        ClientResponse response = service.path("trans").path("authTransactionSigningCrOtp").accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, gson.toJson(map));

        String retJson = response.getEntity(String.class);

        @SuppressWarnings("unchecked")
        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
        String message = returnData.get("message").toString();
        String object = returnData.get("object").toString();
        JsonObject objectJson = gson.fromJson(object, JsonObject.class);

        if("0".equals(code)) {
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            System.out.println("Details: " + details);
            FacesContext.getCurrentInstance().getExternalContext().redirect("welcome.xhtml");
            return null;
        }
        else {
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "Transaction Failed.");
            FacesContext.getCurrentInstance().addMessage(null, mssg);
            return null;
        }
        //return null;
    }
    public static String convertHmacSha256(String secretKey, String params) throws NoSuchAlgorithmException,
            InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        try
        {
            final SecretKeySpec secret_key = new SecretKeySpec (StringUtils.getBytesUtf8 (secretKey) , "HmacSHA256");
            final Mac mac = Mac.getInstance ("HmacSHA256");
            mac.init (secret_key);
            final byte[] bytes = mac.doFinal (StringUtils.getBytesUtf8 (params));
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
