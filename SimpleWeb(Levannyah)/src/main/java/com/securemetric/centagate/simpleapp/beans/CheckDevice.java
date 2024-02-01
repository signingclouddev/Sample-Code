package com.securemetric.centagate.simpleapp.beans;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.commons.codec.binary.Hex;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.primefaces.PrimeFaces;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@ManagedBean(name="checkDevice")
public class CheckDevice implements Serializable {

    private static final long serialVersionUID = 1094801825228386363L;
    private String userId;
    private String username;
    private String authToken;
    private String adminUsername;
    private String secretCode;
    private String role;
    private String deviceHid;
    private String deviceName;
    private String deviceModel;

    public CheckDevice() {
        this.userId = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userId").toString();
        this.username = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
        this.authToken = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authToken").toString();
        this.adminUsername = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
        this.secretCode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("secretCode").toString();
        this.role = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("role").toString();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String username) {
        this.adminUsername = adminUsername;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    public String getDeviceHid() {
        return deviceHid;
    }

    public void setDeviceHid(String deviceHid) {
        this.deviceHid = deviceHid;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String checkDeviceList() throws Exception {

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create ( config );
        WebResource service = client.resource ("https://cloud.centagate.com/CentagateWS/webresources");

        Gson gson = new Gson();

        String cenToken = convertHmacSha256(secretCode, adminUsername + authToken);

        ClientResponse response = service.path("device").path("listMultipleUserDevice").path(username).path(cenToken).path(userId).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        String retJson = response.getEntity(String.class);

        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
        String message = returnData.get("message").toString();
        // String object = returnData.get("object").toString();
        Map<String, Object> object = (Map<String, Object>) returnData.get("object");
        ArrayList<Map<String, Object>> deviceList = (ArrayList<Map<String, Object>>) object.get("deviceList");

        // response
        System.out.println("Check Device");
        System.out.println("Response: " + response);
        System.out.println("Code: " + code);
        System.out.println("Message: " + message);
        System.out.println("Object: " + object);
        System.out.println();

        if ("0.0".equals(code)) {
            if (!deviceList.isEmpty()) {
                for (Map<String, Object> device : deviceList) {
                    String deviceName = (String) device.get("deviceName");
                    String deviceModel = (String) device.get("deviceModel");
                    String deviceHid = (String) device.get("deviceHid");
                    String accountId = (String) device.get("accountId");

                    this.deviceHid = deviceHid;
                    this.deviceName = deviceName;
                    this.deviceModel = deviceModel;

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("deviceHid", deviceHid);
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("deviceName", deviceName);
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("deviceModel", deviceModel);
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("accountId", accountId);

                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success!", "Device successfully registered"));
//                    PrimeFaces.current().executeScript("PF('bindDialog').hide()");
                    if ("2".equals(role)) {
                        FacesContext.getCurrentInstance().getExternalContext().redirect("self-serviceadmin.xhtml");
                    } else {
                        FacesContext.getCurrentInstance().getExternalContext().redirect("self-service.xhtml");
                    }
                }
            }
            return null;
        } else {
//            PrimeFaces.current().executeScript("PF('bindDialog').hide()");

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Something went wrong"));

//            FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
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

