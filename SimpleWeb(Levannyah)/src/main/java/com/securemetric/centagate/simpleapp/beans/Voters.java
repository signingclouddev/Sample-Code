package com.securemetric.centagate.simpleapp.beans;

import com.google.gson.Gson;
import com.securemetric.centagate.simpleapp.database.DataConnect;
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
import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@ManagedBean(name="voters")
@SessionScoped
public class Voters implements Serializable {

    private static final long serialVersionUID = 1094801825228386363L;
    private String id;
    private String adminUsername;
    private String authToken;
    private String secretCode;
    private String firstName;
    private String lastName;
    private String username;
    private String userApp;
    private String userUniqueId;
    private String userClientId;
    private String roles;
    private String group;
    private String userEmail;
    private String status;
    private String name;

    private String generateId() {
        UUID uuid = UUID.randomUUID();
        String uniqueId = uuid.toString().replace("-", "");
        return uniqueId.substring(0, 8);
    }

    public Voters() {
        this.adminUsername = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
        this.authToken = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authToken").toString();
        this.secretCode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("secretCode").toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String username) {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String voterRegistration() throws Exception {

        name = firstName + " " + lastName;

        userApp = generateId();
        userUniqueId = generateId();
        userClientId = generateId();

        ClientConfig config = new DefaultClientConfig ();
        Client client = Client.create ( config );
        WebResource service = client.resource ("https://cloud.centagate.com/CentagateWS/webresources");

        Gson gson = new Gson();

        String cenToken = convertHmacSha256(secretCode, adminUsername + authToken);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("username", username);
        map.put("userApp", userApp);
        map.put("userUniqueId", userUniqueId);
        map.put("userClientId", userClientId);
        map.put("userAdditionalData1", "");
        map.put("userAdditionalData2", "");
        map.put("userAdditionalData3", "");
        map.put("userAdditionalData4", "");
        map.put("userAdditionalData5", "");
        map.put("userEmail", userEmail);
        map.put("roles", "3");
        map.put("userGroup", "voters");
        map.put("cenToken", cenToken);

        ClientResponse response = service.path("user").path("registerUserActivate").path(adminUsername).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, gson.toJson(map));

        String retJson = response.getEntity(String.class);

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
            SetupPassword setupPassword = new SetupPassword();
            setupPassword.setup(name, username, userEmail);

            // Insert into database
            String insertQuery = "INSERT INTO voters (firstName, lastName, username, userEmail, status) VALUES (?, ?, ?, ?, ?)";
            try (Connection connection = DataConnect.getConnection();
                 PreparedStatement statement = connection.prepareStatement(insertQuery)) {

                // Set the parameter values for the prepared statement
                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.setString(3, username);
                statement.setString(4, userEmail);
                statement.setString(5, "Eligible");

                // Execute the insert query
                statement.executeUpdate();
            } catch (SQLException e) {
                // Handle any database-related errors
                e.printStackTrace();
                // Redirect or display an error message to the user as needed
            }
            return null;
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", message));

            return null;
        }
    }

    public List<Voters> getVoters() throws SQLException {
        List<Voters> voters = new ArrayList<>();

        String selectQuery = "SELECT * FROM voters";
        try  {

            Connection connection = DataConnect.getConnection();
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Voters voter = new Voters();
                voter.setId(resultSet.getString("id"));
                voter.setFirstName(resultSet.getString("firstName"));
                voter.setLastName(resultSet.getString("lastName"));
                voter.setUsername(resultSet.getString("username"));
                voter.setUserEmail(resultSet.getString("userEmail"));
                voter.setStatus(resultSet.getString("status"));
                voters.add(voter);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any database-related errors
        }

        return voters;
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