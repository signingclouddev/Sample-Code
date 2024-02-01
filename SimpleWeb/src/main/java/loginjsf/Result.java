package loginjsf;

import loginjsf.CentagateAPI;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import org.primefaces.PrimeFaces;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

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
@ManagedBean(name = "result", eager = true)
@RequestScoped
public class Result {
    
    private String email;
    private String username;
    private String password;
    /*userBindAuth variable used in result.xhtml to identify which category the user belong to:
        bindauth (0): user have bind the device and enabled passwordless authentication
        nobindnoauth (1): user have not bind the device and not enabled passwordless authentication 
        bindnoauth (2): user have bind the device but not enabled passwordless authentication
    */
    private int userBindAuth;

    private CentagateAPI api = new CentagateAPI();

    /*Constructor, initialize informations from session*/
    public Result() {
        try{
            getUserSession();
        }catch(IOException e) {
            System.out.println("Error" + e.getMessage());
        }
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public void setuserBindAuth(int userBindAuth) {
        this.userBindAuth = userBindAuth;
    }

    //Get the user session from the Login.java, if the session is null then redirect user back to index.xhtml
    public void getUserSession() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        HashMap<String, String> hMap = new HashMap();
        hMap = (HashMap) context.getExternalContext().getSessionMap().get("LogonDetail");
    
        if(hMap == null) {
            PrimeFaces.current().executeScript("window.location.href = 'http://localhost:8080/simpleweb/faces/index.xhtml';");
    
        }else {
            setUsername(hMap.get("user"));
            setEmail(hMap.get("email"));
            setuserBindAuth(Integer.parseInt(hMap.get("bindAuth")));
        }
    }

    /* Used by result.xhtml buttons. 
       Functionality: Adding username, email and the user parameter to the sessionKey and redirect user to the RegistrationResult page for binding account
    */
    public void bindUser(String method) throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        HashMap<String, String> map = new HashMap();

        map.put("method", method);
        map.put("user", getUsername());
        map.put("email", getEmail());

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = ec.getSessionMap();
        sessionMap.put("BindDetail", map);

        ec.redirect("registrationresult.xhtml?faces-redirect=true");
    }

    //Used by navigation bar (header.xhtml) to invalid the session and logout the user
    public void logout() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        HttpSession session = (HttpSession) ec.getSession(false);
        if(session != null) {
            if(!ec.isResponseCommitted()) {
                session.invalidate();
            }
        }
        ec.redirect("index.xhtml?faces-redirect=true");
    }
}