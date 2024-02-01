package loginjsf;

import loginjsf.CentagateAPI;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import javax.faces.application.FacesMessage;

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
@ManagedBean(name = "pushauth", eager = true)
@ViewScoped
public class PushAuth {
    
    private String email;
    private String username;
    private String authtoken;
    private int pushstatus;
    /*userBindAuth variable used in result.xhtml to identify which category the user belong to:
        bindauth (0): user have bind the device and enabled passwordless authentication
        nobindnoauth (1): user have not bind the device and not enabled passwordless authentication 
        bindnoauth (2): user have bind the device but not enabled passwordless authentication
    */
    private int userBindAuth;
    CentagateAPI api = new CentagateAPI();

    /*Constructor, initialize user information from session and set a default value for pushstatus (Used to check the status of the request from API)*/
    public PushAuth() {
        try {
            getUserSession();
        }catch(IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
       
        pushstatus = -1;
    }
    
    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getauthToken() {
        return authtoken;
    }

    public int getpushstatus() {
        return pushstatus;
    }

    public int getuserBindAuth() {
        return userBindAuth;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setauthToken(String authtoken) {
        this.authtoken = authtoken;
    }

    public void setpushstatus(int pushstatus) {
        this.pushstatus = pushstatus;
    }

    public void setuserBindAuth(int userBindAuth) {
        this.userBindAuth = userBindAuth;
    }

    //Used for display primefaces growl message 
    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().
                addMessage(null, new FacesMessage(severity, summary, detail));
    }

    //Get the user session from the Login.java, if the session is null then redirect user back to index.xhtml
    public void getUserSession() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        HashMap<String, String> hMap = new HashMap();
        hMap = (HashMap) context.getExternalContext().getSessionMap().get("pushDetail");

        if(hMap == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect("index.xhtml?faces-redirect=true");
        }else {
            setUsername(hMap.get("user"));
            setEmail(hMap.get("email"));
            setauthToken(hMap.get("authToken"));
            setuserBindAuth(Integer.parseInt(hMap.get("bindAuth")));
        }
    }

    /*Check authentication status after user request for the Push Authentication
        Status:
        23007: Request is pending
        0: Request Success
        23026: Request Rejected
    */ 
    public void checkAuthenticationStatus() throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        int res = api.checkAuthMethod(getUsername(), "PUSH", getauthToken());
        setpushstatus(res);
    }

    //Used by hidden button in pushauthlogin.xhtml to redirect user to homepage (result.xhtml) with session included
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
}