package com.securemetric.centagate.BudgetManagementSystem.beans;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@ManagedBean(name="manageDevice")
@SessionScoped
public class ManageDevice implements Serializable {
    private String message;
    private String userId;
    private String username;
    private String adminUsername;
    private String secretCode;
    private String authToken;
    private String deviceName;
    private String deviceModel;
    private String deviceHid;
    private String serverEncPublicKey;
    private String roles;

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
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

    public String getAdminUsername() {
        return adminUsername;
    }
    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getSecretCode() {
        return secretCode;
    }
    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    public String getAuthToken() {
        return authToken;
    }
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
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

    public String getDeviceHid() {
        return deviceHid;
    }
    public void setDeviceHid(String deviceHid) {
        this.deviceHid = deviceHid;
    }

    public String getServerEncPublicKey() {
        return serverEncPublicKey;
    }
    public void setServerEncPublicKey(String serverEncPublicKey) {
        this.serverEncPublicKey = serverEncPublicKey;
    }

    public String getRoles() {
        return roles;
    }
    public void setRoles(String roles) {
        this.roles = roles;
    }

    public ManageDevice() {
        this.userId = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userId").toString();
        this.username = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
        this.adminUsername = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
        this.secretCode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("secretCode").toString();
        this.authToken = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authToken").toString();
        this.roles = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("roles").toString();
    }

    public String getListOfUserDeviceApi() throws Exception {

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create (config);
        WebResource service = client.resource ("https://cloud.centagate.com/CentagateWS/webresources");

        Long timeStamp = Instant.now().getEpochSecond();
        String unixTimestamp = Long.toString(timeStamp);

        Gson gson = new Gson();

        String cenToken = convertHmacSha256(secretCode, adminUsername + authToken);

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userId", userId);
        map.put("username", username);
        map.put("authToken", authToken);
        map.put("adminUsername", adminUsername);
        map.put("secretCode", secretCode);
        map.put("cenToken", cenToken);

        String json = gson.toJson(map);

        ClientResponse response = service.path("device").path("listMultipleUserDevice").path(username).path(cenToken).path(userId).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        String retJson = response.getEntity(String.class);

        @SuppressWarnings("unchecked")
        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
        String message = returnData.get("message").toString();
        //String object = returnData.get("object").toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> object = (Map<String, Object>) returnData.get("object");

        ArrayList<Map<String, Object>> deviceList = (ArrayList<Map<String, Object>>) object.get("deviceList");
        System.out.println("authToken: " + authToken);
        System.out.println("secretCode:" + secretCode);

        if("0.0".equals(code)) {
            if(deviceList != null && !deviceList.isEmpty()) {
                Map<String, Object> firstDevice = deviceList.get(0);
                this.serverEncPublicKey = (String) firstDevice.get("serverEncPublicKey");
                this.deviceName = (String) firstDevice.get("deviceName");
                this.deviceModel = (String) firstDevice.get("deviceModel");
                this.deviceHid = (String) firstDevice.get("deviceHid");
                System.out.println("Code:" + code);
                System.out.println("Message:" + message);
                System.out.println("Object:" + object);
                System.out.println("Role:" + roles);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("deviceName", deviceName);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("deviceModel", deviceModel);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("deviceHid", deviceHid);
            }
            else {
                System.out.println("Code:" + code);
                System.out.println("Message:" + message);
                System.out.println("Object:" + object);
                System.out.println("Role:" + roles);
                FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Device Cannot be Found. Please Register a New Device.");
                FacesContext.getCurrentInstance().addMessage(null, mssg);

                deviceName = null;
                deviceModel = null;
                deviceHid = null;
            }
            return "success";
        }
        else {
            //Unseccessful, Display Error
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "Please Try Again.");
            FacesContext.getCurrentInstance().addMessage(null, mssg);
        }
        return null;
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

    public String back() {
        HttpSession session = SessionUtils.getSession();
        return "welcome.xhtml?faces-redirect=true";
    }

    public String logout() {
        if (deviceHid == null && "2".equals(roles)) {
            //Ask admin to bind device before logout
            System.out.println("Roles:" + roles);
            System.out.println("Device Hid:" + deviceHid);

            return "deviceList.xhtml?faces-redirect=true";
        }
        else {
            HttpSession session = SessionUtils.getSession();
            session.invalidate();
            return "prelogin.xhtml?faces-redirect=true";
        }
    }

    public String applyBudget() {
        if (deviceHid == null) {
            //Ask user to bind device before apply budget
            System.out.println("Roles:" + roles);
            System.out.println("Device Hid:" + deviceHid);

            return "deviceList.xhtml?faces-redirect=true";
        }
        else {
            HttpSession session = SessionUtils.getSession();
            return "reqBudget.xhtml?faces-redirect=true";
        }
    }

}
