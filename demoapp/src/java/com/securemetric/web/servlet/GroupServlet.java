package com.securemetric.web.servlet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.securemetric.web.api.APIController;
import com.securemetric.web.object.Group;
import com.securemetric.web.object.GroupPolicy;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author User
 */
@WebServlet(urlPatterns = {"/GroupServlet"})
public class GroupServlet extends HttpServlet {

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

        response.setContentType("text/html;charset=utf-8");
        returnData = new HashMap<String, String>();
        out = response.getWriter();
        try {
            if (request.getParameter("mode") != null) {
                String mode = request.getParameter("mode");
                
                String adminId = (String) request.getSession().getAttribute("userid"); 
                String authToken = (String) request.getSession().getAttribute("authToken");
                String secretCode = (String) request.getSession().getAttribute("secretCode"); 
                
                if(!secretCode.isEmpty()) {
                    
                    RegisterServlet registerServlet = new RegisterServlet();
                    registerServlet.refreshAuthToken(authToken,adminId);
                    authToken = registerServlet.getAuthToken();
                    secretCode = registerServlet.getSecretCode();
                    request.getSession().setAttribute("authToken", authToken);
                    request.getSession().setAttribute("secretCode", secretCode);
                    String cenToken = APIController.generateCenToken(secretCode, adminId + authToken);
                    request.getSession().setAttribute("cenToken", cenToken);
                    if (mode.equals("1")) 
                    {   
                        this.readGroupList(adminId, cenToken);
                    } 
                    else {
                        String groupId = request.getParameter("groupId");
                        readGroupDetails(adminId, cenToken, groupId);
                    }
                }
                else {
                    returnData.put("code", "1");
                    System.out.println("DEBUG Message[processRequest] : secretCode is empty ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
        }

    }
    
    private void readGroupList(String userId, String cenToken) {
        System.out.println("DEBUG Message[readGroupList] : Get Group list ");
        /*POST API REQUEST*/

        returnData = new HashMap<String, String>();
        try {
            APIController.TrustAllService();
        } catch (Exception e) {

        }

        /* Send the POST request for authentication */
        Gson gson = new Gson();
        /*GSON library*/

        com.sun.jersey.api.client.Client client = RestfulUtil.buildClient();//com.sun.jersey.api.client.Client.create ( config );

        /* Send the POST request for authentication */
        WebResource service = client.resource(APIController.getBaseURI()).path("group").path("listGroup").path(userId).path(cenToken);

        System.out.println("DEBUG Message[readGroupList] : Get group list ");
        /* Send the POST request for authentication */
        
        ClientResponse response = service.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        
        String json = response.getEntity(String.class);
        System.out.println("DEBUG Message[readGroupList] : Call API :" + json);
        HashMap<Object, Object> returnData2 = (HashMap<Object, Object>) gson.fromJson(json, HashMap.class);
        String code = String.valueOf(returnData2.get("code"));

        /* Read the output returned from the authentication */
        System.out.println("DEBUG Message[readGroupList] : Get group list code = " + code);
        if (code.equals("0.0")) {
            System.out.println("DEBUG Message[readGroupList] : Success get group list");
            List<Group> groupsList = new ArrayList<Group>();
            HashMap<Object, Object> map2 = (HashMap<Object, Object>) gson.fromJson(returnData2.get("object").toString(), HashMap.class);
            List<com.google.gson.internal.LinkedTreeMap<String, Object>> groupList = (ArrayList<com.google.gson.internal.LinkedTreeMap<String, Object>>) map2.get("groupList");
            for (int i = 0; i < groupList.size(); i++) {
                Group insertGroup = new Group();
                insertGroup.setGroupId(groupList.get(i).get("groupId").toString());
                insertGroup.setGroupName(groupList.get(i).get("groupName").toString());
                groupsList.add(insertGroup);
            }
            returnData.put("code", "0");
            System.out.println("DEBUG Message[readGroupList] : return groupsList = " +  gson.toJson ( groupsList ));
            returnData.put("object", gson.toJson ( groupsList ));
            out.print(gson.toJson(returnData));
        } else {
            returnData.put("code", "1");
            System.out.println("DEBUG Message[readGroupList] : failed get group list");
        }
    }
    
    private void readGroupDetails(String userId, String cenToken, String groupId) {
        System.out.println("DEBUG Message[readGroupDetails] : Get Group list ");
        /*POST API REQUEST*/
        String authString = null;

        try {
            APIController.TrustAllService();
        } catch (Exception e) {

        }

        /* Send the POST request for authentication */
        Gson gson = new Gson();
        /*GSON library*/

        com.sun.jersey.api.client.Client client = RestfulUtil.buildClient();//com.sun.jersey.api.client.Client.create ( config );

        /* Send the POST request for authentication */
        WebResource service = client.resource(APIController.getBaseURI()).path("group").path("read").path(userId).path(cenToken).path(groupId);

        System.out.println("DEBUG Message[readGroupDetails] : Get group details ");
        /* Send the POST request for authentication */
        String json = service.accept(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println("DEBUG Message[readGroupDetails] : Call API :" + json);

        HashMap<Object, Object> map = (HashMap<Object, Object>) gson.fromJson(json, HashMap.class);
        String code = String.valueOf(map.get("code"));

        /* Read the output returned from the authentication */
        if (code.equals("0.0")) {
            System.out.println("DEBUG Message[readGroupDetails] : Success get group details");
            List<Group> groupsList = new ArrayList<Group>();
            GroupPolicy groupPolicy = new GroupPolicy();
            com.google.gson.internal.LinkedTreeMap<String, Object> objectRawMap = (com.google.gson.internal.LinkedTreeMap<String, Object>) map.get("object");
            com.google.gson.internal.LinkedTreeMap<String, Object> objectMap = (com.google.gson.internal.LinkedTreeMap<String, Object>) objectRawMap.get("groupDetail");
            if (map.size() > 1) {
            List<com.google.gson.internal.LinkedTreeMap<String, Object>> groupPolicyList = (ArrayList<com.google.gson.internal.LinkedTreeMap<String, Object>>) objectMap.get("groupPolicyList");
            for (int i = 0; i < groupPolicyList.size(); i++) {
                    Double tempDbl = (Double) groupPolicyList.get(i).get("itemId");

                    switch (tempDbl.intValue()) {
                        case 1:
                            groupPolicy.setPwdValidity(Integer.parseInt(groupPolicyList.get(i).get("value").toString()));
                            break;
                        case 2:
                            groupPolicy.setPwdHistory(Integer.parseInt(groupPolicyList.get(i).get("value").toString()));
                            break;
                        case 3:
                            groupPolicy.setPwdLength(Integer.parseInt(groupPolicyList.get(i).get("value").toString()));
                            break;
                        case 4:
                            groupPolicy.setPwdGap(Integer.parseInt(groupPolicyList.get(i).get("value").toString()));
                            break;
                        case 5:
                            groupPolicy.setPwdComplex(Integer.parseInt(groupPolicyList.get(i).get("value").toString()));
                            break;
                        case 6:
                            groupPolicy.setPwdBlackListed(groupPolicyList.get(i).get("value").toString());
                            break;
                        case 7:
                            groupPolicy.setAllowedLoginAttemp(Integer.parseInt(groupPolicyList.get(i).get("value").toString()));
                            break;
                        case 8:
                            groupPolicy.setLockAffective(Integer.parseInt(groupPolicyList.get(i).get("value").toString()));
                            break;
                        case 9:
                            groupPolicy.setTimeOut(Integer.parseInt(groupPolicyList.get(i).get("value").toString()));
                            break;
                        case 10:
                            groupPolicy.setDormant(Integer.parseInt(groupPolicyList.get(i).get("value").toString()));
                            break;
                        case 11:
                            groupPolicy.setAuthOpt(groupPolicyList.get(i).get("value").toString());
                            break;
                        case 14:
                            groupPolicy.setPwdAllowRepeatChar(Integer.parseInt(groupPolicyList.get(i).get("value").toString()));
                            break;
                        case 15:
                            groupPolicy.setPwdAllowEqualUsername(Integer.parseInt(groupPolicyList.get(i).get("value").toString()));
                            break;                            
                        default:
                            break;
                    }
                }
            }
            returnData.put("code", "0");
            returnData.put("object", gson.toJson ( groupPolicy ));
             returnData.put("centoken", cenToken);
            out.print(gson.toJson(returnData));
        } else {
            System.out.println("DEBUG Message[readGroupDetails] : failed get group details");
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
            Logger.getLogger(GroupServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(GroupServlet.class.getName()).log(Level.SEVERE, null, ex);
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
