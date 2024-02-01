package com.securemetric.centagate.BudgetManagementSystem.beans;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

@ManagedBean(name="deviceRegistration")
@SessionScoped
public class DeviceRegistration implements Serializable {
    private static final long serialVersionUID = 1094801825228386363L;
    private String username;
    private String adminUsername;
    private String authToken;
    private String secretCode;
    private String qrCode;
    private String passcode;

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

    public String getQrCode() {
        return qrCode;
    }
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getPasscode() {
        return passcode;
    }
    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public DeviceRegistration() {
        this.username = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
        this.adminUsername = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
        this.authToken = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authToken").toString();
        this.secretCode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("secretCode").toString();
    }

    public String getTokenRegistrationOneTimePinApi() throws Exception {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create ( config );
        WebResource service = client.resource ("https://cloud.centagate.com/CentagateWS/webresources");

        String status = "1";

        Long timeStamp = Instant.now().getEpochSecond();
        String unixTimestamp = Long.toString(timeStamp);

        Gson gson = new Gson();

        String cenToken = convertHmacSha256(secretCode, adminUsername + authToken);

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("status", status);
        map.put("validity", "");
        map.put("cenToken", cenToken);

        String json = gson.toJson(map);

        ClientResponse response = service.path("device").path("register").path("onetimepin").path(adminUsername).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, gson.toJson(map));

        String retJson = response.getEntity(String.class);

        @SuppressWarnings("unchecked")
        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
        String message = returnData.get("message").toString();
        String object = returnData.get("object").toString();
        JsonObject objectJson = gson.fromJson(object, JsonObject.class);

        if ("0".equals(code)) {
            //Device Registration Success, go to Welcome Page
            this.qrCode = objectJson.get("qr").getAsString();
            this.passcode = objectJson.get("passcode").getAsString();
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            System.out.println("QR Code: " + qrCode);
            System.out.println("Passcode: " + passcode);
            //FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("qr", qrCode);
            //FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("passcode", passcode);
            FacesContext.getCurrentInstance().getExternalContext().redirect("deviceRegistration.xhtml");
            return null;
        }
        else {
            //Unseccessful, Display Error, go to Prelogin
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "Please Try Again.");
            FacesContext.getCurrentInstance().addMessage(null, mssg);
            PrimeFaces.current().dialog().showMessageDynamic(mssg);
            //FacesContext.getCurrentInstance().getExternalContext().redirect("welcome.xhtml");
            return null;
        }
    }

    public static String convertHmacSha256(String secretKey, String params) throws NoSuchAlgorithmException,
            InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        try
        {
            final SecretKeySpec secret_key = new SecretKeySpec ( StringUtils.getBytesUtf8 (secretKey) , "HmacSHA256" );
            final Mac mac = Mac.getInstance ( "HmacSHA256");
            mac.init (secret_key);
            final byte[] bytes = mac.doFinal (StringUtils.getBytesUtf8 ( params ) );
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

    public String done() {
        HttpSession session = SessionUtils.getSession();
        return "deviceList.xhtml?faces-redirect=true";
    }

    public String back() {
        HttpSession session = SessionUtils.getSession();
        return "welcome.xhtml?faces-redirect=true";
    }

}
