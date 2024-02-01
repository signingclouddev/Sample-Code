/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.web.servlet;

import com.google.gson.Gson;
import com.securemetric.web.api.APIController;
import com.securemetric.web.controller.UserManagement;
import com.securemetric.web.util.Config;
import com.securemetric.web.util.SecureRestClientTrustManager;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
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
import java.util.Enumeration;
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
public class RestfulUtil extends HttpServlet {

    //  private static final String INTEGRATION_KEY = "a6b99b9adbcf64e5c440a1733b27fc0e6045fa019315bfe52863fabec91b70ef";
    //  private static final String SECRET_KEY = "0X1u9YO3zQuk";
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
        response.addHeader("X-Frame-Options", "DENY");
        response.addHeader("X-XSS-Protection", "1; mode=block");
        response.addHeader("X-Content-Type-Options", "nosniff");

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();
        String consolelog = (String) request.getSession().getAttribute("consolelog");
        Date d = new Date();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("M/dd/yyyy, hh:mm:ss a");
        try {
            if (request.getSession().getAttribute("userid") != null
                    && request.getSession().getAttribute("authToken") != null
                    && request.getParameter("mode") != null
                    && request.getParameter("mode").equals("refreshauthstate")) {

                String ipAddress = request.getRemoteAddr();
                InetAddress thisIp = InetAddress.getLocalHost();
                if (ipAddress.equals("127.0.0.1")) {
                    ipAddress = thisIp.getHostAddress();
                }
                String userAgent = request.getHeader("User-Agent");
                SecureRestClientTrustManager secureRestClientTrustManager = new SecureRestClientTrustManager();
                //challenge the timeout
                String userid = request.getSession().getAttribute("userid").toString();
                String email = getUserLoginName(userid);
                String authObMethod = request.getSession().getAttribute("authObMethod") != null ? request.getSession().getAttribute("authObMethod").toString() : "";
                String authToken = request.getSession().getAttribute("authToken").toString();

                String unixTimestamp = String.valueOf(System.currentTimeMillis() / 1000L);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("username", email);
                map.put("authToken", authToken);
                map.put("authMethod", authObMethod);
                map.put("integrationKey", integrationKey);
                map.put("unixTimestamp", unixTimestamp);
                //    map.put ( "ipAddress" , ipAddress );
                map.put("userAgent", userAgent);
                map.put("hmac", secureRestClientTrustManager.calculateHmac256(
                        secretKey,
                        email + authObMethod + integrationKey + unixTimestamp + authToken + userAgent));

                Client client = RestfulUtil.buildClient();//Client.create ( config );
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
                        authToken = authMap.get("authToken");

                        String secretCode = authMap.get("secretCode");
                        consolelog = consolelog + "<br>" + DATE_FORMAT.format(d) + " : " + "Transaction Complete Successful";
                        String multiStepAuth = authMap.get("multiStepAuth");
                        String authMethods = authMap.get("authMethods");
                        String adaptiveScore = authMap.get("adaptiveScore");
//                        String amt = (String) request.getSession().getAttribute("amt");
//                        String fromacct = (String) request.getSession().getAttribute("fromacct");
//                        String toacc = (String) request.getSession().getAttribute("toacc");
//
//                       
                        request.getSession().setAttribute("consolelog", consolelog);
                        
                        request.getSession().setAttribute("secretCode", secretCode);
                        request.getSession().setAttribute("userid", userid);
                        //request.getSession().setAttribute("password", password);
                        request.getSession().setAttribute("multiStepAuth", multiStepAuth);
                        //request.getSession().setAttribute("authToken", authToken);
                        //  request.getSession().setAttribute("secretCode", secretCode);
                        request.getSession().setAttribute("adaptiveScore", adaptiveScore);
                        request.getSession().setAttribute("authMethods", authMethods);
                        request.getSession().setAttribute("authToken", authToken);
                        request.getSession().setAttribute("smsEnabled", "true");
                        request.getSession().setAttribute("otpEnabled", "true");
                        request.getSession().setAttribute("crOtpEnabled", "true");
                        request.getSession().setAttribute("pkiEnabled", "true");
                        request.getSession().setAttribute("qnaEnabled", "true");
                        request.getSession().setAttribute("qrCodeEnabled", "true");
                        request.getSession().setAttribute("mobileSoftCertEnabled", "true");

                        if (multiStepAuth != null && multiStepAuth.equals("true")) {
                            
                            List<String> availAuthList = new ArrayList<String>();
                            availAuthList.addAll(Arrays.asList(authMethods.split(",")));
                            
                            if (availAuthList.contains("SMS")) {
                                request.getSession().setAttribute("smsEnabled", "false");
                            }

                            if (availAuthList.contains("OTP")) {
                                request.getSession().setAttribute("otpEnabled", "false");
                            }

                            if (availAuthList.contains("CROTP")) {
                                request.getSession().setAttribute("crOtpEnabled", "false");
                            }

                            if (availAuthList.contains("QRCODE")) {
                                request.getSession().setAttribute("qrCodeEnabled", "false");
                            }

                            if (availAuthList.contains("PKI")) {
                                request.getSession().setAttribute("pkiEnabled", "false");
                            }

                            if (availAuthList.contains("MSOFTCERT")) {
                                request.getSession().setAttribute("mobileSoftCertEnabled", "false");
                            }
                            //   String redirectPage = "/multiStep.jsp";
                            out.print("1|true");
                            //   redirectPage(request, response, "/multiStepTrans.jsp");
                            ///   System.out.println("*********ready for step up : \n" + request);
                            //   returnStatus = "0";
                        } else {
                            out.print("1|false");
                            request.getSession().setAttribute("multiStepAuth", "");
                            request.getSession().setAttribute("multiStep", "");
                            request.getSession().setAttribute("authToken", null);
                            request.getSession().setAttribute("crOtpEnabled", "true");
                            request.getSession().setAttribute("pkiEnabled", "true");
                            request.getSession().setAttribute("qrCodeEnabled", "true");

                        }

                    } else if (code.equals("23007")) {
                        //pending                            
                        out.print("2|" + message);
                    } else {
                        consolelog = consolelog + "<br>" + DATE_FORMAT.format(d) + " : " + "Transaction Failed";
                        out.print("0|" + message);
                    }
                } else {
                    System.out.println("DEBUG Message[OTPAuth] : Call API " + clientResponse.toString());
                    System.out.println("DEBUG Message[OTPAuth] : Call API " + gson.toJson(map));
                    //server error. logout user
                    //-1 = send to login page
                    consolelog = consolelog + "<br>" + DATE_FORMAT.format(d) + " : " + "Transaction Failed";
                    out.print("-1|" + "Invalid credentials");
                    return;
                }
            } else {
                consolelog = consolelog + "<br>" + DATE_FORMAT.format(d) + " : " + "Transaction Failed";
                out.print("-1|" + "Invalid Inputs");
            }
        } catch (Exception ex) {
            System.out.println("DEBUG Message[RestfulUtil] : Failed with exception : " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            request.getSession().setAttribute("consolelog", consolelog);
            out.close();
        }
    }

    private String getUserLoginName(String username) {
        String loginname = "";
        try {
            //    UserManagement user = new UserManagement();
            loginname = username;
        } catch (Exception e) {
            e.printStackTrace();
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

    private void redirectPage(HttpServletRequest request, HttpServletResponse response, String redirectPage)
            throws ServletException, IOException {

        RequestDispatcher requestDispatcher = request.getRequestDispatcher(redirectPage);
        requestDispatcher.forward(request, response);
    }
}
