package com.securemetric.web.servlet;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;

import Decoder.BASE64Decoder;
import java.util.HashMap;

import java.io.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.util.Hashtable;
/**
 *
 * @author lingkeshra.rajendram
 */
public class QrCodeGenerateServlet extends HttpServlet {
    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String redirectPage = "/qrcodegenerate.jsp";
        
        try {

            int mode = Integer.parseInt(request.getParameter("MODE"));
            
            switch(mode){
                case 0:
                    generateQrCode(request, response);
                    break;
                    
                case 1:
                    analyseQrCode(request, response);
                    break;
                
                default:
                    redirectPage(request, response, redirectPage);
                    break;
            }
            
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }
    
    public static void generateQrCode(HttpServletRequest request,  HttpServletResponse response){
        
        try{
            String username = request.getParameter("username");
            String accountnumber = request.getParameter("accountnumber");
            String city = request.getParameter("city");
            String country = request.getParameter("country");
            
            String usernamelength;
            String accountnumberlength;
            String citylength;
            String countrylength;
            
            if (username.length()<10){    
                usernamelength = "0"+ Integer.toString(username.length());
            }else{
                usernamelength = Integer.toString(username.length());
            }
            
            String totalusername = "59" + usernamelength + username;
            
            if (accountnumber.length()<10){    
                accountnumberlength = "0"+ Integer.toString(accountnumber.length());
            }else{
                accountnumberlength = Integer.toString(accountnumber.length());
            }
            
            String totalaccountnumber = "04" + accountnumberlength + accountnumber;
            
            if (city.length()<10){    
                citylength = "0"+ Integer.toString(city.length());
            }else{
                citylength = Integer.toString(city.length());
            }
            
            String totalcity = "60" + citylength + city;
            
            if (country.length()<10){    
                countrylength = "0"+ Integer.toString(country.length());
            }else{
                countrylength = Integer.toString(country.length());
            }
            
            String totalcountry = "58" + countrylength + country;
            
            String qrRespondWithChallenge = "000201"+"010211"+"2778"+"0012com.p2pqrpay"+"01040053"+"020899960300"+"0315998001234567890"+totalaccountnumber+"52046016"+"5303608"+totalcountry+totalusername+totalcity+"6217"+"0506211000"+"0803***"+"6304"+"9DF8";
            
            request.getSession().setAttribute("qrCode", qrRespondWithChallenge);
        
            response.setContentType("text/plain");  // Set content type of the response so that jQuery knows what it can expect.
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(qrRespondWithChallenge);
            
        }catch(Exception e){
            e.printStackTrace();
        }  
    }
    
