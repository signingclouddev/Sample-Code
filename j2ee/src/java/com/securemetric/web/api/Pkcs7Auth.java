/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.web.api;

import com.google.gson.Gson;
import com.securemetric.web.servlet.RestfulUtil;
import com.securemetric.web.util.SecureRestClientTrustManager;
import java.util.HashMap;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 *
 * @author auyong
 */
public class Pkcs7Auth {

    private String strHMAC = null;

    public HashMap<String, String> Authenticate(String username, String plainText, String signature, String algorithm, String authToken, String ipAddress, String userAgent) {
        /*POST API REQUEST*/

        String authString = null;
        HashMap<String, String> responseMap = new HashMap();
        try {
            SecureRestClientTrustManager secureRestClientTrustManager = new SecureRestClientTrustManager();
            /* The integration key. This is the key you get from the update app page inside the CENTAGATE */
            String integrationKey = APIController.getIntegrationKey();
            /* The current time in second (GMT+00:00) */
            String unixTimestamp = String.valueOf(System.currentTimeMillis() / 1000L);
            /* The secret key. This is the key you get from the update app page inside the CENTAGATE */
            String secretKey = APIController.getSecretKey();
            /* Put all the required parameters for basic authentication */
            HashMap<String, String> map = new HashMap();
            map.put("username", username);
            map.put("plainText", plainText);
            map.put("signature", signature);
            map.put("integrationKey", integrationKey);
            map.put("unixTimestamp", unixTimestamp);
            map.put("algorithm", algorithm);
            map.put("ipAddress", ipAddress); /* The client’s IP address */

            map.put("userAgent", userAgent); /* The client’s user agent */

            map.put("hmac", secureRestClientTrustManager.calculateHmac256(
                    secretKey,
                    username + signature + algorithm + plainText + integrationKey + unixTimestamp + authToken + ipAddress + userAgent));

            if (authToken != null) {
                map.put("authToken", authToken);
            }

            /* Read the output returned from the authentication */
            Gson gson = new Gson(); /*GSON library*/

            com.sun.jersey.api.client.Client client = RestfulUtil.buildClient();//com.sun.jersey.api.client.Client.create ( config );
            WebResource service = client.resource(APIController.getAuthURI() + "/auth/authPkiPkcs7");
            ClientResponse response = service.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, gson.toJson(map));
            System.out.println("DEBUG Message[authPkiPkcs7] : Call API authToken " + authToken);
            System.out.println("DEBUG Message[authPkiPkcs7] : Call API " + response.toString());
            if (response.getStatus() == 200) {
                authString = response.getEntity(String.class);
                responseMap = gson.fromJson(authString, HashMap.class);
                System.out.println("DEBUG Message[authPkiPkcs7] : " + authString);
            }

        } catch (Exception e) {
            System.out.println("DEBUG Message[authPkiPkcs7] : Failed with exception " + e.getMessage());
            e.printStackTrace();
        }

        return responseMap;
    }
}
