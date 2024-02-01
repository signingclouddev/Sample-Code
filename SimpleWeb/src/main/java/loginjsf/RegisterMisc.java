/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package loginjsf;

import loginjsf.CentagateAPI;
import org.primefaces.PrimeFaces;
import org.apache.commons.codec.binary.Base64;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;

import javax.naming.*;
import java.security.*;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
/*
 *
 * @author weiyou.teoh
 */

@ManagedBean(name = "registermisc", eager = true)
@RequestScoped
public class RegisterMisc {
    private String email;
    private String username;
    private String phonenumber;
    private String selectedvalue;
    private String answer;
    private CentagateAPI api = new CentagateAPI();
    private HashMap<String,String> questionList;

    /*Constructor, initialize informations from session and Call Request Question List API from CentagateAPI.java
      to save all the questions in the Hash Map*/
    public RegisterMisc() {
        try{
            getUserSession();
        }catch(IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        questionList = api.getQuestionList();
    }    
   
    public String getEmail() {return email;}
   
    public String getUsername() {return username;}

    public String getPhonenumber() {return phonenumber;} 

    public String getSelectedvalue() {return selectedvalue;}

    public String getAnswer() {return answer;}

    public HashMap<String,String> getQuestionList() {return questionList;}
   
    public void setEmail(String email) {this.email = email;}
   
    public void setUsername(String username) {this.username = username;}
   
    public void setPhonenumber(String phonenumber) {this.phonenumber = phonenumber;}

    public void setAnswer(String answer) {this.answer = answer;}
    
    public void setQuestionList(HashMap<String,String> questionList) {this.questionList = questionList;}
    
    public void setSelectedvalue(String selectedvalue) {this.selectedvalue = selectedvalue;}

    //Used for display primefaces growl message 
    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().
        addMessage(null, new FacesMessage(severity, summary, detail));
    }

    //Get the user session from the Registration.java, if the session is null then redirect user back to register.xhtml
    public void getUserSession() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        HashMap<String, String> hMap = new HashMap();
        hMap = (HashMap) context.getExternalContext().getSessionMap().get("UserDetail");

        if(hMap == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect("register.xhtml?faces-redirect=true");
        }else {
            setUsername(hMap.get("user"));
            setEmail(hMap.get("email"));
        }
    }   

    /*Main function used to register the user phone number and security question:
        Step 1: Add phone number to Centagate Console using API with checking the validation
        Step 2: Update phone number that registered in Registration.java in database
        Step 3: Add security question to Centagate Console using API with checking the validation
        Step 4: redirect user to the registrationresult.xhtml if validation success   
    */
    public void register() throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {

        if(setPhoneNumberforSMS()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "API Error", "Cannot save phone number in Centagate Console, possibly Phone Number in used");
        }else {
            int finalRes;

            Context context = new InitialContext();
            DataSource dataSource = (DataSource) context.lookup("jdbc/mysql");
            Connection con = dataSource.getConnection();

            PreparedStatement statement = con.prepareStatement("select Email from user where email= ?");
            statement.setString(1, getEmail());

            ResultSet result = statement.executeQuery();

            if(result.next()) {
                PreparedStatement stmt = con.prepareStatement("update user set PhoneNumber = ? where Email = ?");
                stmt.setString(1, "+6" + getPhonenumber());
                stmt.setString(2, getEmail());

                finalRes = stmt.executeUpdate();

                if(finalRes != 1) {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Error", "phone number cannot be registered in database");
                }else {
                    if(saveSecurityQuestion()) {
                        addMessage(FacesMessage.SEVERITY_ERROR, "API Error", "Cannot save security question in Centagate Console");
                    }else {
                        redirectToRegistrationResult();
                    }
                }
            }else {
                addMessage(FacesMessage.SEVERITY_INFO, " Email not Existed", "Email not existed in database");
            }
        }
    }

    //function used to call Token Registration API in CentagateAPI.java, return true if success, else return false
    private boolean setPhoneNumberforSMS() throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        boolean numberExisted = false;
        int result = api.userTokenRegistration(getUsername(), "+6" + getPhonenumber());

        if(result != 0) {
            numberExisted = true;
        }

        return numberExisted;
    }
    
    //Function used to calling API to save the user encoded security question, return true if success, else return false 
    public boolean saveSecurityQuestion() throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        boolean questionError = false;
        int result = api.saveQuestion(getUsername(), encodeSecurityQuestion());
    
        if(result != 0) {
            questionError = true;
        }

        return questionError;
    }

    //Encode the question id, question and user answer following the format (QuestionID:Question:Answer)
    public String encodeSecurityQuestion() {
        String [] userQuestionInput = getSelectedvalue().split("=");
        String selectedID = userQuestionInput[0];
        String selectedQuestion = userQuestionInput[1];

        String encodedID = base64Encode(selectedID);
        String encodedQuestion = base64Encode(selectedQuestion);
        String encodedAnswer = base64Encode(getAnswer());

        String encodedString = encodedID + ":" + encodedQuestion + ":"+ encodedAnswer;
        
        return encodedString;
    }

    //Encode String into Base64
    public String base64Encode(String text) {
        Base64 base64 = new Base64();
        String encodedString = new String(base64.encode(text.getBytes()));
        return encodedString;
    }


    //Adding username and email to the sessionKey and redirect user to the RegistrationResult page
    public void redirectToRegistrationResult() throws NoSuchAlgorithmException, 
    InvalidKeyException,IllegalStateException, SignatureException, NoSuchProviderException, Exception
    {
        String method = "register";
        HashMap<String, String> map = new HashMap();
        map.put("method", method);
        map.put("user", getUsername());
        map.put("email", getEmail());

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = ec.getSessionMap();
        sessionMap.put("BindDetail", map);

        ec.redirect("registrationresult.xhtml?faces-redirect=true");
    }
}