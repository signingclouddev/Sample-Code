package loginjsf;

import loginjsf.CentagateAPI;

import javax.faces.bean.ManagedBean;
import org.primefaces.PrimeFaces;

import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.Map;
import java.util.HashMap;

import javax.naming.*;
import java.io.IOException;
/**
 *
 * @author weiyou.teoh
 */
@ManagedBean(name = "information", eager = true)
@RequestScoped
public class Information {
    
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phonenumber;
    private int balance = 0;
    /*userBindAuth variable used in result.xhtml to identify which category the user belong to:
        bindauth (0): user have bind the device and enabled passwordless authentication
        nobindnoauth (1): user have not bind the device and not enabled passwordless authentication 
        bindnoauth (2): user have bind the device but not enabled passwordless authentication
    */
    private int userbindAuth;

    /*Constructor, initialize user information (username, email) from session, and initialize the remaining information by querying database*/
    public Information() {
        try{
            getUserSession();
        }catch (IOException e) {
            System.out.println("Error" + e.getMessage());
        }
        getUserInformation();
    }
    
    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public int getBalance() {
        return balance;
    }

    public int getuserbindAuth() {
        return userbindAuth;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setuserbindAuth(int userbindAuth) {
        this.userbindAuth = userbindAuth;
    }

    //Get the user session from Login.java, if the session is null then redirect user back to index.xhtml
    public void getUserSession() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        HashMap<String, String> hMap = new HashMap();
        hMap = (HashMap) context.getExternalContext().getSessionMap().get("LogonDetail");
        
        if(hMap == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect("index.xhtml?faces-redirect=true");
        }else {
            setUsername(hMap.get("user"));
            setEmail(hMap.get("email"));
            setuserbindAuth(Integer.parseInt(hMap.get("bindAuth")));
        }
    }

    //Query database to retrieve required information
    public void getUserInformation() {
        try{
            Context context = new InitialContext();
            DataSource dataSource = (DataSource) context.lookup("jdbc/mysql");
            Connection con = dataSource.getConnection();

            PreparedStatement statement = con.prepareStatement("select * from user where Email = ?"); 
            statement.setString(1, getEmail());
        
            ResultSet res = statement.executeQuery();
            if(res.next()) {
                String FirstName = res.getString("FirstName");
                String LastName = res.getString("LastName");
                String PhoneNumber = res.getString("PhoneNumber");
                int Balance = res.getInt("Balance");

                setFirstname(FirstName);
                setLastname(LastName);
                setPhonenumber(PhoneNumber);
                setBalance(Balance);
            }else {
                PrimeFaces.current().executeScript("alert('Cannot Retrieve User Information')");
            }
            con.close();
        }catch(Exception e) {
            System.out.println("Error:  " + e.getMessage());
        }
    }
}