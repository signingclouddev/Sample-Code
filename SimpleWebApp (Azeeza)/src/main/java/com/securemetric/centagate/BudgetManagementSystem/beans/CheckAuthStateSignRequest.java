package com.securemetric.centagate.BudgetManagementSystem.beans;

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
import java.sql.*;
import java.time.Instant;
import java.util.HashMap;
//import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


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

//Call Check State API
@ManagedBean(name="checkAuthStateSignRequest")
@SessionScoped
public class CheckAuthStateSignRequest implements Serializable {

    private String message;
    private String username;
    private String authToken;
    private String authMethod;
    private String secretCode;
    private String userId;
    private String budget_type;
    private java.util.Date budget_date;
    private double budget_amount;
    private String budget_remarks;
    private Integer employeeId;

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

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CheckAuthStateSignRequest() {
        Object employeeIdObject = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("employee_id");
        if (employeeIdObject != null) {
            this.employeeId = Integer.parseInt(employeeIdObject.toString());
        } else {
            // Handle the case when the employee_id is not found in the session map
            System.out.println("Employee ID is not found");
        }
        this.username = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
        this.authToken = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authToken").toString();
        this.authMethod = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authMethod").toString();
        this.budget_type = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("budget_type").toString();
        Object budgetAmountObject = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("budget_amount");
        if (budgetAmountObject != null) {
            this.budget_amount = Double.parseDouble(budgetAmountObject.toString());
        } else {
            // Handle the case when the employee_id is not found in the session map
            System.out.println("Budget amount is null");
        }
        this.budget_remarks = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("budget_remarks").toString();

    }

    public String getBudget_type() {
        return budget_type;
    }
    public void setBudget_type(String budget_type) {
        this.budget_type = budget_type;
    }

    public java.util.Date getBudget_date() {
        return budget_date;
    }
    public void setBudget_date(java.util.Date budget_date) {
        this.budget_date = budget_date;
    }
    public java.sql.Timestamp getBudget_Timestamp() {
        if (budget_date != null) {
            return new java.sql.Timestamp(budget_date.getTime());
        }
        return null;
    }

    public double getBudget_amount() {
        return budget_amount;
    }
    public void setBudget_amount(double budget_amount) {
        this.budget_amount = budget_amount;
    }

    public String getBudget_remarks() {
        return budget_remarks;
    }
    public void setBudget_remarks(String budget_remarks) {
        this.budget_remarks = budget_remarks;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
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
            //Login Success, go to Display Status Apply Budget Page
            this.secretCode = objectJson.get("secretCode").getAsString();
            this.authToken = objectJson.get("authToken").getAsString();
            this.userId = objectJson.get("userId").getAsString();
            System.out.println("Code:" + code);
            System.out.println("Message:" + message);
            System.out.println("Object:" + object);
            System.out.println("Auth Token" + authToken);
            System.out.println("Secret Code:" + secretCode);
            System.out.println("Secret Code:" + username);

            try {
                Connection connection = DatabaseConnection.getConnection();

                if (connection != null) {
                    String selectSql = "SELECT employee_id FROM employee WHERE employee_id = ?";
                    PreparedStatement selectStatement = connection.prepareStatement(selectSql);
                    selectStatement.setInt(1, employeeId);
                    ResultSet resultSet = selectStatement.executeQuery();

                    if (resultSet.next()) {
                        java.sql.Timestamp budget_date_sql = new java.sql.Timestamp(System.currentTimeMillis());

                        String mySql = "INSERT INTO budget_info (budget_type, budget_date, budget_amount, budget_remarks, budget_status) VALUES (?, ?, ?, ?, 'Pending')";
                        PreparedStatement preparedStatement = connection.prepareStatement(mySql, Statement.RETURN_GENERATED_KEYS);

                        preparedStatement.setString(1, budget_type);
                        preparedStatement.setTimestamp(2, budget_date_sql);
                        preparedStatement.setDouble(3, budget_amount);
                        preparedStatement.setString(4, budget_remarks);

                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                            if (generatedKeys.next()) {
                                int budgetId = generatedKeys.getInt(1);

                                String insertSql = "INSERT INTO budget_transaction(employee_id, budget_id) VALUES (?, ?)";
                                PreparedStatement insertStatement = connection.prepareStatement(insertSql);
                                insertStatement.setInt(1, employeeId);
                                insertStatement.setInt(2, budgetId);

                                int rowsInserted = insertStatement.executeUpdate();

                                if (rowsInserted > 0) {
                                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", username);
                                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("authToken", authToken);
                                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("secretCode", secretCode);
                                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userId", userId);
                                    FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "Your Budget Application is Recorded Successfully.");
                                    PrimeFaces.current().dialog().showMessageDynamic(mssg);
                                    PrimeFaces.current().executeScript("setTimeout(function() { redirectToRequestBudget(); }, 3000);");
                                }
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An Error Occurred While Recording Your Budget Request.");
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
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "Transaction Failed.");
            FacesContext.getCurrentInstance().addMessage(null, mssg);
            PrimeFaces.current().dialog().showMessageDynamic(mssg);
            FacesContext.getCurrentInstance().getExternalContext().redirect("reqBudget.xhtml");
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

    public String no() {
        HttpSession session = SessionUtils.getSession();
        return "reqBudget.xhtml?faces-redirect=true";
    }

    public void clear() {
        budget_type = null;
        budget_date = null;
        budget_amount = 0.00;
        budget_remarks = null;
    }

    public String back() {
        HttpSession session = SessionUtils.getSession();
        return "reqBudget.xhtml?faces-redirect=true";
    }
}
