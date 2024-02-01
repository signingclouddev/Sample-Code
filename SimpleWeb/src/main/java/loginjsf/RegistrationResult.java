package loginjsf;

import loginjsf.CentagateAPI;
import org.primefaces.PrimeFaces;
import java.util.HashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;

import javax.naming.*;
import java.security.*;
import java.io.IOException;
import javax.servlet.http.HttpSession;

/*
 *
 * @author weiyou.teoh
 */

@ManagedBean(name = "registrationresult", eager = true)
@RequestScoped
public class RegistrationResult {
    private String method;
    private String username;
    private String provisioningQR;
    private String provisioningCode;

    /*Constructor, initialize username and method from user session and initialize provisioningQR and provisioningCode
      which used in registrationresult.xhtml
    */
    public RegistrationResult() throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
   {
        getUserSession();
        getProvisioningIntent();
    }    
   
    public String getMethod() {
        return method;
    }

    public String getUsername() {
        return username;
    }

    public String getProvisioningQR() {
        return provisioningQR;
    }

    public String getProvisioningCode() {
        return provisioningCode;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProvisioningQR(String provisioningQR) {
        this.provisioningQR = provisioningQR;
    }

    public void setProvisioningCode(String provisioningCode) {
        this.provisioningCode = provisioningCode;
    }
    
    //Used for display primefaces growl message 
    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().
        addMessage(null, new FacesMessage(severity, summary, detail));
    }

    //Function used for request QR Code and provisioning code API from CentagateAPI.java to bind a device in Centagate Application  
    public void getProvisioningIntent() throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
   {
        CentagateAPI api = new CentagateAPI();
        HashMap<String, String> hMap = api.getUserQR(getUsername());
        
        if(Integer.parseInt(hMap.get("code")) == 0) {
            String qr = hMap.get("qr");
            String passcode = hMap.get("passcode");
            setProvisioningQR(qr);
            setProvisioningCode(passcode);
        }else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Binding Error", "Cannot generate QR Code and Provisioning Code");
            setProvisioningQR("");
            setProvisioningCode("");
        }
    }

    //Get required information from RegisterMisc.java or Transaction.java's session. If session null then redirect user back to the login page (index.xhtml)
    public void getUserSession() {
        FacesContext context = FacesContext.getCurrentInstance();

            HashMap<String, String> hMap = new HashMap();
            hMap = (HashMap) context.getExternalContext().getSessionMap().get("BindDetail");

            if(hMap == null) {
                PrimeFaces.current().executeScript("window.location.href = 'http://localhost:8080/simpleweb/faces/index.xhtml';");
            }else {
                setUsername(hMap.get("user"));
                setMethod(hMap.get("method"));
            }   
    }

    //Used in registrationresult.xhtml button to clear the session and redirect user back to login page (index.xhtml)
    public void redirectLogin() throws IOException {
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