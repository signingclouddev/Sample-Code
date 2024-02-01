package loginjsf;

import loginjsf.CentagateAPI;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import javax.faces.application.FacesMessage;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import org.primefaces.PrimeFaces;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import java.util.Map;
import java.util.HashMap;

import javax.naming.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.security.NoSuchProviderException;

/**
 *
 * @author weiyou.teoh
 */
@ManagedBean(name = "transaction", eager = true)
@RequestScoped
public class Transaction{
    
    private String senderemail;
    private int senderbalance;
    private String sender;
    private String receiver;
    private String message;
    private int amount;
    private boolean validation = false; //Used to check if the user existed in database, for condition used 
    private String authtoken;
    private String qr;
    /*userBindAuth variable used in transaction.xhtml to identify which category the user belong to:
        bindauth (0): user have bind the device and enabled passwordless authentication
        nobindnoauth (1): user have not bind the device and not enabled passwordless authentication 
        bindnoauth (2): user have bind the device but not enabled passwordless authentication
    */
    private int userbindAuth;

    CentagateAPI api = new CentagateAPI();

    /*Constructor, initialize informations from session*/
    public Transaction() {
        try{
            getUserSession();
        }catch(IOException e) {
            System.out.println("Error" + e.getMessage());
        }
    }
    
    public String getSenderemail() {
        return senderemail;
    }

    public int getSenderbalance() {
        return senderbalance;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public int getAmount() {
        return amount;
    }
    
    public boolean getValidation() {
        return validation;
    }

    public String getAuthToken() {
        return authtoken;
    }

    public String getqr() {
        return qr;
    }

    public int getuserbindAuth() {
        return userbindAuth;
    }

    public void setSenderemail(String senderemail) {
        this.senderemail = senderemail;
    }

    public void setSenderbalance(int senderbalance) {
        this.senderbalance = senderbalance;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setValidation(boolean validation) {
        this.validation = validation;
    }

    public void setAuthToken(String authtoken) {
        this.authtoken = authtoken;
    }

    public void setqr(String qr) {
        this.qr = qr;
    }

    public void setuserbindAuth(int userbindAuth) {
        this.userbindAuth = userbindAuth;
    }

    //Used for display primefaces growl messages
    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().
        addMessage(null, new FacesMessage(severity, summary, detail));
    }

    //Get the user session from the Login.java, if the session is null then redirect user back to index.xhtml
    public void getUserSession() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        HashMap<String, String> hMap = new HashMap();
        hMap = (HashMap) context.getExternalContext().getSessionMap().get("LogonDetail");

        if(hMap == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect("index.xhtml?faces-redirect=true");
        }else {
            setSender(hMap.get("user"));
            setSenderemail(hMap.get("email"));
            setuserbindAuth(Integer.parseInt(hMap.get("bindAuth")));
        }

        retrieveSenderBalance();    
    }

    //Query the database to get the sender's balance 
    public void retrieveSenderBalance() {
        try{
            Context context = new InitialContext();
            DataSource dataSource = (DataSource) context.lookup("jdbc/mysql");
            Connection con = dataSource.getConnection();

            PreparedStatement statement = con.prepareStatement("select Balance from user where Email = ?"); 
            statement.setString(1, getSenderemail());
        
            ResultSet res = statement.executeQuery();
            if(res.next()) {
                int bal = res.getInt("Balance");
                setSenderbalance(bal);
            }

            con.close();
        }catch(Exception e) {
            System.out.println("Error:  " + e.getMessage());
        }
    }

    //Validate the amount and prompt the transaction choice if the validation successful
    public void validate() {
        validateAmount();
        promptTransactionChoice();
    }

    //Validate the input amount in the range of negative to the max of the integer size, also validate the sender balance with the input amount
    private void validateAmount() {
        if(getAmount() <= 0 || getAmount() >= 2147483647) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Invalid Amount", "Amount out of range");
        }else {
            if(getAmount() > getSenderbalance()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Insufficient Balance", "You got insufficient Balance");
            }else {
                    checkUserExisted();
            }
        }
    }
   
    //Check if the receiver existed in database, prompt messages in Primefaces Growl if user not existed
    private void checkUserExisted() {
        try{
            Context context = new InitialContext();
            DataSource dataSource = (DataSource) context.lookup("jdbc/mysql");
            Connection con = dataSource.getConnection();

            PreparedStatement statement = con.prepareStatement("select * from user where Username = ?"); 
            statement.setString(1, getReceiver());
        
            ResultSet res = statement.executeQuery();
                    
            if(res.next()) {
                if(!getReceiver().equals(getSender())) {
                    setValidation(true);
                }else {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Self Transfer", "You cannot transfer back to yourself");
                }
                
            }else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Invalid User", "User not existed. Please try another user");
            }
            con.close();
        }catch(Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    //Prompt the primefaces dialog if the validation is success
    private void promptTransactionChoice() {
        if(getValidation()) {
            PrimeFaces.current().executeScript("PF('confirmDetails').show();");
        }
    }


    /*Function used in transaction.xhtml button, where the method parameter decided to use which authentication method:
        push: Save the required information in session (used in TransactionSign.java) and redirect user to pushransactionsign.xhtml
        qr: Save the required information in session (used in TransactionSign.java) and redirect user to qrtransactionsign.xhtml
    */
    public void route(String method) throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception, IOException
    {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

        
        switch(method) {
            case "push":
                HashMap<String, String> hMap = api.RequestCROTPSign(sender, api.base64Encode(message));
                
                if(Integer.parseInt(hMap.get("code")) == 0) {
                    setAuthToken(hMap.get("authToken"));

                    HashMap<String, String> pushmap = new HashMap();
                    pushmap.put("authToken", getAuthToken());
                    pushmap.put("amount", String.valueOf(getAmount()));
                    pushmap.put("receiver", getReceiver());

                    Map<String, Object> sessionpushMap = ec.getSessionMap();
                    sessionpushMap.put("transact", pushmap);

                    ec.redirect("pushtransactionsign.xhtml?faces-redirect=true");
                }else {
                    PrimeFaces.current().executeScript("alert('User not allowed this method to perform transaction')");
                }

                break;

            case "qr":
                HashMap<String, String> hashsmap = api.checkUserAvailableAuthMethod(sender);

                if(Integer.parseInt(hashsmap.get("code")) == 0) {
                    HashMap<String, String> hashMap = api.RequestQRSign(sender, api.base64Encode(message), hashsmap.get("defAccId"));
                    setAuthToken(hashMap.get("authToken"));
                    setqr(hashMap.get("qr"));

                    HashMap<String, String> qrmap = new HashMap();
                    qrmap.put("authToken", getAuthToken());
                    qrmap.put("amount", String.valueOf(getAmount()));
                    qrmap.put("receiver", getReceiver());
                    qrmap.put("qr", getqr());

                    Map<String, Object> sessionqrMap = ec.getSessionMap();
                    sessionqrMap.put("transact", qrmap);
                    ec.redirect("qrtransactionsign.xhtml?faces-redirect=true");
                }else {
                    PrimeFaces.current().executeScript("alert('This method is not allowed for user to perform transaction')");
                }

                break;
            }
    }
}