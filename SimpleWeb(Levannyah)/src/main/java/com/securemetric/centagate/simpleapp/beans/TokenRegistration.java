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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.HashMap;

@ManagedBean(name="tokenRegistration")
@SessionScoped
public class TokenRegistration implements Serializable {

    private static final long serialVersionUID = 1094801825228386363L;
    private String adminUsername;
    private String authToken;
    private String secretCode;
    private String username;
    private String qrCode;
    private String passcode;
    private String role;

    public TokenRegistration() {
        this.adminUsername = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
        this.username = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
        this.authToken = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authToken").toString();
        this.secretCode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("secretCode").toString();
        this.role = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("role").toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getQrCode() {
        return (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("qrCode");
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getPasscode() {
        return (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("passcode");
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public String tokenRegistrationAuth() throws Exception {

        ClientConfig config = new DefaultClientConfig ();
        Client client = Client.create ( config );
        WebResource service = client.resource ("https://cloud.centagate.com/CentagateWS/webresources");

        Gson gson = new Gson();

        String cenToken = convertHmacSha256(secretCode, adminUsername + authToken);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", adminUsername);
        map.put("status", "1");
        map.put("cenToken", cenToken);

        ClientResponse response = service.path("device").path("register").path("onetimepin").path(adminUsername).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, gson.toJson(map));
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

            this.qrCode = jsonObject.get("qr").getAsString();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("qrCode", qrCode);

            this.passcode = jsonObject.get("passcode").getAsString();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("passcode", passcode);

            FacesContext.getCurrentInstance().getExternalContext().redirect("register-device.xhtml");

            return null;
        } else {
            if ("2".equals(role)) {
                FacesContext.getCurrentInstance().getExternalContext().redirect("self-serviceadmin.xhtml");
            } else {
                FacesContext.getCurrentInstance().getExternalContext().redirect("self-service.xhtml");
            }
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