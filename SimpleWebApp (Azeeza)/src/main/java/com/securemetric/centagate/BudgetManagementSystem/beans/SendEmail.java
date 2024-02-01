package com.securemetric.centagate.BudgetManagementSystem.beans;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@ManagedBean(name="sendEmail")
@SessionScoped
public class SendEmail {
    private String userEmail;
    private String newUsername;
    private String password;

    public SendEmail(String userEmail, String newUsername, String password) {
        this.userEmail = userEmail;
        this.newUsername = newUsername;
        this.password = password;
    }

    public String EmailSender() throws Exception {
        // Sender's email and password
        String senderEmail = "azeezamohd@outlook.com";
        String senderPassword = "Acombeng1203_";

        // Recipient's email address
        String recipientEmail = userEmail;

        // Email subject and content
        String emailSubject = "User Activation";
        String emailContent = "Hi, Welcome to Budget Management System,\n" +
                "Your account has been created. To start using your account, please login using the following credentials.\n\n" +
                "Username: " + newUsername + "\n" +
                "Password: " + password + "\n\n" +
                "To ensure the security of your account, please follow the instructions below on how to update your password: \n" +
                "1. Login using the credentials given above. \n" +
                "2. Once you logged in, navigate to your username with profile icon on the top navigation menu, and choose 'Change Password' menu from the drop down menu. \n" +
                "3. You will be redirected to the Update Password page where you can enter the current password [given above] for verification purposes. Please enter in correctly. \n" +
                "4. Next, enter your desired new password. Make sure to choose a strong and unique password for better security. \n" +
                "5. Confirm your new password by click on the 'Confirm' button. \n" +
                "6. Your password has now been successfully changed. From this point forward, you will need to use your new password to log in to your account. \n\n" +
                "THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL";

        // SMTP server configuration
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.outlook.com");
        properties.put("mail.smtp.port", "587");

        // Create a session with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create a new email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(emailSubject);
            message.setText(emailContent);

            // Send the email
            Transport.send(message);

            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            System.out.println("Failed to send email. Error: " + e.getMessage());
        }
        return null;
    }
}
