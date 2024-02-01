/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.web.api;

import com.google.gson.Gson;
import com.securemetric.web.servlet.RestfulUtil;
import com.securemetric.web.util.SecureRestClientTrustManager;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.util.HashMap;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author deskvdi3
 */
public class ReqOTPAuth {

    private String strHMAC = null;

    public HashMap<String, String> requestOTP(String username, String password, String ipAddress, String userAgent, String authToken, String multistep) {
        /*requestSmsOtp*/
        String authString = null;
        HashMap<String, String> responseMap = new HashMap();
        try {
            SecureRestClientTrustManager secureRestClientTrustManager = new SecureRestClientTrustManager();
            /* The integration key. This is the key you get from the update app page inside the CENTAGATE */

            String integrationKey = APIController.getIntegrationKey();;
            /* The current time in second (GMT+00:00) */
            String unixTimestamp = String.valueOf(System.currentTimeMillis() / 1000L);
            /* The secret key. This is the key you get from the update app page inside the CENTAGATE */
            String secretKey = APIController.getSecretKey();
            /* Put all the required parameters for basic authentication */
            HashMap<String, String> map = new HashMap();
            //System.out.println("\n\n\n request otp: "+multistep+"   "+authToken);
            if (multistep != null && multistep.equals("true")) {
                map.put("username", username);
                map.put("integrationKey", integrationKey);
                map.put("unixTimestamp", unixTimestamp);
                map.put("ipAddress", ipAddress);
                /* The client’s IP address */
                map.put("userAgent", userAgent);

                map.put("authToken", authToken);
                /* The client’s user agent */
                map.put("hmac", secureRestClientTrustManager.calculateHmac256(
                        secretKey,
                        username + integrationKey + unixTimestamp + authToken + ipAddress + userAgent));
             
            } else {
                
                map.put("username", username);
                map.put("integrationKey", integrationKey);
                map.put("unixTimestamp", unixTimestamp);
                map.put("ipAddress", ipAddress);
                /* The client’s IP address */
                map.put("userAgent", userAgent);

                // map.put("authToken", authToken);
                /* The client’s user agent */
                map.put("hmac", secureRestClientTrustManager.calculateHmac256(
                        secretKey,
                        username + integrationKey + unixTimestamp + ipAddress + userAgent));

            }

            Gson gson = new Gson();
            /*GSON library*/

            com.sun.jersey.api.client.Client client = RestfulUtil.buildClient();//com.sun.jersey.api.client.Client.create ( config );
            /* Send the POST request for authentication */
            WebResource service = client.resource(APIController.getAuthURI() + "/req/requestOtp");
            ClientResponse response = service.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, gson.toJson(map));
            System.out.println("DEBUG Message[ReqOTPAuth] : call API" + response.toString());
            /* Read the output returned from the authentication */
            if (response.getStatus() == 200) {
                authString = response.getEntity(String.class);
                responseMap = gson.fromJson(authString, HashMap.class);
                System.out.println("DEBUG Message[ReqOTPAuth] : call API" + authString);
            }

        } catch (Exception e) {
            System.out.println("Error");
            System.out.println("DEBUG Message[ReqOTPAuth] : Failed with exception " + e.getMessage());
            e.printStackTrace();
        }

        return responseMap;
    }
}
