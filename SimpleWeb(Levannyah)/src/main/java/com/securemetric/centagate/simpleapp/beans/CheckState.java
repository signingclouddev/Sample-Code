package com.securemetric.centagate.simpleapp.beans;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.commons.codec.binary.Hex;
import org.apache.tomcat.util.codec.binary.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.time.Instant;
import java.util.HashMap;

@ManagedBean(name="checkState")
@SessionScoped
public class CheckState implements Serializable {

    private static final long serialVersionUID = 1094801825228386363L;
    private String username;
    private String authToken;
    private String authMethod;
    private String secretCode;
    private String userId;

    public CheckState() {
        this.username = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
        this.authToken = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authToken").toString();
        this.authMethod = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authMethod").toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String stateCheck() throws Exception {

        ClientConfig config = new DefaultClientConfig ();
        Client client = Client.create ( config );
        WebResource service = client.resource ("https://cloud.centagate.com/CentagateWS/webresources/");

        Gson gson = new Gson();

        String integrationKey = "41e90b5523297d0d85acadb5dbb0dc2a82e37753e832af41fddaea16ef9013c2";
        String secretKey = "pq969XmDAyV9";
        String unixTimestamp = Long.toString(Instant.now().getEpochSecond());

        String hmac = convertHmacSha256(secretKey,username + authMethod + integrationKey + unixTimestamp + authToken);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("authMethod", authMethod); //The authMethod you wish to check "MSOFTCERT", "MAUDIOPASS", "QRCODE", "CROTP", ”PUSH”
        map.put("authToken", authToken);
        map.put("integrationKey", integrationKey);
        map.put("unixTimestamp", unixTimestamp);
        map.put("ipAddress", "");
        map.put("userAgent", "");
        map.put("browserFp", "");
        map.put("supportFido", "");
        map.put("hmac", hmac);

        ClientResponse response = service.path ("session").path("statecheck").accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, gson.toJson(map));

        String retJson = response.getEntity(String.class);

        @SuppressWarnings("unchecked")
        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
        String message = returnData.get("message").toString();
        String object = returnData.get("object").toString();

        // response
        System.out.println("Response: " + response);
        System.out.println("Code: " + code);
        System.out.println("Message: " + message);
        System.out.println("Object: " + object);
        System.out.println();

        if ("0".equals(code)) {
            JsonObject jsonObject = gson.fromJson(object, JsonObject.class);

            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", username);

            this.userId = jsonObject.get("userId").getAsString();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userId", userId);

            this.authToken = jsonObject.get("authToken").getAsString();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("authToken", authToken);

            this.secretCode = jsonObject.get("secretCode").getAsString();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("secretCode", secretCode);

            FacesContext.getCurrentInstance().getExternalContext().redirect("candidates.xhtml");
            return null;

        } else if ("23007".equals(code)) {
            // Authentication is pending
            return "";

        } else if ("23026".equals(code)) {
            // Authentication request rejected
            FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Authentication Request Rejected", "You have rejected the request"));
            return null;

        } else {
            HttpSession session = SessionUtils.getSession();
            session.invalidate();
            FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
            return null;
        }
    }

    public static String convertHmacSha256(String secretKey, String params) throws NoSuchAlgorithmException,
            InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        try
        {
            final SecretKeySpec secret_key = new SecretKeySpec ( StringUtils.getBytesUtf8 ( secretKey ) , "HmacSHA256" );
            final Mac mac = Mac.getInstance ( "HmacSHA256" );
            mac.init ( secret_key );
            final byte[] bytes = mac.doFinal ( StringUtils.getBytesUtf8 ( params ) );
            return Hex.encodeHexString ( bytes );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new NoSuchAlgorithmException ( e );
        }
        catch ( InvalidKeyException e )
        {
            throw new InvalidKeyException ( e );
        }
        catch ( IllegalStateException e )
        {
            throw new IllegalStateException ( e );
        }
        catch ( Exception e )
        {
            throw new Exception ( e );
        }
    }
}