    public static void analyseQrCode(HttpServletRequest request,  HttpServletResponse response){
        try{            
          Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
          hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);    
          
          String image = "0";

          BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));

          String json;
          if(br != null){
            json = br.readLine();
            Object obj = new JSONParser().parse(json);
            JSONObject jo = (JSONObject) obj;
            System.out.println("THE image: " + jo.get("image"));
            image = (String) jo.get("image");
           }
          
            String[] resultImage = image.split(",");
            
            BufferedImage img = decodeToImage(resultImage[1]);
            
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(img)));
            Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap,hints);
                        
            System.out.println("qrCode Result: " + qrCodeResult.getText());
            
            //FIRST PART QR CODE STRING INFO
            
            String qrCodeFirst = firstQrString(qrCodeResult.getText());
        
            String qrCodeFirstUpdate = withoutMerchantAccountInfo(qrCodeFirst);
        
            String merchantAccountInfo = merchantAccountInfo(qrCodeFirst);
        
            HashMap<String,String> qrCodeFirstIdValue = new HashMap<String,String>();
        
            for(int i=0; i< qrCodeFirstUpdate.length(); i++)
            {
                String subStringforIdqrCodeFirst = substringforId(qrCodeFirstUpdate);
                int subStringforLengthqrCodeFirst = substringforLength(qrCodeFirstUpdate);
                String subStringforValueqrCodeFirst = substringforValue(qrCodeFirstUpdate, subStringforLengthqrCodeFirst);

                String updatedQrCodeFirst = cleanUpString(qrCodeFirstUpdate, subStringforLengthqrCodeFirst);
                qrCodeFirstUpdate = updatedQrCodeFirst;

                qrCodeFirstIdValue.put(subStringforIdqrCodeFirst, subStringforValueqrCodeFirst);
            }
            
            //SECOND PART QR CODE STRING INFO
            
            String qrCode = initqrCodeString(qrCodeResult.getText());
        
            HashMap<String,String> qrCodeIdValue = new HashMap<String,String>();

            for (int i=0; i< qrCode.length(); i++)
            {
                String subStringforId = substringforId(qrCode);
                int subStringforLength= substringforLength(qrCode);
                String subStringforValue= substringforValue(qrCode, subStringforLength);

                String updateQrCode = cleanUpString(qrCode, subStringforLength);
                qrCode = updateQrCode;

                qrCodeIdValue.put(subStringforId, subStringforValue);
            }
                        
            String responseWriter = qrCodeFirstIdValue.get("00") + "|" +  qrCodeFirstIdValue.get("01") + "|" + merchantAccountInfo + "|" + qrCodeIdValue.get("00") + "|" + qrCodeIdValue.get("01") + "|" +  qrCodeIdValue.get("02") + "|" + qrCodeIdValue.get("03") + "|" + qrCodeIdValue.get("04") + "|" + qrCodeIdValue.get("52") + "|" + qrCodeIdValue.get("53") + "|" + qrCodeIdValue.get("58") + "|" + qrCodeIdValue.get("59") + "|" + qrCodeIdValue.get("60") + "|" + qrCodeIdValue.get("62");
            
            System.out.println(responseWriter);
            
            response.setContentType("text/plain");  // Set content type of the response so that jQuery knows what it can expect.
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(responseWriter);
            
        }catch(Exception e){
            e.printStackTrace();
        }  
    }
    
    private static BufferedImage decodeToImage(String imageString) {
        BufferedImage image = null;
        byte[] imageByte;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
    
    public static String withoutMerchantAccountInfo(String qrCodeString){
    
        String qrCode = qrCodeString.substring(0, 12);
        
        return qrCode;
    }
    
    public static String merchantAccountInfo(String qrCodeString){
    
        String qrCode = qrCodeString.substring(12);
        
        return qrCode;
    }
    
    public static String firstQrString(String qrCodeString){
    
        String qrCode = qrCodeString.substring(0, 16);
        
        return qrCode;
    }
    
    
    private static  String initqrCodeString(String qrCodeString){
    
        String qrCode = qrCodeString.substring(16);
        
        return qrCode;
    }
    
    
    private static String substringforId( String qrCode )
    {
        String qrCodeId = qrCode.substring(0,2);
        
        return qrCodeId;
    }
    
    private static int substringforLength( String qrCode )
    {
        String qrCodeLength = qrCode.substring(2,4);
        
        return Integer.parseInt(qrCodeLength);
    }
    
    private static String substringforValue( String qrCode, int length )
    {
        String cleanUpVersion = qrCode.substring(4);
        
        String qrCodeValue = cleanUpVersion.substring(0,length);
        
        return qrCodeValue;
    }
    
    private static String cleanUpString( String qrCode, int length){
        String cleanUpVersion = qrCode.substring(4);
        
        String qrCodeValue = cleanUpVersion.substring(length);
         
        return qrCodeValue;
    } 
    
    private void redirectPage(HttpServletRequest request, HttpServletResponse response, String redirectPage)
            throws ServletException, IOException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(redirectPage);
        requestDispatcher.forward(request, response);
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
        return "Short description";
    }// </editor-fold>
    
}
