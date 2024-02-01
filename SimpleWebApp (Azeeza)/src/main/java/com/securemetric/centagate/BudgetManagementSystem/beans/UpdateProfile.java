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
import java.util.HashMap;
import java.time.Instant;

import com.google.gson.Gson;
import com.securemetric.centagate.BudgetManagementSystem.db.DatabaseConnection;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.WebResource;
//import org.apache.catalina.WebResource;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.apache.commons.codec.binary.Hex;
import org.primefaces.PrimeFaces;

@ManagedBean(name="updateProfile")
@SessionScoped
public class UpdateProfile implements Serializable {
    private static final long serialVersionUID = 1094801825228386363L;
    private String message;
    private String firstName;
    private String lastName;
    private String userEmail;
    private String adminUsername;
    private String secretCode;
    private String authToken;

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
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

    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public UpdateProfile() {
        this.secretCode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("secretCode").toString();
        this.authToken = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authToken").toString();
        this.adminUsername = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
    }

    public String displayProfileEmployee() throws Exception {
        String selectedEmployeeUsername = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selected_employee_username");

        try {
            Connection connection = DatabaseConnection.getConnection();
            if (connection != null) {
                String selectSql = "SELECT employee_id, employee_fname, employee_lname, employee_email FROM employee WHERE employee_username = ?";
                PreparedStatement selectStatement = connection.prepareStatement(selectSql);
                selectStatement.setString(1, selectedEmployeeUsername);
                ResultSet resultSet = selectStatement.executeQuery();

                if (resultSet.next()) {
                    setFirstName(resultSet.getString(2));
                    setLastName(resultSet.getString(3));
                    setUserEmail(resultSet.getString(4));

                    return "success";
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred while retrieving user information");
            FacesContext.getCurrentInstance().addMessage(null, mssg);
        }
        return null;
    }

    public String getUpdateUserProfileApi() throws Exception {
        Integer selectedEmployeeId = (Integer) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selected_employee_id");
        String selectedEmployeeUsername = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selected_employee_username");

        ClientConfig config = new DefaultClientConfig ();
        Client client = Client.create (config);
        WebResource service = client.resource ("https://cloud.centagate.com/CentagateWS/webresources");

        Long timeStamp = Instant.now().getEpochSecond();
        String unixTimestamp = Long.toString(timeStamp);

        Gson gson = new Gson();

        String cenToken = convertHmacSha256(secretCode, adminUsername + authToken);

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", selectedEmployeeUsername);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("email", userEmail);
        map.put("cenToken", cenToken);

        String json = gson.toJson(map);

        ClientResponse response = service.path("user").path("updatebyusername").path(adminUsername).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, gson.toJson(map));

        String retJson = response.getEntity(String.class);

        @SuppressWarnings("unchecked")
        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
        String message = returnData.get("message").toString();
        String object = returnData.get("object").toString();

        if ("0".equals(code)) {
            //Registration Success, go to Welcome Page
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            System.out.println("Employee Username:" + selectedEmployeeUsername);
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "Profile Details Updated Successfully.");
            PrimeFaces.current().dialog().showMessageDynamic(mssg);

            try {
                Connection connection = DatabaseConnection.getConnection();
                if (connection != null) {
                    String selectSql = "SELECT employee_id FROM employee WHERE employee_username = ?";
                    PreparedStatement selectStatament = connection.prepareStatement(selectSql);
                    selectStatament.setString(1, selectedEmployeeUsername);
                    ResultSet resultSet = selectStatament.executeQuery();

                    if (resultSet.next()) {
                        int employeeId = resultSet.getInt("employee_id");

                        String updateSql = "UPDATE employee SET employee_fname = ?, employee_lname = ?, employee_email = ? WHERE employee_id = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                        updateStatement.setString(1, this.getFirstName());
                        updateStatement.setString(2, this.getLastName());
                        updateStatement.setString(3, this.getUserEmail());
                        updateStatement.setInt(4, selectedEmployeeId);
                        int rowsUpdated = updateStatement.executeUpdate();

                        if (rowsUpdated > 0) {
                            System.out.println("The User's Profile is Successfully Updated");

                        }
                        else {
                            mssg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", "User ID not Found.");
                            FacesContext.getCurrentInstance().addMessage(null, mssg);
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurs while updating user's profile.");
                FacesContext.getCurrentInstance().addMessage(null, mssg);
            }
            return null;
        }
        else {
            //Unsuccessful, Display Error
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "Please Try Again.");
            FacesContext.getCurrentInstance().addMessage(null, mssg);
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


    public String no() {
        HttpSession session = SessionUtils.getSession();
        return "userList.xhtml?faces-redirect=true";
    }

    public String back() {
        HttpSession session = SessionUtils.getSession();
        return "welcome.xhtml?faces-redirect=true";
    }
}
