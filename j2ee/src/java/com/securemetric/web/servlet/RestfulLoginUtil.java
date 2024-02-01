package com.securemetric.web.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.securemetric.web.api.APIController;
import com.securemetric.web.util.Config;
import com.securemetric.web.util.SecureRestClientTrustManager;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

/**
 *
 * @author USER
 */
public class RestfulLoginUtil extends HttpServlet {

    private final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy, hh:mm:ss a");
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Config config = new Config();
        Properties sysProp = config.getConfig(Config.TYPE_SYSTEM);
        String integrationKey = sysProp.getProperty(Config.INTEGRATION_KEY);
        String secretKey = sysProp.getProperty(Config.SECRET_KEY);

        PrintWriter out = response.getWriter();
        String consolelog = (String) request.getSession().getAttribute("consolelog");
        Date d = new Date();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("M/dd/yyyy, hh:mm:ss a");
        try {
            if (request.getSession().getAttribute("userid") != null
                    && request.getSession().getAttribute("authToken") != null
                    && request.getParameter("mode") != null
                    && request.getParameter("mode").equals("refreshauthstate")) {
                
                String userAgent = request.getHeader("User-Agent");
                SecureRestClientTrustManager secureRestClientTrustManager = new SecureRestClientTrustManager();
                //challenge the timeout
                String userid = request.getSession().getAttribute("userid").toString();
                String password = request.getSession().getAttribute("password").toString();
                
                String devAccId = (String) request.getSession().getAttribute("devAccId");
                
                String pageid = request.getParameter("pageid");
                String username = getUserLoginName(userid);
                String authObMethod = request.getSession().getAttribute("authObMethod") != null ? request.getSession().getAttribute("authObMethod").toString() : "";
                String authToken = request.getSession().getAttribute("authToken").toString();
                String unixTimestamp = String.valueOf(System.currentTimeMillis() / 1000L);
                String hmac = secureRestClientTrustManager.calculateHmac256(
                        secretKey,
                        username + authObMethod + integrationKey + unixTimestamp + authToken + userAgent);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("username", username);
                map.put("authToken", authToken);
                map.put("authMethod", authObMethod);
                map.put("integrationKey", integrationKey);
                map.put("unixTimestamp", unixTimestamp);
                map.put("userAgent", userAgent);
                map.put("hmac", hmac);

                Client client = RestfulLoginUtil.buildClient();//Client.create ( config );
                WebResource service = client.resource(APIController.getBaseURI());

                Gson gson = new Gson();
                
                ClientResponse clientResponse = service.path("session").path("statecheck").accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, gson.toJson(map));
                System.out.println("DEBUG Message[statecheck] : Call getStatus " +   gson.toJson(map));
                System.out.println("DEBUG Message[statecheck] : Call getStatus " +  clientResponse.getStatus());
                if (clientResponse.getStatus() == ClientResponse.Status.OK.getStatusCode()) {

                    String json = clientResponse.getEntity(String.class);
                    System.out.println("json =" + json);
                    HashMap<String, String> responseMap = gson.fromJson(json, HashMap.class);

                    String code = responseMap.get("code");
                    String message = responseMap.get("message");

                    if (code.equals("0")) {
                        
                        String authJson = responseMap.get("object");
                        HashMap<String, String> authMap = gson.fromJson(authJson, HashMap.class);
                        
                        //String multiStepAuth = authMap.get("multiStepAuth");
                        //System.out.println("MultiStepAuth: " + authMap.get("multiStepAuth"));
                        String multiStepAuth = null;
                        String authMethods = authMap.get("authMethods");
                        String adaptiveScore = authMap.get("adaptiveScore");
                        authToken = authMap.get("authToken");
                        String secretCode = authMap.get("secretCode");
                        String role = authMap.get("role");
                        String groupId = authMap.get("groupId");
                        String email= authMap.get("email");
                        /* STORED SESSION */
                        request.getSession().setAttribute("successMsg", "Login Successful");
                        request.getSession().setAttribute("returnCode", "0");
                        request.getSession().setAttribute("userid", userid);
                        request.getSession().setAttribute("password", password);
                        request.getSession().setAttribute("devAccId", devAccId);
                        request.getSession().setAttribute("multiStepAuth", multiStepAuth);
                        request.getSession().setAttribute("authToken", authToken);
                        request.getSession().setAttribute("secretCode", secretCode);
                        request.getSession().setAttribute("adaptiveScore", adaptiveScore);
                        request.getSession().setAttribute("authMethods", authMethods);
                        request.getSession().setAttribute("role", role);
                        request.getSession().setAttribute("groupId", groupId);
                        request.getSession().setAttribute("email", email);
                        request.getSession().setAttribute("centagateUserId", userid);
                        
                        request.getSession().setAttribute("loginSession", "1");

                        consolelog = (null == request.getSession().getAttribute("consolelog")) ? null
                                : request.getSession().getAttribute("consolelog").toString();
                        consolelog += "<br>" + format.format(new Date()) + " : ";
                        consolelog += " Login success";
                        request.getSession().setAttribute("consolelog", consolelog);
                        out.print("1|" + message);
                    } else if (code.equals("23007")) {
                        //pending                            
                        out.print("2|" + message);
                    } else {
                        consolelog = consolelog + "<br>" + DATE_FORMAT.format(d) + " : " + "Authentication Failed";
                        out.print("0|" + message);
                    }
                    
                } else {
                    System.out.println("DEBUG Message[Check State API] : Call API " + clientResponse.toString());
                    System.out.println("DEBUG Message[Check State API] : Call API " + gson.toJson(map));
                    consolelog = consolelog + "<br>" + DATE_FORMAT.format(d) + " : " + "Authentication Failed";
                    out.print("-1|" + "Invalid credentials");
                }
            } else {
                consolelog = consolelog + "<br>" + DATE_FORMAT.format(d) + " : " + "Authentication Failed";
                out.print("-1|" + "Invalid Inputs");
            }
        } catch (JsonSyntaxException ex) {
            System.out.println("DEBUG Message[RestfulUtil] : Failed with exception : " + ex.getMessage());
        } catch (ClientHandlerException ex) {
            System.out.println("DEBUG Message[RestfulUtil] : Failed with exception : " + ex.getMessage());
        } catch (UniformInterfaceException ex) {
            System.out.println("DEBUG Message[RestfulUtil] : Failed with exception : " + ex.getMessage());
        } finally {
            request.getSession().setAttribute("consolelog", consolelog);
            out.close();
        }
    }

    private String getUserLoginName(String username) {
        String loginname = "";
        try {
            loginname = username;
        } catch (Exception e) {
        }

        return loginname;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //throw new ServletException("Methods not supported");
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "RESTful utility";
    }// </editor-fold>

    public static Client buildClient() {
        try {
            System.setProperty("jsse.enableSNIExtension", "false");
            System.setProperty("https.protocols", "SSLv3,TLSv1,TLSv1.1,TLSv1.2");
            DefaultClientConfig config = new DefaultClientConfig();

            if (Service.bypassSslChecking) {
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                }}, new java.security.SecureRandom());

                Map<String, Object> properties = config.getProperties();
                HTTPSProperties httpsProperties = new HTTPSProperties(
                        new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                }, sslcontext
                );
                properties.put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, httpsProperties);
                config.getClasses().add(JacksonJsonProvider.class);
            }
            return new Client(new URLConnectionClientHandler(
                    new HttpURLConnectionFactory() {
                Proxy p = null;

                @Override
                public HttpURLConnection getHttpURLConnection(URL url)
                        throws IOException {
                    if (p == null) {
                        if (Service.hasProxy) {
                            p = new Proxy(Proxy.Type.HTTP,
                                    new InetSocketAddress(
                                            Service.proxyHost,
                                            Integer.parseInt(Service.proxyPort)));
                        } else {
                            p = Proxy.NO_PROXY;
                        }
                    }
                    return (HttpURLConnection) url.openConnection(p);
                }
            }), config);
            //return ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier().build();
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
