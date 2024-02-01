package loginjsf;

import loginjsf.CentagateAPI;
import org.primefaces.PrimeFaces;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;

import javax.naming.*;
import java.security.*;
import javax.faces.validator.ValidatorException;

import java.util.HashMap;
import java.util.Map;

/*
 *
 * @author weiyou.teoh
 */

@ManagedBean(name = "registration", eager = true)
@RequestScoped
public class Registration {
    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String password;
    private String confirmpassword;

    private CentagateAPI api = new CentagateAPI();

    /*Constructor*/
    public Registration() {}    
   
    public String getFirstname() {return firstname;}
   
    public String getLastname() {return lastname;}
   
    public String getEmail() {return email;}
   
    public String getUsername() {return username;}
   
    public String getPassword() {return password;}
   
    public String getConfirmpassword() {return confirmpassword;}

    public void setFirstname(String firstname){this.firstname = firstname;}    
   
    public void setLastname(String lastname) {this.lastname = lastname;}
   
    public void setEmail(String email) {this.email = email;}
   
    public void setUsername(String username) {this.username = username;}
   
    public void setPassword(String password) {this.password = password;}
     
    public void setConfirmpassword(String confirmpassword) {this.confirmpassword = confirmpassword;} 
   
    //Used for display primefaces growl message 
    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().
        addMessage(null, new FacesMessage(severity, summary, detail));
    }

    //Validate the password, to ensure password same as confirm password
    public void validatePassword(FacesContext context, UIComponent component, Object value) throws ValidatorException {
       UIInput passwordField = (UIInput) context.getViewRoot().findComponent("regForm:password");
       if(passwordField == null) {
            throw new IllegalArgumentException(String.format("Unable to find component"));
       }
       
       String pass = (String) passwordField.getValue();
       String confirmPassword = (String) value;
       
       if(!confirmPassword.equals(pass)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Passwords do not match!", "Passwords do not match!");
            throw new ValidatorException(message);
       }
    }

    //Called in register.xhtml to register a new user
    public void userRegistration() throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        checkDatabaseuserexisted();
    }   



    /*Main function used to check if the user existed in database by email address. 
      Trigger Primefaces Growl if the user existed in database, if not then register
      user in the database and Centagate Console, finally redirect user to the next 
      page (registermisc.xhtml)
    */
    private void checkDatabaseuserexisted() {
        if(getPassword().length() < 6) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Password Error", "Password length should more than 6");
        }else {
            try{
                int finalRes;

                //Setup connection to the database (using glassfish jdbc)
                Context context = new InitialContext();
                DataSource dataSource = (DataSource) context.lookup("jdbc/mysql");
                Connection con = dataSource.getConnection();

                PreparedStatement statement = con.prepareStatement("select Email from user where email= ?"); 
                statement.setString(1, getEmail());

                ResultSet result = statement.executeQuery();

                if(result.next()) {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Email Existed", "Email existed in database");
                }else {
                    if(registerUser()) {
                        addMessage(FacesMessage.SEVERITY_ERROR, "API Error", "Cannot register user in Centagate Console, user existed or something occurred");
                    }else {
                        if(changePassword()) {
                            addMessage(FacesMessage.SEVERITY_ERROR, "API Error", "Cannot change password in Centagate Console");
                        }else {
                            PreparedStatement statementfalse = con.prepareStatement("insert into user(FirstName, LastName, Email, Username, Password, Balance) values(?,?,?,?,?,?)");
                            statementfalse.setString(1, getFirstname());
                            statementfalse.setString(2, getLastname());
                            statementfalse.setString(3, getEmail());
                            statementfalse.setString(4, getUsername());
                            statementfalse.setString(5, api.hashPassword(getPassword()));
                            statementfalse.setInt(6, 0);

                            finalRes = statementfalse.executeUpdate();

                            if(finalRes != 1) {
                                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "User cannot be registered");
                            }

                            HashMap<String, String> map = new HashMap();
                            map.put("user", getUsername());
                            map.put("email", getEmail());

                            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                            Map<String, Object> sessionMap = ec.getSessionMap();
                            sessionMap.put("UserDetail", map);

                            ec.redirect("registermisc.xhtml?faces-redirect=true");
                        }
                    }                      
                }   
            
                con.close();
            }catch(Exception e) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
            }
        }
    }

    //function used to call User Registration API in CentagateAPI.java, return true if success, else return false
    private boolean registerUser() throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        boolean userExisted = false;
        int result = api.userApiRegistration(getFirstname(), getLastname(), getUsername(), getEmail());
        if(result != 0) {
            userExisted = true;
        }
        return userExisted;
    }

    //function used to call Change User Password API in CentagateAPI.java, return true if success, else return false
    private boolean changePassword() throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        boolean passwordError = false;
        int result = api.changeUserPassword(getUsername(), getUsername() + "1234", getPassword());

        if(result != 0) {
            passwordError = true;
        }
        return passwordError; 
    }
}
