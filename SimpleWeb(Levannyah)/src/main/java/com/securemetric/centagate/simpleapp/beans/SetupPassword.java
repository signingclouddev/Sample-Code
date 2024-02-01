package com.securemetric.centagate.simpleapp.beans;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Random;

@ManagedBean(name="setupPassword")
@SessionScoped
public class SetupPassword implements Serializable {

    // Generate random 4-digit number
    private String generateNum() {
        Random random = new Random();
        int number = random.nextInt(10000);
        return String.format("%04d", number);
    }

    public void setup(String name, String username, String userEmail) throws Exception {

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource("https://cloud.centagate.com/CentagateWS/webresources/password/");

        Gson gson = new Gson();

        String integrationKey = "41e90b5523297d0d85acadb5dbb0dc2a82e37753e832af41fddaea16ef9013c2";
        String secretKey = "pq969XmDAyV9";
        String password = username + generateNum();
        String encryptedPassword = encryptPassword(password, secretKey);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("newPassword", encryptedPassword);
        map.put("integrationKey", integrationKey);

        ClientResponse response = service.path("v3").path("registerNewPassword").accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, gson.toJson(map));
        String retJson = response.getEntity(String.class);

        @SuppressWarnings("unchecked")
        HashMap<String, Object> returnData = (HashMap<String, Object>) gson.fromJson(retJson, HashMap.class);

        String code = returnData.get("code").toString();
        String message = returnData.get("message").toString();
        String object = returnData.get("object").toString();

        // response
        System.out.println("Response: " + response);
        System.out.println("Code: " + code);
        System.out.println("Message: " + message);
        System.out.println("Object: " + object);
        System.out.println();

        if ("0".equals(code)) {
            EmailSender emailSender = new EmailSender();
            emailSender.sendEmail(name, username, userEmail, password);
        } else {
            FacesContext.getCurrentInstance().getExternalContext().redirect("voters.xhtml");
        }
    }

    public static String encryptPassword(String content, String key) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] aesKey = digest.digest(key.getBytes());
            SecretKey secretKey = new SecretKeySpec(aesKey,"AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}