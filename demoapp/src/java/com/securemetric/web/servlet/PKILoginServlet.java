package com.securemetric.web.servlet;

import com.google.gson.Gson;
import com.securemetric.web.api.PkiAuth;
import com.securemetric.web.util.Config;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

/**
 *
 * @author SecureMetric Technology Sdn. Bhd.
 */
public class PKILoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String SSL_HEADER = "SSL_CLIENT_S_DN";
    private static final String CERTIFICATE_PREFIX = "-----BEGIN CERTIFICATE-----";
    private static final String CERTIFICATE_SUFFIX = "-----END CERTIFICATE-----";
    private SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy, hh:mm:ss a");

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean certificateSent = false;

        String username = request.getParameter("userid");
        String mode = request.getParameter("MODE");
        String browserFp = request.getParameter("browserfp");
        String transValue = request.getParameter("amt");
        if (username == null || username.trim().equals("") || username.trim().equals("undefined")) {

            this.redirectToUnsecuredPort(request, response, "Invalid input");
            return;

        }

        X509Certificate[] certificates = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

        if (certificates == null || certificates[0] == null) {
            if (request.getHeader(SSL_HEADER) != null) {
                String pem = request.getHeader(SSL_HEADER);
                pem = pem.replaceAll(CERTIFICATE_PREFIX, "");
                pem = pem.replaceAll(CERTIFICATE_SUFFIX, "");
                pem = pem.replaceAll("[^\\x20-\\x7e]", ""); //remove unreadable ASCII character
                pem = pem.replaceAll(" ", "");
                pem = pem.replaceAll("\r", "");
                pem = pem.replaceAll("\n", "");
                pem = pem.trim();
                pem = CERTIFICATE_PREFIX + "\n" + pem + "\n" + CERTIFICATE_SUFFIX + "\n";

                /*
                 * Try to construct it as a cert
                 */
                try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(StringUtils.getBytesUtf8(pem));
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    X509Certificate pemCert = (X509Certificate) cf.generateCertificate(bis);
                    bis.close();
                    certificates = new X509Certificate[]{
                        pemCert
                    };
                    certificateSent = true;
                } catch (CertificateException ex) {
                    //this.logger.error ( "fn:doGet" , ex );
                    this.redirectToUnsecuredPort(request, response, "Internal server error");
                } catch (IOException ex) {
                    //       this.logger.error ( "fn:doGet" , ex );
                    this.redirectToUnsecuredPort(request, response, "Internal server error");
                }
            }
        } else {
            certificateSent = true;
        }

        if (certificateSent) {
            String certFingerprint = "";
            try {
                certFingerprint = this.calculateFingerprintSha1(certificates[0]);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(PKILoginServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CertificateEncodingException ex) {
                Logger.getLogger(PKILoginServlet.class.getName()).log(Level.SEVERE, null, ex);
            }

            request.getSession().setAttribute("certFingerprint", certFingerprint);
            request.getSession().setAttribute("MODE", "6");
            request.getSession().setAttribute("userid", username);

            if (request.getParameter("REQ") == null) {
                //Return /LoginServlet
                this.redirectPage(request, response, "/LoginServlet");
            } else {
                HashMap<String, String> authString = new HashMap();
                PkiAuth pkiAuth = new PkiAuth();
                String ipAddress = request.getRemoteAddr();
                String userAgent = request.getHeader("User-Agent");
                String authToken =  (String)request.getSession().getAttribute("authToken");
                String multiStep=(String)request.getSession().getAttribute("multiStep");
                if(multiStep==null)
                {
                    authToken="";
                }
                authString = pkiAuth.Authenticate(username, certFingerprint, authToken, ipAddress, userAgent, browserFp, transValue);
                Gson gson = new Gson();
                String consolelog = (String) request.getSession().getAttribute("consolelog");
                Date d = new Date();
                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("M/dd/yyyy, hh:mm:ss a");
                if (authString != null && !authString.isEmpty()) {
                    int code = Integer.parseInt(authString.get("code"));
                    String authJson = authString.get("object");
                    HashMap<String, String> authMap = gson.fromJson(authJson, HashMap.class);
                    if (code == 0) {
                        consolelog = consolelog + "<br>" + DATE_FORMAT.format(d) + " : " + "PKI Login Successful";
//                        System.out.println("PKI Login Successful");
//                        request.getSession().setAttribute("successMsg", "PKI Login Successful");
//                        request.getSession().setAttribute("authCode", "0");
//                        request.getSession().setAttribute("authMode", 6);
//                        request.getSession().setAttribute("authObMethod", "PKI");
//                        request.getSession().setAttribute("authToken", authMap.get("authToken"));                      
                        //Return TransServlet
                       // System.out.println("\n\n\n\n  authMap.get(multiStepAuth) "+authMap.get("multiStepAuth"));
                        if (authMap.get("multiStepAuth").equals("true")) {
                            request.getSession().setAttribute("multiStep", "true");
                            request.getSession().setAttribute("authMethods", authMap.get("authMethods"));
                            request.getSession().setAttribute("adaptiveScore", authMap.get("adaptiveScore"));
                            request.getSession().setAttribute("authToken", authMap.get("authToken"));
                        }
                        else
                        {
                          request.getSession().setAttribute("multiStep", "");
                        }
                        consolelog = consolelog + "<br>" + DATE_FORMAT.format(d) + " : " + "Transaction Complete Successful";
                        request.getSession().setAttribute("consolelog", consolelog);
                        this.redirectPage(request, response, "/TransServlet");
                    } else {
                        consolelog = consolelog + "<br>" + DATE_FORMAT.format(d) + " : " + "Transaction Failed";
                        request.getSession().setAttribute("consolelog", consolelog);
                        response.setContentType("text/plain");  // Set content type of the response so that jQuery knows what it can expect.
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write("error");
                    }
                } else {
                    consolelog = consolelog + "<br>" + DATE_FORMAT.format(d) + " : " + "Transaction Failed";
                    request.getSession().setAttribute("consolelog", consolelog);
                    response.setContentType("text/plain");  // Set content type of the response so that jQuery knows what it can expect.
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("error");
                }

            }
        }
    }

    private String calculateFingerprintSha1(X509Certificate certificate) throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] der = certificate.getEncoded();
        md.update(der);
        byte[] digest = md.digest();

        return Hex.encodeHexString(digest);
    }

    private void redirectToUnsecuredPort(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws IOException {
        /*
         * Invalidate the session
         */
        request.getSession().invalidate();
        Config config = new Config();
        Properties sysProp = config.getConfig(Config.TYPE_SYSTEM);
        // ResourceBundle resource = ResourceBundle.getBundle("system");

        String insecureLoginUrl = sysProp.getProperty(Config.LOGIN_URL);//resource.getString("login-url-no-client-auth");
        insecureLoginUrl = insecureLoginUrl.concat("?err=" + errorMessage);

        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        writer.write("<html><head><title>CENTAGATE</title></head><body>");
        writer.write("<script type=\"text/javascript\">\n");
        writer.write("    if ( document.execCommand )\n");
        writer.write("        document.execCommand ( \"ClearAuthenticationCache\" ) ;\n");
        writer.write("    if ( window.crypto && window.crypto.logout )\n");
        writer.write("        window.crypto.logout ( ) ;\n\n");
        writer.write("    document.location = \"" + insecureLoginUrl + "\" ;");
        writer.write("</script></body></html>");
        writer.flush();
        writer.close();
    }

    private void redirectPage(HttpServletRequest request, HttpServletResponse response, String redirectPage)
            throws ServletException, IOException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(redirectPage);
        requestDispatcher.forward(request, response);
    }

}
