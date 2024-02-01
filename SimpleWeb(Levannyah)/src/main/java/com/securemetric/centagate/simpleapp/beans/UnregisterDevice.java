package com.securemetric.centagate.simpleapp.beans;

import com.google.gson.Gson;
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
import javax.faces.context.FacesContext;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.HashMap;

@ManagedBean(name="unregisterDevice")
public class UnregisterDevice implements Serializable {
    private String username;
    private String adminUsername;
    private String userId;
    private String deviceHid;
    private String authToken;
    private String secretCode;
    private String role;

    public UnregisterDevice() {
        this.username = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
        this.adminUsername = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
        this.userId = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userId").toString();
        this.deviceHid = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("deviceHid").toString();
        this.authToken= FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authToken").toString();
        this.secretCode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("secretCode").toString();
        this.role = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("role").toString();
    }

    public void unregister() throws Exception {

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource("https://cloud.centagate.com/CentagateWS/webresources/");

        Gson gson = new Gson();
        String cenToken = convertHmacSha256(secretCode, adminUsername + authToken);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userId", userId);

        ClientResponse response = service.path("device").path("unregister").path(username).path(cenToken).path(deviceHid).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, gson.toJson(map));        String retJson = response.getEntity(String.class);

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
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success!", "Device successfully unregistered"));;
            TokenRegistration tokenRegistration = new TokenRegistration();
            tokenRegistration.tokenRegistrationAuth();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Something went wrong"));
        }
    }

    public static String convertHmacSha256(String secretKey, String params) throws NoSuchAlgorithmException,
            InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception {
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
}
