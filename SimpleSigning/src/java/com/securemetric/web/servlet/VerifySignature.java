package com.securemetric.web.servlet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.google.gson.Gson;
import com.securemetric.web.controller.APIController;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author User
 */
@WebServlet(urlPatterns = {"/VerifySignature"})
public class VerifySignature extends HttpServlet {

    private PrintWriter out;
    private HashMap<String, String> returnData;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        if (returnData == null) {
            returnData = new HashMap<String, String>();
        }

        response.setContentType("text/html;charset=utf-8");

        out = response.getWriter();
        try {
            Gson gson = new Gson();

            String ipAddress = request.getRemoteAddr();;
            String userAgent = request.getHeader("User-Agent");
            
            String username = request.getParameter("username");
            String plainText = request.getParameter("plainText");
            String signature = request.getParameter("signature");
            String algo = "0";
            
            int result = -1;
            APIController apiCall = new APIController();
            plainText = apiCall.convertWebSafeBase64ToNormalBase64(plainText);
            signature = apiCall.convertWebSafeBase64ToNormalBase64(signature);
            
            HashMap<String, String> resultMap = apiCall.Authenticate(username, plainText, signature, algo, "", ipAddress, userAgent);
            
            result = Integer.parseInt(resultMap.get("code"));

            if (result == 0) {
                returnData.put("code", "0");
                returnData.put("message", resultMap.get("message"));
            } else {
                returnData.put("code", "1");
                returnData.put("message", resultMap.get("message"));
            }
            
            out.print(gson.toJson(returnData));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(VerifySignature.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(VerifySignature.class.getName()).log(Level.SEVERE, null, ex);
        }
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
