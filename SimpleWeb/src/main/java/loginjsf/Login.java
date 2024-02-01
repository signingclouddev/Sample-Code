package loginjsf;

import loginjsf.CentagateAPI;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import javax.annotation.PostConstruct;
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

import javax.servlet.http.HttpSession;

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
@ManagedBean(name = "login", eager = true)
@ViewScoped
public class Login {
    
    private String email;
    private String password;
    private String username;
    private String otp;
    private String authtoken;
    private String crotp;
    private String challenge;
    private String qrcode;
    /*userBindAuth variable used in result.xhtml to identify which category the user belong to:
        bindauth (0): user have bind the device and enabled passwordless authentication
        nobindnoauth (1): user have not bind the device and not enabled passwordless authentication 
        bindnoauth (2): user have bind the device but not enabled passwordless authentication
    */
    private int userBindAuth;

    private int number;
    CentagateAPI api = new CentagateAPI();

    @PostConstruct
    public void init() {
        email = "";
        authtoken = "";
    }

    /*constructor*/
    public Login() {
    }
    
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
    
    public String getotp() {
        return otp;
    }

    public String getauthToken() {
        return authtoken;
    }

    public String getcrotp() {
        return crotp;
    }

    public String getchallenge() {
        return challenge;
    }

    public String getqrcode() {
        return qrcode;
    }

    public int getuserBindAuth() {
        return userBindAuth;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setotp(String otp) {
        this.otp = otp;
    }

    public void setchallenge(String challenge) {
        this.challenge = challenge;
    }

    public void setauthToken(String authtoken) {
        this.authtoken = authtoken;
    }

    public void setcrotp(String crotp) {
        this.crotp = crotp;
    }

    public void setqrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public void setuserBindAuth(int userBindAuth) {
        this.userBindAuth = userBindAuth;
    }

    //Used for display primefaces growl message 
    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().
        addMessage(null, new FacesMessage(severity, summary, detail));
    }

