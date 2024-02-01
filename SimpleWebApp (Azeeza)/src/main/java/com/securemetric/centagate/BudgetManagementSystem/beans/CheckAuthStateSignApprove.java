

package com.securemetric.centagate.BudgetManagementSystem.beans;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import java.sql.ResultSet;
import java.time.Instant;
import java.util.HashMap;

@ManagedBean(name="checkAuthStateSignApprove")
@SessionScoped
public class CheckAuthStateSignApprove implements Serializable {
    private static final long serialVersionUID = 1094801825228386363L;
    private String message;
    private String username;
    private String authToken;
    private String authMethod;
    private String secretCode;

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public CheckAuthStateSignApprove() {
        this.username = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
        this.authToken = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authToken").toString();
        this.authMethod = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authMethod").toString();
    }

    public String getAuthToken() {
        return authToken;
    }
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthMethod() {
        return authMethod;
    }
    public void setAuthMethod(String authMethod) {
        this.authMethod = authMethod;
    }

    public String getSecretCode() {
        return secretCode;
    }
    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    public String getCheckAuthStateApi() throws Exception {

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create (config);
        WebResource service = client.resource ("https://cloud.centagate.com/CentagateWS/webresources");

        String integrationKey = "8a21874b64d9bb5bd4b02400aa014223fb2170782c41d532fea37c07c0ffe8c6";
        String secretKey = "Hrz3G92PO2hN";
        //String authMethod = "PUSH";
        //String authMethod = "QRCODE";

        Long timeStamp = Instant.now().getEpochSecond();
        String unixTimestamp = Long.toString(timeStamp);

        Gson gson = new Gson();

        String hmac = convertHmacSha256(secretKey, username + authMethod + integrationKey + unixTimestamp + authToken);

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("authMethod", authMethod); //The authMethod you wish to check "MSOFTCERT", "MAUDIOPASS", "QRCODE", "CROTP", ”PUSH”
        map.put("authToken", authToken);
        map.put("integrationKey", integrationKey);
        map.put("unixTimestamp", unixTimestamp);
        map.put("ipAddress", "");
        map.put("userAgent", "");
        map.put("browserFp", "");
        map.put("supportFido", "");
        map.put("hmac", hmac);

        String json = gson.toJson(map);

        ClientResponse response = service.path ("session").path("statecheck").accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, gson.toJson(map));

        String retJson = response.getEntity(String.class);

        @SuppressWarnings("unchecked")
        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
        String message = returnData.get("message").toString();
        String object = returnData.get("object").toString();
        JsonObject objectJson = gson.fromJson(object, JsonObject.class);

        if("0".equals(code)) {
            //Login Success, go to Display Status Pending Page
            this.secretCode = objectJson.get("secretCode").getAsString();
            this.authToken = objectJson.get("authToken").getAsString();
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            System.out.println("Secret Code:" + secretCode);

            try {
                Connection connection = DatabaseConnection.getConnection();
                if(connection != null) {
                    String selectSql = "SELECT budget_id FROM budget_info WHERE budget_status = ?";
                    PreparedStatement selectStatement = connection.prepareStatement(selectSql);
                    selectStatement.setString(1, "Pending");
                    ResultSet resultSet = selectStatement.executeQuery();

                    if(resultSet.next()) {
                        int budgetId = resultSet.getInt("budget_id");

                        String updateSql = "UPDATE budget_info SET budget_status = ? WHERE budget_id = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                        updateStatement.setString(1, "Approved");
                        updateStatement.setInt(2, budgetId);
                        int rowsUpdated = updateStatement.executeUpdate();

                        if(rowsUpdated > 0) {
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("secretCode", secretCode);
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("authToken", authToken);
                            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "The status is Changed to Approved.");
                            FacesContext.getCurrentInstance().addMessage(null, mssg);
                            PrimeFaces.current().dialog().showMessageDynamic(mssg);
                            PrimeFaces.current().executeScript("setTimeout(function() { redirectToPendingBudget(); }, 3000);");

                        }
                        else {
                            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", "Budget ID not found.");
                            FacesContext.getCurrentInstance().addMessage(null, mssg);
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "An error occured while updating the status.");
                FacesContext.getCurrentInstance().addMessage(null, mssg);
            }
            return null;
        }
        else if("23007".equals(code)) {
            //Authentication is Pending
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            return null;
        }
        else {
            //Unsuccessful, Display Error
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            FacesContext.getCurrentInstance().getExternalContext().redirect("statusPending.xhtml");
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "Transaction Failed.");
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
            final Mac mac = Mac.getInstance ("HmacSHA256" );
            mac.init (secret_key);
            final byte[] bytes = mac.doFinal (StringUtils.getBytesUtf8(params));
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
