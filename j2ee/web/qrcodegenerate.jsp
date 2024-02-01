<%-- 
    Document   : main
    Created on : Jul 15, 2015, 3:27:27 PM
    Author     : auyong
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page language = "java" %>
<%@ page session= "true" %>
<%@ page import = "java.util.*" %>
<%@ page import = "com.securemetric.web.util.DateCalculator" %>
<%@ page import = "com.securemetric.web.util.Config"%>
<!DOCTYPE html>

<%
    Config sysConfig = new Config();
    Properties sysProp = sysConfig.getConfig(Config.TYPE_SYSTEM);
//ResourceBundle resource = ResourceBundle.getBundle("system");
    String BaseURL = sysProp.getProperty(Config.BASE_URL);//resource.getString("baseurl");
    String authOption = sysProp.getProperty(Config.AUTH_OPTION);//resource.getString("authOption");
    String userid = (String) request.getSession().getAttribute("userid");
    String password = (String) request.getSession().getAttribute("password");
    String loginSession = (String) request.getSession().getAttribute("loginSession");
    String otpChallengeDisplay = (String) request.getSession().getAttribute("otpChallengeDisplay");
    String multiStep = (String) request.getSession().getAttribute("multiStepAuth");
    String authToken = (String) request.getSession().getAttribute("authToken");
    String consolelog = (String) request.getSession().getAttribute("consolelog");
    if(consolelog.length()>5000)
    {
        consolelog="";
    }
    //  String otpChallenge = "";
    DateCalculator dateCalculator = new DateCalculator();
    String todayDate = dateCalculator.convertToDate(System.currentTimeMillis(), "yyyy-MM-dd");
    String otpChallenge = (String) request.getSession().getAttribute("otpChallenge");

    userid = (String) request.getSession().getAttribute("userid");
    password = (String) request.getSession().getAttribute("password");
    String role = (String) request.getSession().getAttribute("role");
    
    String smsAuth = (String) request.getSession().getAttribute("SMS");
    String otpAuth = (String) request.getSession().getAttribute("OTP");
    String crOtpAuth = (String) request.getSession().getAttribute("CROTP");
    String pkiAuth = (String) request.getSession().getAttribute("PKI");
    String fidoAuth = (String) request.getSession().getAttribute("FIDO");
    String qrAuth = (String) request.getSession().getAttribute("QRCODE");
    String pushAuth = (String) request.getSession().getAttribute("PUSH");
    String pushDisplay = "";
    String smsDisplay = "";
    String otpDisplay = "";
    String crOtpDisplay = "";
    String pkiDisplay = "";
    String qrDisplay = "";
    String fidoDisplay = "";
    
    String qrCode = (String) request.getSession().getAttribute("qrCode");
    
    if (smsAuth == null) {
        smsDisplay = "style=\"visibility: hidden\"";
    }

    if (otpAuth == null) {
        otpDisplay = "style=\"visibility: hidden\"";
    }
    if (crOtpAuth == null) {
        crOtpDisplay = "style=\"visibility: hidden\"";
    }
    if (pkiAuth == null) {
        pkiDisplay = "style=\"visibility: hidden\"";
    }
    if (qrAuth == null) {
        qrDisplay = "style=\"visibility: hidden\"";
    }
    
    if (fidoAuth == null) {
        fidoDisplay = "style=\"visibility: hidden\"";
    }
    
    if (pushAuth == null) {
        pushDisplay = "style=\"visibility: hidden\"";
    }
    
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <title>Main Page</title>
        <style type="text/css"><%@ include file="/styles/login.css" %></style>
        <style type="text/css"><%@ include file="/styles/styles.css" %></style>
        <style type="text/css"><%@ include file="/styles/w3.css" %></style>
        <script language="javascript" src="javascript/JSfunction.js"></script>
        <script language="javascript" src="javascript/JSFormat.js"></script>
        <script language="javascript" src="javascript/JSInvoke.js"></script>
        <script language="javascript" src="javascript/jquery-ui-1.10.4.js"></script>
        <script type="text/javascript" src="javascript/fingerprint2.min.js"></script>
        <script language="javascript" src="javascript/centagate-agent.js"></script>
        <script type="text/javascript" src="javascript/json2.js"></script>            
        <script type="text/javascript" src="javascript/base64url-arraybuffer.js"></script>
        <script type="text/javascript" src="javascript/fido.js"></script>
        <script type="text/javascript" src="javascript/qcode-decoder.min.js"></script>
        <script language="javascript">
            
            var baseURL = getBaseURL();
            d = new Date();
            function setWINDOW()
            {
                if ('<%=loginSession%>' !== '1') {
                    document.getElementById('fade').style.display = 'block';
                    document.getElementById('notice-message').style.display = 'block';
                    document.getElementById('notice-text').innerHTML = "Require to login";
                    setTimeout(function () {
                        window.location = (baseURL + "/login.jsp");
                    }, 2000);
                }
            }

            function generate()
            {
                if (CheckMandatory(window.document.FORM))
                {
                    window.document.FORM.username.className = "input-display";
                    window.document.FORM.username.readOnly = true;
                    window.document.FORM.username.tabIndex = "-1";

                    window.document.FORM.accountnumber.className = "input-display";
                    window.document.FORM.accountnumber.readOnly = true;
                    window.document.FORM.accountnumber.tabIndex = "-1";

                    window.document.FORM.city.className = "input-display";
                    window.document.FORM.city.readOnly = true;
                    window.document.FORM.city.tabIndex = "-1";
                    
                    window.document.FORM.country.className = "input-display";
                    window.document.FORM.country.readOnly = true;
                    window.document.FORM.country.tabIndex = "-1";

                    window.document.FORM.btnproceed.style.visibility = 'hidden';
                    document.getElementById('generateQrCode').style.display = 'block';
                    
                    qrCodeGenerate("0", document.getElementById('username').value, document.getElementById('accountnumber').value, document.getElementById('city').value, document.getElementById('country').value );
                    
                } else {
                    window.location.replace(getBaseURL() + "/qrcodegenerate.jsp");
                }
            }
            
            function cancel()
            {
                window.document.FORM.btnproceed.style.visibility = 'visible';
                document.getElementById('generateQrCode').style.display = 'none';
                
                document.getElementById('success-message-noclose').style.display = 'none';
                
                window.document.FORM.username.className = "mandatory";
                window.document.FORM.username.readOnly = false;
                window.document.FORM.username.tabIndex = "-1";
                window.document.FORM.username.value = "";

                window.document.FORM.accountnumber.className = "mandatory";
                window.document.FORM.accountnumber.readOnly = false;
                window.document.FORM.accountnumber.tabIndex = "";
                window.document.FORM.accountnumber.value = "";

                window.document.FORM.amount.readOnly = false;
                window.document.FORM.amount.tabIndex = "";
                window.document.FORM.amount.value = "";

                window.document.FORM.tac.className = "";
                if ('<%=authOption%>' === 'Y') {
                    window.document.FORM.btnOTPLogin.disabled = false;
                    window.document.FORM.btnSMSLogin.disabled = false;
                }
                log = document.getElementById('consolelog').value;
                log = log + "<br>" + d.toLocaleString() + " : " + "Status validation stopped";
                document.getElementById('consolelog').value = log;
                document.getElementById('displayconsolelog').innerHTML = log;
                clearInterval(loginStateCheckService);
            }

            function logout()
            {
                document.getElementById('fade').style.display = 'block';
                document.getElementById('notice-message').style.display = 'block';
                document.getElementById('notice-text').innerHTML = "Logout Succesful";

                window.document.FORM.action = baseURL + "/LoginServlet?MODE=LOGOUT";
                window.document.FORM.submit();
            }

        </script>
    </head>
    <form name="FORM" id="FORM" method="post" action="">
        <input type="hidden" id="browserfp" name="browserfp" value="" />
        <body onload="setWINDOW();" style="background-position: center center; background-repeat: no-repeat">
            <div class="w3-sidebar w3-light-grey w3-bar-block" style="width:15%">
            <h3 class="w3-bar-item" style="background: #205fa28c;color: white;FONT-FAMILY: Tahoma;font-size: 18px;margin-top: 0%;height: 51px;">Menu</h3>
            <a href="#" onclick="goToPage('main.jsp');" class="w3-bar-item w3-button">Transaction</a>
            <% if (role != null && role.equals("3")) {%>  
            <a href="#" onclick="goToPage('selfservice.jsp');" class="w3-bar-item w3-button">User Self Service</a>
            <% } else { %>
            <a href="#" onclick="goToPage('selfservice.jsp');" class="w3-bar-item w3-button">User Self Service</a>
            <a href="#" onclick="goToPage('selfregister.jsp');" class="w3-bar-item w3-button">User Registration</a>
            <% } %>
            <a href="#" onclick="goToPage('qrcodegenerate.jsp');" class="w3-bar-item w3-button">QR Code Generation</a>
            <a href="#" onclick="goToPage('qrcodeupload.jsp');" class="w3-bar-item w3-button">Transaction via QR Code</a>
            </div>
            <table cellpadding=0 cellspacing=0 border=0 width="100%">
                <tr><td colspan=2>
                        <table cellpadding=0 cellspacing=0 border=0 width="100%">
                            <tr>
                                <td class=login-body width="40%" align=center colspan="3">Enterprise Solution for Global Banking</td>
                            </tr>
                            <tr>
                                <td class=login-body width="40%" align=left colspan="2"></td>
                                <td class=login-caption width="40%" align=right>
                                    <INPUT onclick="logout();" name="btnLogout" type=submit value="Logout" class=imgLogout >
                                </td>
                            </tr>
                        </table>

                        <TABLE cellSpacing=0 cellPadding=1 width="100" align=center border=0 bordercolor="#FFFFFF" bordercolorlight="#eeeeee" bordercolordark="#ffffff">

                            <TR>   
                                <TD align=center>
                                    <img src="images/logo.png" border=0 height="55" width="220">
                                </TD>

                            </TR>
                        </TABLE>

                    </td></tr>
            </table>    
            <!-- add --> 
            <center>
                <fieldset width="70%" class="fieldsettitle">
                    <br>
                    <TABLE BORDER="0" WIDTH="70%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER">
                        <TR>
                            <TH colSpan=5>QR CODE GENERATOR</TH>
                        </TR>    
                        <TR>
                            <TD class="caption" width="20%">Username</TD>
                            <TD width="30%"><input type="text" name="username" id="username" value="<%=userid%>" class="mandatory"></TD>
                            <TD colspan=3 ></TD>  
                        </TR>

                        <TR>
                            <TD class="caption" width="20%">Account Number</TD>
                            <TD width="30%"><input type="text" name="accountnumber" id="accountnumber" value="100796123456SA" class="mandatory"></TD>
                            <TD colspan=3 ></TD>
                        </TR>

                        <TR>
                            <TD class="caption" width="20%">City</TD>
                            <TD width="30%"><input type="text" name="city" id="city" value="KL" class="mandatory"></TD>
                            <TD colspan=3 ></TD> 
                        </TR>
                        
                        <TR>
                            <TD class="caption" width="20%">Country Code:</TD>
                            <TD width="30%"><input type="text" name="country" id="country" value="MY" class="mandatory"></TD>
                            <TD colspan=3 ></TD> 
                        </TR>
                        
                        <TR rowspan="2">
                            <td colspan="5">&nbsp;</td> 
                        </TR> 
                        <input type="hidden" name="desc" id="desc" value="">    
                    </TABLE>

                    <TABLE BORDER="0" WIDTH="70%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER">
                        <TR>
                            <TD width="35%"></TD>
                            <TD width="15%"><INPUT onclick="generate();" name="btnproceed" type=button value="Generate" class=button-login></TD>
                            <TD width="15%"><INPUT onclick="cancel();" name="btnclear" type=button value="Clear" class=button-login></TD>
                            <TD width="35%"></TD>
                        </TR>
                    </TABLE>            

                    <div id="generateQrCode" class="hide-row">
                        <br></br><br/>
                        <TABLE BORDER="0" WIDTH="70%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER">
                            <TR>
                                <TH colspan=6>Generate QR Code</TH>
                            </TR>  
                            <TR>
                                <TD colspan=6 class="caption">Please save the QR code that shown below</TD>
                            </TR>
                            <TR>          
                                <TD  colspan=5 align=center><img id="qrCode" WIDTH=350 HEIGHT=350 src=""/></TD>  
                            </TR>`
                            <TR>          
                                <TD width="15%" align="center"><a id="downloadQr"  href='' download><INPUT name="btndownload" type=button value="Download" class=button-login></a></TD>  
                            </TR>
                        </TABLE>
                        <br/><br/><br/><br/>
                    </div>
                </fieldset>
            </center>
        
          

        <div id="error-message" class="alert-box error" align="center">
            <span align="center">error: </span><text id="error-text"></text>
            <a href = "javascript:void(0)" style="text-align:right; display:block;" onclick = "backtomain();">Close</a>
        </div>  
        <div id="success-message-noclose" class="alert-box success-noclose" align="center">
            <span align="center">success: </span><text id="success-text-noclose"></text>
            <br>
            <img src="images/spinner-small.gif" border=0 height="50" width="50" align="center">
        </div>                  
        <div id="success-message" class="alert-box success" align="center">
            <span align="center">success: </span><text id="success-text"></text>
            <a href = "javascript:void(0)" style="text-align:right; display:block;" onclick = "backtomain();">Close</a>
        </div>  
        <div id="notice-message" class="alert-box notice" align="center"><span align="center">notice: </span><text id="notice-text"></text></div>  
        <div id="fade" class="black_overlay"></div>   
        <INPUT value="<%=userid%>" name="userid" id="userid" type="hidden" > 
        <INPUT value="<%=password%>" name="password" id="password" type="hidden" > 
        <INPUT value="<%=multiStep%>" name="multiStep" id="multiStep" type="hidden" >
        <INPUT value="<%=authToken%>" name="authToken" id="authToken" type="hidden" >
        <INPUT value="<%=loginSession%>" name="loginSession" id="loginSession" type="hidden" >
     
        <INPUT name="plainTextField" id="plainTextField" type="hidden" >
        <INPUT name="reqCode" id="reqCode" type="hidden" > 
        <INPUT name="pageid" id="pageid" type="hidden" value="login"> 
        <a id="showlink" href = "javascript:void(0)" style="text-align:left; display:block;margin-left: 15%" onclick = "document.getElementById('showlink').style.display = 'none';document.getElementById('logcontent').style.display = 'block';">Show</a>
        <div id="logcontent" style="display:none;;margin-left: 15%">
            <a href = "javascript:void(0)" style="text-align:left;margin-left: 15%" onclick = "document.getElementById('logcontent').style.display = 'none';
                    document.getElementById('showlink').style.display = 'block';">Hide</a>    
            <text id="displayconsolelog" name="displayconsolelog"><%=consolelog%></text>
        </div>
        <INPUT name="consolelog" id="consolelog" type="hidden" value="<%=consolelog%>">
        </body>
    </form>

</html>