    //Used in index.xhtml button, to login the user and prompt the dialog based on the userBindAuth category
    public void checkUserExisted() {
        String result="not defined";
        
        try{
            Context context = new InitialContext();
            DataSource dataSource = (DataSource) context.lookup("jdbc/mysql");
            Connection con = dataSource.getConnection();

            PreparedStatement statement = con.prepareStatement("select * from user where Email = ?"); 
            statement.setString(1, getEmail());
        
            ResultSet res = statement.executeQuery();
            if(res.next()) {
                setUsername(res.getString("Username"));
                HashMap<String, String> hMap = api.checkUserAvailableAuthMethod(getUsername());
                if(Integer.parseInt(hMap.get("code")) == 0) {
                    setuserBindAuth(0);
                    PrimeFaces.current().executeScript("PF('bindauth').show()");
                }else {
                    setuserBindAuth(1);
                    PrimeFaces.current().executeScript("PF('bindnoauth').show()");
                
                }
            }else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Invalid Email", "The email is not registered");
            }
            con.close();
        }catch(Exception e) {
            System.out.println("Error:  " + e.getMessage());
        }
    }

    /*Used in index.xhtml button, main function that login the user:

    For password, CROTP and OTP:

        Step 1: Call the specific authentication method API based on user choice
        Step 2: If the return code is 0 means success, then redirect user to the homepage (result.xhtml)
        Step 3: If the return code is 23017 means fail, then prompt a Primefaces Growl message to ask user try again

    For push and QR:

        Step 1: Check the available method of user using API
        Step 2: Save all the information in a session, then redirect user to the specific login authentication page
    */
    public void login(String authMethod) throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        
        switch(authMethod) {
            case "password":
                HashMap<String, String> passwordMap = api.userApiAuthentication(getUsername(), getPassword());
            
                if(Integer.parseInt(passwordMap.get("code")) == 0) {

                    if(getuserBindAuth() != 0) {

                        String authMethods = passwordMap.get("authMethods");
                        String [] methods = authMethods.split(",");

                        if(methods.length > 1) {
                            setuserBindAuth(2);
                        }else {
                            setuserBindAuth(1);
                        }
                    }

                    redirectPage();
                    
                }else {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Password Authentication Failed", "Incorrect password");
                }

                break;

            case "otp":
                int otpResult = api.authenticationOTP(getUsername(), getotp());

                if(otpResult == 0) {
                    if(getuserBindAuth() != 0) {
                        setuserBindAuth(2);
                    }
                    redirectPage();
                }else {
                    if(otpResult == 23017) {
                        setuserBindAuth(1);
                        addMessage(FacesMessage.SEVERITY_ERROR, "Error Method", "User not allowed to use this Authentication method");   
                    }else {
                        addMessage(FacesMessage.SEVERITY_ERROR, "OTP Authentication Failed", "Incorrect OTP");
                    }
                }

                break;

            case "push":
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

                HashMap<String, String> hashMap = api.pushAuthentication(getUsername());
               
                if(Integer.parseInt(hashMap.get("code")) == 0) {
                    if(getuserBindAuth() != 0) {
                        setuserBindAuth(2);
                    }
                    
                    setauthToken(hashMap.get("authToken"));
                    
                    HashMap<String, String> map = new HashMap();
                    map.put("user", getUsername());
                    map.put("email", getEmail());
                    map.put("authToken", getauthToken());
                    map.put("bindAuth", Integer.toString(getuserBindAuth()));

                    Map<String, Object> pushsessionMap = ec.getSessionMap();
                    pushsessionMap.put("pushDetail", map);

                    ec.redirect("pushauthlogin.xhtml?faces-redirect=true");
                }else {
                    setuserBindAuth(1);
                    addMessage(FacesMessage.SEVERITY_ERROR, "Push Failed", "User not allowed to use this Authentication Method");
                }

                break;

            case "crotp":
                HashMap<String, String> hMap = api.checkUserAvailableAuthMethod(getUsername());
                if(Integer.parseInt(hMap.get("code")) == 0) {
                    int crotpResult = api.authenticationCROTP(getUsername(), getchallenge(), getcrotp(), hMap.get("defAccId"));

                    if(crotpResult == 0) {
                        redirectPage();
                    }else {
                        addMessage(FacesMessage.SEVERITY_ERROR, "CROTP Authentication Failed", "Incorrect returned OTP");
                    }
                }else {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Error Method", "User not allowed to use this Authentication Method");
                }

                break;

            case "qr":
                ExternalContext eContext = FacesContext.getCurrentInstance().getExternalContext();

                HashMap<String, String> qrMap = api.checkUserAvailableAuthMethod(getUsername());
                if(Integer.parseInt(qrMap.get("code")) == 0) {
                    HashMap<String, String> returnMap = api.requestQRAuthentication(getUsername(), qrMap.get("defAccId"));

                    if(Integer.parseInt(returnMap.get("code")) == 0) {
                        String qr = returnMap.get("qrCode");
                        setauthToken(returnMap.get("authToken"));
                        setqrcode(qr);

                        HashMap<String, String> m = new HashMap();
                        
                        m.put("user", getUsername());
                        m.put("email", getEmail());
                        m.put("authToken", getauthToken());
                        m.put("qr", getqrcode());

                        Map<String, Object> qrsessionMap = eContext.getSessionMap();
                        qrsessionMap.put("qrDetail", m);

                        eContext.redirect("qrauthlogin.xhtml?faces-redirect=true");
                    }else {
                        addMessage(FacesMessage.SEVERITY_ERROR, "QR Code generated Failed", "Something bad occurred. Reload the page or use other authentication method");
                    }
                }else {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Error Method", "User not allowed to use this Authentication Method");
                }
                break;
        }
    }

    //Used by push dialog and qr dialog for the hidden buttons in index.xhtml to redirect user to homepage (result.xhtml) with session included
    public void redirectPage() throws IOException {

        HashMap<String, String> map = new HashMap();
        map.put("user", getUsername());
        map.put("email", getEmail());
        map.put("bindAuth", Integer.toString(getuserBindAuth()));

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = ec.getSessionMap();
        sessionMap.put("LogonDetail", map);
        
        ec.redirect("result.xhtml?username=" + username + "&login=true");
    }

    //Get the OTP Challenge from CentagateAPI.java for user to type in their Centagate Application
    public void getOTPChallenge() throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        String challenge = api.requestCROTPAuthentication(getUsername());
        setchallenge(challenge);
    }
}
