package com.securemetric.web.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.securemetric.web.servlet.RestfulUtil;
import com.securemetric.web.util.SecureRestClientTrustManager;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import java.util.HashMap;
import javax.ws.rs.core.MediaType;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author lingkeshra.rajendram
 */
public class ReqMobileSoftCert {

    public HashMap<String, String> Authenticate(String username, String devAccId, String authToken, String ipAddress, String userAgent, String details, String multiStep) {
        String authString;
        HashMap<String, String> responseMap = new HashMap();
        try {
            SecureRestClientTrustManager secureRestClientTrustManager = new SecureRestClientTrustManager();
            String integrationKey = APIController.getIntegrationKey();
            String unixTimestamp = String.valueOf(System.currentTimeMillis() / 1000L);
            String secretKey = APIController.getSecretKey();
            HashMap<String, String> map = new HashMap();
            details = Base64.encodeBase64String(details.getBytes()).replaceAll("(\r|\n)", "").trim();
            authToken = "";
            String hmac = secureRestClientTrustManager.calculateHmac256(
                    secretKey,
                    username + devAccId + details + integrationKey + unixTimestamp + authToken + ipAddress + userAgent);
            
            map.put("username", username);
            map.put("devAccId", devAccId);
            map.put("details", details);
            map.put("authToken", authToken);
            map.put("integrationKey", integrationKey);
            map.put("unixTimestamp", unixTimestamp);
            map.put("ipAddress", ipAddress);
            map.put("userAgent", userAgent);
            map.put("hmac", hmac);
            
            /* Read the output returned from the authentication */
            Gson gson = new Gson();
            com.sun.jersey.api.client.Client client = RestfulUtil.buildClient();//com.sun.jersey.api.client.Client.create ( config );
            WebResource service = client.resource(APIController.getAuthURI() + "/req/requestMobileSoftCert");
            ClientResponse response = service.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, gson.toJson(map));

            if (response.getStatus() == 200) {
                authString = response.getEntity(String.class);
                responseMap = gson.fromJson(authString, HashMap.class);
            }

        } catch (JsonSyntaxException e) {
            System.out.println("Error");
            System.out.println("DEBUG Message[ReqMobileSoftCert] : Failed with exception " + e.getMessage());
        }   catch (ClientHandlerException e) {
            System.out.println("Error");
            System.out.println("DEBUG Message[ReqMobileSoftCert] : Failed with exception " + e.getMessage());
        } catch (UniformInterfaceException e) {
            System.out.println("Error");
            System.out.println("DEBUG Message[ReqMobileSoftCert] : Failed with exception " + e.getMessage());
        }
        return responseMap;
    }
}
