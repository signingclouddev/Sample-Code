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
import java.sql.*;
import java.util.HashMap;
import java.time.Instant;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.WebResource;
//import org.apache.catalina.WebResource;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.apache.commons.codec.binary.Hex;
import org.primefaces.PrimeFaces;

import com.securemetric.centagate.BudgetManagementSystem.db.DatabaseConnection;

@ManagedBean(name="userRegistration")
@SessionScoped
public class UserRegistration implements Serializable {

    private String message;
    private String adminUsername;
    private String firstName;
    private String lastName;
    private String newUsername;
    private String userApp;
    private String userUniqueId;
    private String userClientId;
    private String roles;
    private String userGroup;
    private String userEmail;
    private String secretCode;
    private String authToken;
    private boolean userRegistrationApi;

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getAdminUsername() {
        return adminUsername;
    }
    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNewUsername() {
        return newUsername;
    }
    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getUserApp() {
        return userApp;
    }
    public void setUserApp(String userApp) {
        this.userApp = userApp;
    }

    public String getUserUniqueId() {
        return userUniqueId;
    }
    public void setUserUniqueId(String userUniqueId) {
        this.userUniqueId = userUniqueId;
    }

    public String getUserClientId() {
        return userClientId;
    }
    public void setUserClientId(String userClientId) {
        this.userClientId = userClientId;
    }

    public String getRoles() {
        return roles;
    }
    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getUserGroup() {
        return userGroup;
    }
    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public UserRegistration() {
        this.secretCode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("secretCode").toString();
        this.authToken = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authToken").toString();
        this.adminUsername = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
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

    public boolean isUserRegistrationApi() {
        return userRegistrationApi;
    }
    public void setUserRegistrationApi(boolean userRegistrationApi) {
        this.userRegistrationApi = userRegistrationApi;
    }

    public String getUserRegistrationApi() throws Exception {

        ClientConfig config = new DefaultClientConfig ();
        Client client = Client.create (config);
        WebResource service = client.resource ("https://cloud.centagate.com/CentagateWS/webresources");

        Long timeStamp = Instant.now().getEpochSecond();
        String unixTimestamp = Long.toString(timeStamp);

        Gson gson = new Gson();

        String cenToken = convertHmacSha256(secretCode, adminUsername + authToken);

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("username", newUsername);
        map.put("userApp", userApp);
        map.put("userUniqueId", userUniqueId);
        map.put("userClientId", userClientId);
        map.put("userAdditionalData1", "");
        map.put("userAdditionalData2", "");
        map.put("userAdditionalData3", "");
        map.put("userAdditionalData4", "");
        map.put("userAdditionalData5", "");
        map.put("userEmail", userEmail);
        map.put("roles", roles);
        map.put("userGroup", userGroup);
        map.put("cenToken", cenToken);

        String json = gson.toJson(map);

        ClientResponse response = service.path("user").path("registerUserActivate").path(adminUsername).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, gson.toJson(map));

        String retJson = response.getEntity(String.class);

        @SuppressWarnings("unchecked")
        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
        String message = returnData.get("message").toString();
        String object = returnData.get("object").toString();

        if ("0".equals(code)) {
            //Registration API Success, Show Success Dialog
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);

            //Establish Database Connection
            try {
                Connection connection = DatabaseConnection.getConnection();

                String mySql = "INSERT INTO employee(employee_fname, employee_lname, employee_username, employee_appId, employee_uniqueId, employee_clientId, employee_email, employee_role, employee_group) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement= connection.prepareStatement(mySql);

                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, newUsername);
                preparedStatement.setString(4, userApp);
                preparedStatement.setString(5, userUniqueId);
                preparedStatement.setString(6, userClientId);
                preparedStatement.setString(7, userEmail);
                preparedStatement.setString(8, roles);
                preparedStatement.setString(9, userGroup);
                preparedStatement.executeUpdate();
                FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "New user successfully created. An email has been sent to the user account.");
                System.out.println("New User Successfully Created");
                ResetPassword resetPassword = new ResetPassword(newUsername, userEmail);
                resetPassword.getSetupPasswordApi();
                //FacesContext.getCurrentInstance().getExternalContext().redirect("resetPassword.xhtml");
                PrimeFaces.current().dialog().showMessageDynamic(mssg);

                firstName = null;
                lastName = null;
                newUsername = null;
                userApp = null;
                userClientId = null;
                userEmail = null;
                roles = null;
                userGroup = null;
                userUniqueId = null;
                return null;
            }
            catch(SQLException e) {
                e.printStackTrace();
                FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occured while registering the user.");
                FacesContext.getCurrentInstance().addMessage(null, mssg);
                //userRegistrationApi = false;
                return null;
            }
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

    public String yes() {
        HttpSession session = SessionUtils.getSession();
        return "welcome.xhtml?faces-redirect=true";
    }

    public String no() {
        HttpSession session = SessionUtils.getSession();
        return "userRegistration.xhtml?faces-redirect=true";
    }

    public void clear() {
        firstName = null;
        lastName = null;
        newUsername = null;
        userApp = null;
        userClientId = null;
        userEmail = null;
        roles = null;
        userGroup = null;
        userUniqueId = null;
    }

    public String back() {
        HttpSession session = SessionUtils.getSession();
        return "welcome.xhtml?faces-redirect=true";
    }

    public String resetPassword() {
        HttpSession session = SessionUtils.getSession();
        return "resetPassword.xhtml?faces-redirect=true";
    }
}
