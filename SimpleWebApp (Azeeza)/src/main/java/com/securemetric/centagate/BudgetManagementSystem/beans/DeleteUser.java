package com.securemetric.centagate.BudgetManagementSystem.beans;


import com.google.gson.Gson;
import com.securemetric.centagate.BudgetManagementSystem.db.DatabaseConnection;
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
import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;

@ManagedBean(name="deleteUser")
@SessionScoped
public class DeleteUser implements Serializable {
    private static final long serialVersionUID = 1094801825228386363L;
    private String message;
    private String adminUsername;
    private String secretCode;
    private String authToken;

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

    public DeleteUser() {
        this.adminUsername = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
        this.secretCode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("secretCode").toString();
        this.authToken = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authToken").toString();
    }

    public String getUnbindAndDeleteUserApi() throws Exception {

        Integer selectedEmployeeId = (Integer) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selected_employee_id");
        String selectedEmployeeUsername = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selected_employee_username");

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource("https://cloud.centagate.com/CentagateWS/webresources");

        Long timeStamp = Instant.now().getEpochSecond();
        String unixTimestamp = Long.toString(timeStamp);

        Gson gson = new Gson();

        String cenToken = convertHmacSha256(secretCode, adminUsername + authToken);

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", selectedEmployeeUsername);
        map.put("cenToken", cenToken);

        String json = gson.toJson(map);

        ClientResponse response = service.path("user").path("unbindAndDelete").path(adminUsername).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, gson.toJson(map));

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
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "The User is Deleted Successfully.");
            PrimeFaces.current().dialog().showMessageDynamic(mssg);
            PrimeFaces.current().executeScript("setTimeout(function() { redirectToDisplayUserList(); }, 3000);");

            try {
                Connection connection = DatabaseConnection.getConnection();
                if (connection != null) {
                    // Step 1: Delete related budget transactions
                    PreparedStatement deleteBudgetTransactionStatement;
                    String deleteBudgetTransactionQuery = "DELETE FROM budget_transaction WHERE employee_id = ?;";
                    deleteBudgetTransactionStatement = connection.prepareStatement(deleteBudgetTransactionQuery);
                    deleteBudgetTransactionStatement.setInt(1, selectedEmployeeId);
                    deleteBudgetTransactionStatement.executeUpdate();

                    // Step 2: Delete the employee row
                    PreparedStatement deleteEmployeeStatement;
                    String deleteEmployeeQuery = "DELETE FROM employee WHERE employee_id = ?;";
                    deleteEmployeeStatement = connection.prepareStatement(deleteEmployeeQuery);
                    deleteEmployeeStatement.setInt(1, selectedEmployeeId);
                    deleteEmployeeStatement.executeUpdate();

                    System.out.println("Delete user success");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occured while registering the user.");
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
            //FacesContext.getCurrentInstance().addMessage(null, mssg);
            PrimeFaces.current().dialog().showMessageDynamic(mssg);
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

}
