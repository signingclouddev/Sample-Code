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
import javax.servlet.http.HttpSession;
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

@ManagedBean(name="approveBudget")
@SessionScoped
public class ApproveBudget implements Serializable {
    private static final long serialVersionUID = 1094801825228386363L;
    private String message;
    private String username;
    private String details;
    private String authToken;

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

    public String getAuthToken() {
        return authToken;
    }
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public ApproveBudget() {
        this.username = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
    }

    public String getRequestTransactionSigningMobilePushCRApi() throws Exception {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource("https://cloud.centagate.com/v2/CentagateWS/webresources");

        //String devAccId = "758f85209c3fa16d719071f46f432f6c";
        String integrationKey = "8a21874b64d9bb5bd4b02400aa014223fb2170782c41d532fea37c07c0ffe8c6";
        String secretKey = "Hrz3G92PO2hN";
        String details = "Hello";
        String transactionId = "3";
        String authMethod = "PUSH";

        Long timeStamp = Instant.now().getEpochSecond();
        String unixTimestamp = Long.toString(timeStamp);

        Gson gson = new Gson();

        String hmac = convertHmacSha256(secretKey, username + details + integrationKey + unixTimestamp);

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("details", details);
        map.put("authToken", "");
        map.put("integrationKey", integrationKey);
        map.put("unixTimestamp", unixTimestamp);
        map.put("hmac", hmac);

        ClientResponse response = service.path("req").path("requestMobilePushCR").accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, gson.toJson(map));

        String retJson = response.getEntity(String.class);

        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
        String message = returnData.get("message").toString();
        String object = returnData.get("object").toString();
        JsonObject objectJson = gson.fromJson(object, JsonObject.class);

        if ("0".equals(code)) {
            this.authToken = objectJson.get("authToken").getAsString();
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("authMethod", authMethod);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("authToken", authToken);
            FacesContext.getCurrentInstance().getExternalContext().redirect("pushMobileNotificationSignApprove.xhtml");
            //FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success!", "The status is changed to Approved.");
            return null;
        }
        else {
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
            InvalidKeyException, IllegalStateException, SignatureException, NoSuchProviderException, Exception {
        try {
            final SecretKeySpec secret_key = new SecretKeySpec(StringUtils.getBytesUtf8(secretKey), "HmacSHA256");
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secret_key);
            final byte[] bytes = mac.doFinal(StringUtils.getBytesUtf8(params));
            return Hex.encodeHexString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException(e);
        } catch (InvalidKeyException e) {
            throw new InvalidKeyException(e);
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public String back() {
        HttpSession session = SessionUtils.getSession();
        return "statusPending.xhtml?faces-redirect=true";
    }
}
