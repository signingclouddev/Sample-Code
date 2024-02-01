package com.securemetric.centagate.simpleapp.beans;

import org.primefaces.PrimeFaces;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {

    public void sendEmail(String name, String username, String userEmail, String password) {
        // Sender's and recipient's email addresses
        String senderEmail = "simpleapp123@outlook.com";

        // Sender's email credentials
        String senderUsername = "simpleapp123@outlook.com";
        String senderPassword = "#admin00";

        // Email properties and SMTP server configuration
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.outlook.com");
        props.put("mail.smtp.port", "587");

        // Create a Session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderUsername, senderPassword);
            }
        });

        try {
            // Create a new message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
            message.setSubject("Simple App Account Creation");
            message.setText("Dear " + name + ",\n\n" +
                            "Your Simple App account has successfully been created. Your credentials are as follows:\n" +
                            "Username: " + username + "\n" +
                            "Password: " + password + "\n\n" +
                            "Please login to http://localhost:8085/simpleapp and follow the instructions given. Thank you.\n\n" +
                            "Best regards,\nSimple App Team");

            // Send the message
            Transport.send(message);

            PrimeFaces.current().executeScript("PF('addVotersDialog').hide()");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Voter successfully added", "An email has been sent to notify the voter"));

            System.out.println("Email sent successfully.");
        } catch (MessagingException e) {
            System.out.println("Failed to send email. Error: " + e.getMessage());
        }
    }
}
