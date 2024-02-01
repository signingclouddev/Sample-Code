package loginjsf;

import loginjsf.CentagateAPI;
import loginjsf.Transaction;

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
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.security.NoSuchProviderException;

/**
 *
 * @author weiyou.teoh
 */
@ManagedBean(name = "transactionsign", eager = true)
@RequestScoped
public class TransactionSign {
    
    private String senderemail;
    private String sender;
    private String receiver;
    private String message;
    private int amount;
    private String qr;

    private String authtoken;
    private int signingstatus;

    Transaction transaction = new Transaction();
    CentagateAPI api = new CentagateAPI();

    /*Constructor, initialize informations from session, initialize informations retrieved from 
      Transaction.java and initialize the signingstatus to -1
    */
    public TransactionSign() {
        getUserSession();
        senderemail = transaction.getSenderemail();
        sender = transaction.getSender();
        message = transaction.getMessage();
        signingstatus = -1;
    }

    public String getAuthToken() {
        return authtoken;
    }

    public int getSigningstatus() {
        return signingstatus;
    }

    public String getqr() {
        return qr;
    }

    public void setAuthToken(String authtoken) {
        this.authtoken = authtoken;
    }

    public void setSigningstatus(int signingstatus) {
        this.signingstatus = signingstatus;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setqr(String qr) {
        this.qr = qr;
    } 

    //Used for display primefaces growl message 
    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    //Get the user session from Transaction.java to setup required information
    public void getUserSession() {
        FacesContext context = FacesContext.getCurrentInstance();

        HashMap<String, String> hMap = new HashMap();
        hMap = (HashMap) context.getExternalContext().getSessionMap().get("transact");

        setAuthToken(hMap.get("authToken"));
        setAmount(Integer.parseInt(hMap.get("amount")));
        setReceiver(hMap.get("receiver"));
        setqr(hMap.get("qr"));
    }
   
    /*Used by both pushtransactionsign.xhtml and qrtransactionsign.xhtml as hidden button action to perform 
      transaction in database, then redirect user back to homepage (result.xhtml)
    */
    public void showSuccess() throws IOException{
       databaseTransact();
       redirectPage();
    }

    //Update balance of sender and receiver in database
    public void databaseTransact() {
        try{
            Context context = new InitialContext();
            DataSource dataSource = (DataSource) context.lookup("jdbc/mysql");
            Connection con = dataSource.getConnection();

            PreparedStatement statement = con.prepareStatement("update user set Balance = Balance + ? where Username = ?"); 
            
            statement.setInt(1, amount);
            statement.setString(2, receiver);
        
            PreparedStatement stmt = con.prepareStatement("update user set Balance = Balance - ? where Username = ?");
            stmt.setInt(1, amount);
            stmt.setString(2, sender);

            int addAmount = statement.executeUpdate();
            int removeAmount = stmt.executeUpdate();

            statement.close();
            stmt.close();
            con.close();
        }catch(Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }
   
    /*Get the status of request of transaction methods (push and qr) using API in CentagateAPI.java and update
      the result to the signingstatus (used in both qrtransactionsign.xhtml and pushtransactionsign.xhtml p:poll)
    */ 
    public void checkSigningStatus(String method) throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        switch(method) {
            case "push":
                int pushstate = api.checkAuthMethod(sender, "PUSH", getAuthToken());
                setSigningstatus(pushstate);
                break;

            case "qr":
                int qrstate = api.checkAuthMethod(sender, "QRCODE", getAuthToken());
                setSigningstatus(qrstate);
                break;
        }
    }

    /*Main function used by qrtransactionsign and pushtransactionsign to perform transaction update in p: poll
      Step 1: Call the check signing status function to make sure the singningstatus variable is initialized
      Step 2: update the p:poll with JavaScript interval
      Step 3: If success with code 0, trigger showSuccess hidden button
      Step 4: If fail with code 23026, show Primefaces Growl in that page and redirect user back to the last page
    */
    public void transact(String method) throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        switch(method) {
            case "push":
                    checkSigningStatus("push");
                    
                    PrimeFaces.current().executeScript(
                         "var interval = setInterval(function() {" +
                            "  var result = document.getElementById('push:transact').value;" +
                            "  if(result == 0) {" +
                                "    var successButton = document.getElementById('resultForm:transactButton');" +
                                "    PF('growlpush').renderMessage({ " +
                                "        'summary': 'Transaction Successful', " +
                                "        'severity': 'info' " +
                                "    }); " + 
                                "    setTimeout(function() { " +
                                "       successButton.click();" +
                                "    }, 1500); " +
                            "  } else if(result == 23026) {" +
                                "    PF('growlpush').renderMessage({ " +
                                "        'summary': 'Transaction Fail. Try Again', " +
                                "        'severity': 'error' " +
                                "    }); " + 
                                "    setTimeout(function() { " +
                                "       window.history.back();" +
                                "    }, 1500); " +
                            "  }" +
                            "    clearInterval(interval);" +
                        "}, 1000);"
                    );
                break;

            case "qr":
                    checkSigningStatus("qr");
                    
                    PrimeFaces.current().executeScript(
                         "var interval = setInterval(function() {" +
                            "  var result = document.getElementById('qr:transact').value;" +
                            "  if(result == 0) {" +
                                "    var successButton = document.getElementById('qrForm:transactButton');" +
                                 "    PF('growlqr').renderMessage({ " +
                                "        'summary': 'Transaction Successful', " +
                                "        'severity': 'info' " +
                                "    }); " + 
                                "    setTimeout(function() { " +
                                "       successButton.click();" +
                                "    }, 1500); " +
                            "  } else if(result == 23026) {" +
                                "    PF('growlqr').renderMessage({ " +
                                "        'summary': 'Transaction Fail. Try Again', " +
                                "        'severity': 'error' " +
                                "    }); " + 
                                "    setTimeout(function() { " +
                                "       window.history.back();" +
                                "    }, 1500); " +
                            "  }" +
                            "    clearInterval(interval);" +
                        "}, 1000);"
                    );
                break;
        }
    }

    //Redirect user to homepage (result.xhtml)
    public void redirectPage() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect("result.xhtml?faces-redirect=true");
    }
}