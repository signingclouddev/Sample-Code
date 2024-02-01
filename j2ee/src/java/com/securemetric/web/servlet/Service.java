/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.web.servlet;


import com.securemetric.web.util.Config;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Properties;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;


/**
 *
 * @author Biau
 */
public class Service extends HttpServlet implements ServletContextListener {
    public static String proxyHost="";  
    public static String proxyPort="";  
    public static String proxyUser="";      
    public static String proxyPass="";  
    public static boolean hasProxy=false;
    public static boolean bypassSslChecking=false;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {        
        try{
            //set the proxy setting            
            System.out.println("System started. Checking needed to apply network proxy or not... [v1.1]");
                   Config config = new Config();
        Properties sysProp = config.getConfig(Config.TYPE_SYSTEM);
            // ResourceBundle resource = ResourceBundle.getBundle("system");
            proxyHost = sysProp.getProperty(Config.PROXY_HOST)==null?"":sysProp.getProperty(Config.PROXY_HOST);
            proxyPort = sysProp.getProperty(Config.PROXY_PORT)==null?"": sysProp.getProperty(Config.PROXY_PORT);
            proxyUser = sysProp.getProperty(Config.PROXY_USER) == null?"":sysProp.getProperty(Config.PROXY_USER);
            proxyPass = sysProp.getProperty(Config.PROXY_PASS) == null?"":sysProp.getProperty(Config.PROXY_PASS);
            final String skipSSL = sysProp.getProperty(Config.BYPASS_SSL) == null?"true":sysProp.getProperty(Config.BYPASS_SSL);
            if (skipSSL.equalsIgnoreCase("true")){
                bypassSslChecking = true;
            }
            
            System.out.println("proxyHost="+proxyHost);
            System.out.println("proxyPort="+proxyPort);
            System.out.println("proxyUser="+proxyUser);
            System.out.println("proxyPass="+proxyPass);
            System.out.println("bypassSSLchecking="+bypassSslChecking);
                        
            if (proxyHost!=null && proxyHost.length()>0 && proxyPort!=null && proxyPort.length()>0){
                System.setProperty("http.proxyHost", proxyHost);
                System.setProperty("http.proxyPort", proxyPort);
                System.out.println("Network proxy needed. Setting proxy server to "+ proxyHost+":"+proxyPort);
                hasProxy=true;
                
                if (proxyUser!=null && proxyUser.length()>0 && proxyPass!=null && proxyPass.length()>0){
                    System.setProperty("http.proxyUser", proxyUser);
                    System.setProperty("http.proxyPassword", proxyPass);
                    
                    System.out.println("Proxy require authentication. Username="+proxyUser+"\tpassword="+proxyPass);

                    Authenticator.setDefault(
                      new Authenticator() {
                        public PasswordAuthentication getPasswordAuthentication() {
                          return new PasswordAuthentication(proxyUser, proxyPass.toCharArray());
                        }
                      }
                    );
                }
            }
                
        }catch(Exception ex){            
            ex.printStackTrace();
        }                
    }
    
    
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //do nothing
    }

    
    
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet srvService</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet srvService at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }catch(Exception ex){
            //System.out.println("Error during init Captcha service. " + ex.getMessage()); 
            ex.printStackTrace();

        }finally {            
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
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
        return "Short description";
    }// </editor-fold>
}
