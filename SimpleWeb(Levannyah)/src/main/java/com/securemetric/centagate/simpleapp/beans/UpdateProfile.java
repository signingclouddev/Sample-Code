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

@ManagedBean(name="updateProfile")
@SessionScoped
public class UpdateProfile implements Serializable {

    private String id;
    private String authToken;
    private String username;
    private String adminUsername;
    private String secretCode;
    private String firstName;
    private String lastName;
    private String email;

    public UpdateProfile() {
        this.authToken = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authToken").toString();
        this.username = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
        this.adminUsername = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void update() throws Exception {

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource("https://cloud.centagate.com/CentagateWS/webresources/");

        Gson gson = new Gson();

        String cenToken = convertHmacSha256(secretCode, adminUsername + authToken);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("firstName", firstName);
        map.put("lastName", lastName);

        map.put("email", email);

        map.put("cenToken", cenToken);

        ClientResponse response = service.path("user").path("updatebyusername").path(adminUsername).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, gson.toJson(map));
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
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success!", "Profile successfully updated"));

            String updateQuery = "UPDATE admin SET firstName = ?, lastName = ?, Email = ? WHERE username = username";
            try (Connection connection = DataConnect.getConnection();
                 PreparedStatement statement = connection.prepareStatement(updateQuery)) {

                statement.setString(1, this.getFirstName());
                statement.setString(2, this.getLastName());
                statement.setString(3, this.getEmail());

                // Execute the insert query
                statement.executeUpdate();
            } catch (SQLException e) {
                // Handle any database-related errors
                e.printStackTrace();
                // Redirect or display an error message to the user as needed
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Something went wrong"));
        }
    }

    public List<UpdateProfile> getAdmin() throws SQLException {
        List<UpdateProfile> update = new ArrayList<>();

        String selectQuery = "SELECT * FROM admin";
        try  {

            Connection connection = DataConnect.getConnection();
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                UpdateProfile admin = new UpdateProfile();
                admin.setId(resultSet.getString("id"));
                admin.setFirstName(resultSet.getString("firstName"));
                admin.setLastName(resultSet.getString("lastName"));
                admin.setAdminUsername(resultSet.getString("username"));
                admin.setEmail(resultSet.getString("email"));
                update.add(admin);
            }

            for (UpdateProfile admin : update) {
                this.firstName = admin.getFirstName();
                this.lastName = admin.getLastName();
                this.email = admin.getEmail();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any database-related errors
        }

        return update;
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