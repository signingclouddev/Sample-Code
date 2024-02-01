package com.securemetric.centagate.BudgetManagementSystem.beans;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.MediaType;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
//import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.time.Instant;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.securemetric.centagate.BudgetManagementSystem.db.DatabaseConnection;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.WebResource;
//import org.apache.catalina.WebResource;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.apache.commons.codec.binary.Hex;

@ManagedBean(name="loginUser")
@SessionScoped
public class LoginUser implements Serializable {
    private static final long serialVersionUID = 1094801825228386363L;
    private String message;
    private String password;
    private String username;
    private String secretCode;
    private String authToken;
    private String roles;
    private String userId;
    //private String devAccId;

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public LoginUser() {
        this.username = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
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

    public String getRoles() {
        return roles;
    }
    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

//    public String getDevAccId() {
//        return devAccId;
//    }
//    public void setDevAccId(String devAccId) {
//        this.devAccId = devAccId;
//    }

    public String getDataAuthenticationApi() throws Exception {

        ClientConfig config = new DefaultClientConfig ();
        Client client = Client.create (config);
        WebResource service = client.resource ("https://cloud.centagate.com/v2/CentagateWS/webresources/");

        String integrationKey = "8a21874b64d9bb5bd4b02400aa014223fb2170782c41d532fea37c07c0ffe8c6";

        Long timeStamp = Instant.now().getEpochSecond();
        String unixTimestamp = Long.toString(timeStamp);

        Gson gson = new Gson();

        String hmac = convertHmacSha256("Hrz3G92PO2hN", username + password + integrationKey + unixTimestamp);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username",username );
        map.put("password", password);
        map.put("integrationKey", integrationKey);
        map.put("unixTimestamp", unixTimestamp);
        map.put("ipAddress", "");
        map.put("userAgent", "");
        map.put("browserFp", "");
        map.put("supportFido", "");
        map.put("hmac", hmac);

        String json = gson.toJson(map);

        ClientResponse response = service.path("auth").path("authBasic").accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, gson.toJson(map));

        String retJson = response.getEntity(String.class);

        @SuppressWarnings("unchecked")
        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
        String message = returnData.get("message").toString();
        String object = returnData.get("object").toString();
        JsonObject objectJson = gson.fromJson(object, JsonObject.class);

        if("0".equals(code)) {
            //Login Success, go to Welcome Page
            this.secretCode = objectJson.get("secretCode").getAsString();
            this.authToken = objectJson.get("authToken").getAsString();
            this.roles = objectJson.get("role").getAsString();
            this.userId = objectJson.get("userId").getAsString();
            //this.devAccId = objectJson.get("defAccId").getAsString();
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);

            try {
                Connection connection = DatabaseConnection.getConnection();

                String mySql = "SELECT employee_id FROM employee WHERE employee_username = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(mySql);

                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();

                Integer employeeId = 0;

                if (resultSet.next()) {
                    employeeId = resultSet.getInt("employee_id");

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("secretCode", secretCode);
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("authToken", authToken);
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("roles", roles);
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("employee_id", employeeId);
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userId", userId);
                    //FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("devAccId", devAccId);
                    FacesContext.getCurrentInstance().getExternalContext().redirect("welcome.xhtml");

                } else {
                    FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "User does not exist.");
                    FacesContext.getCurrentInstance().addMessage(null, mssg);
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
                FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Login Error!");
                FacesContext.getCurrentInstance().addMessage(null, mssg);
            }
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

    public String logout() {
        HttpSession session = SessionUtils.getSession();
        session.invalidate();
        return "login.xhtml?faces-redirect=true";
    }
}
