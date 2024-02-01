package com.securemetric.centagate.BudgetManagementSystem.beans;

import java.io.Serializable;

import javax.crypto.*;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;

import java.nio.charset.StandardCharsets;
import java.security.*;
//import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.HashMap;

import com.google.gson.Gson;
import com.securemetric.centagate.BudgetManagementSystem.db.DatabaseConnection;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.WebResource;
//import org.apache.catalina.WebResource;


@ManagedBean(name="resetPassword")
@SessionScoped
public class ResetPassword implements Serializable {
    private static final long serialVersionUID = 1094801825228386363L;
    private String message;
    private String username;
    private String password;
    private String userEmail;

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public ResetPassword(String username, String userEmail) {
        this.username = username;
        this.userEmail = userEmail;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


    public String getSetupPasswordApi() throws Exception {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource("https://cloud.centagate.com/CentagateWS/webresources/password/");

        String integrationKey = "8a21874b64d9bb5bd4b02400aa014223fb2170782c41d532fea37c07c0ffe8c6";
        String secretKey = "Hrz3G92PO2hN";
        String password = username + "123";

        Gson gson = new Gson();

        String encryptedPassword = encryptPassword(password, secretKey);

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("newPassword", encryptedPassword);
        map.put("integrationKey", integrationKey);

        String json = gson.toJson(map);

        ClientResponse response = service.path("v3").path("registerNewPassword").accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, gson.toJson(map));

        String retJson = response.getEntity(String.class);

        @SuppressWarnings("unchecked")
        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
        String message = returnData.get("message").toString();
        String object = returnData.get("object").toString();

        if ("0".equals(code)) {
            //Reset Password Success
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            System.out.println("Reset Password Success");
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "The user registration process has been successfully completed");

            try {
                Connection connection = DatabaseConnection.getConnection();
                if (connection != null) {
                    String selectSql = "SELECT employee_id FROM employee WHERE employee_username = ?";
                    PreparedStatement selectStatement = connection.prepareStatement(selectSql);
                    selectStatement.setString(1, username);
                    ResultSet resultSet = selectStatement.executeQuery();

                    if (resultSet.next()) {
                        int employeeId = resultSet.getInt("employee_id");

                        String updateSql = "UPDATE employee SET employee_password = ? WHERE employee_id = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                        updateStatement.setString(1, password);
                        updateStatement.setInt(2, employeeId);
                        int rowsUpdated = updateStatement.executeUpdate();

                        if (rowsUpdated > 0) {
                            System.out.println("The new password is successfully created.");
                            System.out.println("Username: " + username);
                            SendEmail sendEmail = new SendEmail(userEmail, username, password);
                            sendEmail.EmailSender();
                        } else {
                            mssg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", "User ID not Found.");
                            FacesContext.getCurrentInstance().addMessage(null, mssg);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "An error occurs while updating the password.");
                FacesContext.getCurrentInstance().addMessage(null, mssg);
            }
        }
        else {
            //Unsuccessful, Display Error
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "Please Try Again.");
            FacesContext.getCurrentInstance().addMessage(null, mssg);
        }
        return null;
    }

    public static String encryptPassword(String content, String key) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] aesKey = digest.digest(key.getBytes());
            SecretKey secretKey = new SecretKeySpec(aesKey,"AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public String back() {
        HttpSession session = SessionUtils.getSession();
        return "userRegistration.xhtml?faces-redirect=true";
    }
}

