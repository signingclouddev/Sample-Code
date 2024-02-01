package com.securemetric.centagate.BudgetManagementSystem.beans;

import java.io.Serializable;

import javax.crypto.*;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.crypto.spec.SecretKeySpec;
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
import org.primefaces.PrimeFaces;

@ManagedBean(name="updatePassword")
@SessionScoped
public class UpdatePassword implements Serializable {
    private static final long serialVersionUID = 1094801825228386363L;
    private String message;
    private String username;
    private String password;
    private String newPassword;

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public UpdatePassword() {
        this.username = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.password = password;
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getUpdatePasswordApi() throws Exception {

        ClientConfig config = new DefaultClientConfig ();
        Client client = Client.create (config);
        WebResource service = client.resource ("https://cloud.centagate.com/CentagateWS/webresources");

        String integrationKey = "8a21874b64d9bb5bd4b02400aa014223fb2170782c41d532fea37c07c0ffe8c6";
        String secretKey = "Hrz3G92PO2hN";

        Gson gson = new Gson();

        String encryptedPassword = encryptPassword(password,secretKey);
        String encryptedNewPassword = encryptPassword(newPassword,secretKey);

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("password", encryptedPassword);
        map.put("newPassword", encryptedNewPassword);
        map.put("integrationKey", integrationKey);

        String json = gson.toJson(map);

        ClientResponse response = service.path("password").path("updatesimple").path(username).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, gson.toJson(map));

        String retJson = response.getEntity(String.class);

        @SuppressWarnings("unchecked")
        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
        String message = returnData.get("message").toString();
        String object = returnData.get("object").toString();

        if ("0".equals(code)) {
            //Change Password Success
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            System.out.println("Change Password Success");
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "Your Password is Successfully Updated.");
            PrimeFaces.current().dialog().showMessageDynamic(mssg);

            try {
                Connection connection = DatabaseConnection.getConnection();
                if (connection != null) {
                    String selectSql = "SELECT employee_id FROM employee WHERE employee_username = ?";
                    PreparedStatement selectStatament = connection.prepareStatement(selectSql);
                    selectStatament.setString(1, username);
                    ResultSet resultSet = selectStatament.executeQuery();

                    if (resultSet.next()) {
                        int employeeId = resultSet.getInt("employee_id");

                        String updateSql = "UPDATE employee SET employee_password = ? WHERE employee_id = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                        updateStatement.setString(1, newPassword);
                        updateStatement.setInt(2, employeeId);
                        int rowsUpdated = updateStatement.executeUpdate();

                        if (rowsUpdated > 0) {
                            System.out.println("The password is successfully changed");
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
                mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "An error occurs while updating the password.");
                FacesContext.getCurrentInstance().addMessage(null, mssg);
            }
        }
        else {
            //Unsuccessful, Display Error
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            System.out.println("Username:" + username);
            System.out.println("Current Password:" + password);
            System.out.println("New Password:" + newPassword);
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "Please Try Again.");
            FacesContext.getCurrentInstance().addMessage(null, mssg);
            //FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
            //return null;
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
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public String no() {
        HttpSession session = SessionUtils.getSession();
        return "updatePassword.xhtml?faces-redirect=true";
    }

    public String back() {
        HttpSession session = SessionUtils.getSession();
        return "welcome.xhtml?faces-redirect=true";
    }
}
