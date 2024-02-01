<%-- 
    Document   : main
    Created on : Jul 15, 2015, 3:27:27 PM
    Author     : auyong
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
    String BaseURL = sysProp.getProperty(Config.API_URL);//resource.getString("apiurl");
    String passwordMinLength = sysProp.getProperty(Config.PASSWORD_MIN);//resource.getString("passwordMinLength");
    String passwordComplexity = sysProp.getProperty(Config.PASSWORD_COMPLEX);//resource.getString("passwordComplexity");

    String consolelog = (String) request.getSession().getAttribute("consolelog");
    DateCalculator dateCalculator = new DateCalculator();
    String todayDate = dateCalculator.convertToDate(System.currentTimeMillis(), "yyyy-MM-dd");

    String userid = (String) request.getSession().getAttribute("userid");
    String centagateid = (String) request.getSession().getAttribute("centagateid");
    String email = (String) request.getSession().getAttribute("email");
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta http-equiv="Pragma" content="no-cache" /> 
        <meta http-equiv="Cache-Control" content="private, no-store, no-cache, must-revalidate" /> 

        <title>Main Page</title>
        <style type="text/css"><%@ include file="/styles/login.css" %></style>
        <style type="text/css"><%@ include file="/styles/styles.css" %></style>

        <script language="javascript" src="javascript/JSfunction.js"></script>
        <script language="javascript" src="javascript/JSFormat.js"></script>
        <script language="javascript" src="javascript/JSInvoke.js"></script>
        <script language="javascript" src="javascript/jquery-latest.js"></script>
        <script language="javascript" src="javascript/jquery-ui-1.10.4.js"></script>
        <script type="text/javascript" src="javascript/fingerprint2.min.js"></script>
        <script language="javascript">

            var baseURL = getBaseURL();
            d = new Date();
            var scanStateCheckService;

            var username = "<%=userid%>";
            var userId = "<%=centagateid%>";
            var userEmail = "<%=email%>";

            function registerDevice() {
                document.getElementById('qrDiv').style.display = 'block';
                document.getElementById('oneTimePinInfo').style.display = 'none';
                setConsoleLog("Sending request to register mobile device");
                var url = "./registerServlet?mode=4&userId=" + userId + "&userEmail=" + userEmail;
                $.ajaxSetup({
                    cache: false
                });

                $.ajax({
                    url: url,
                    type: "POST"
                }).done(function (data) {

                    jsonvalue = JSON.parse(data);
                    codeValue = jsonvalue.code;

                    if (codeValue == "0") {
                        //display the QR code

                        if (data.object != "") {
                            document.getElementById("qrDiv").innerHTML = "<img src=\"./qrcode?qrtext=" + encodeURIComponent(jsonvalue.object) + "\" /> <br/> Scan the following QR code using your mobile device";
                            setConsoleLog("Request to register mobile device successful");
                            scanStateCheckService = setInterval(function () {
                                refreshQRState();
                            }, interval);

                        } else {
                            document.getElementById("qrDiv").innerHTML = "Unable generate QR Code. Please try again.";
                            setConsoleLog("Request to register mobile device failed");
                        }
                    } else {

                        msgbox('1', 'Generate QR code request failed');
                        setConsoleLog("Request to register mobile device failed");
                    }
                }).fail(function (jqXHR, textStatus, errorThrown) {
                    // log the error to the console
                    console.error(
                            "The following error occured: " +
                            textStatus, errorThrown
                            );
                });
            }

            function reqOneTimePin() {
                document.getElementById('qrDiv').style.display = 'none';
                document.getElementById('oneTimePinInfo').style.display = 'block';
                setConsoleLog("Sending request to register mobile device");
                var url = "./registerServlet?mode=6&username=" + username;
                $.ajaxSetup({
                    cache: false
                });

                $.ajax({
                    url: url,
                    type: "POST"
                }).done(function (data) {

                    jsonvalue = JSON.parse(data);
                    codeValue = jsonvalue.code;

                    if (codeValue == "0") {
                        //display the QR code
                        objectValue = JSON.parse(jsonvalue.object);

                        if (data.object != "") {
                            document.getElementById("passcode").innerHTML = objectValue.passcode;
                            setConsoleLog("Request Pin to register mobile device successful");
                            scanStateCheckService = setInterval(function () {
                                refreshQRState();
                            }, interval);

                        } else {
                            document.getElementById("passcode").innerHTML = "Unable to generate One Time Pin. Please try again.";
                            setConsoleLog("Request to register mobile device failed");
                        }
                    } else {

                        msgbox('1', 'Generate one time pin request failed');
                        setConsoleLog("Request to register mobile device failed");
                    }
                }).fail(function (jqXHR, textStatus, errorThrown) {
                    // log the error to the console
                    console.error(
                            "The following error occured: " +
                            textStatus, errorThrown
                            );
                });
            }

            function refreshQRState() {

                var url = "./registerServlet?mode=5" + "&userId=" + userId;
                $.ajaxSetup({
                    cache: false
                });

                $.ajax({
                    url: url,
                    type: "POST",
                    timeout: 5000
                }).done(function (data) {
                    jsonvalue = JSON.parse(data);
                    codeValue = jsonvalue.code;

                    if (codeValue == "0") {

                        if (jsonvalue.object != "") {
                            clearInterval(scanStateCheckService);
                            msgbox('2', 'User mobile device register succesfully. Please login again');
                            setConsoleLog("User mobile device register succesfully");
                            setTimeout(function () {
                                logout();
                            }, 3000);
                        }

                    }
                }).fail(function (jqXHR, textStatus, errorThrown) {
                    // log the error to the console
                    console.error(
                            "The following error occured: " +
                            textStatus, errorThrown
                            );
                    clearInterval(scanStateCheckService);
                });
            }

            function logout()
            {
                window.document.FORM.action = baseURL + "/LoginServlet?MODE=LOGOUT";
                window.document.FORM.submit();
            }

            function setWINDOW()
            {
                setConsoleLog("User registration mobile device");
            }


            function setConsoleLog(message) {
                log = document.getElementById('consolelog').value;
                log = log + "<br>" + d.toLocaleString() + " : " + message;
                document.getElementById('consolelog').value = log;
                document.getElementById('displayconsolelog').innerHTML = log;
            }

        </script>
    </head>
    <form name="FORM" id="FORM" method="post" action="">
        <input type="hidden" id="browserfp" name="browserfp" value="" />
        <body onload="setWINDOW();" style="background-position: center center; background-repeat: no-repeat">
            <script type="text/javascript">
                new Fingerprint2().get(function (result, components) {
                    $("#browserfp").val(result);
                });
            </script>
            <table cellpadding=0 cellspacing=0 border=0 width="100%">
                <tr><td colspan=2>
                        <table cellpadding=0 cellspacing=0 border=0 width="100%">

                            <tr>
                                <td class=login-body width="40%" align=left colspan="2">Enterprise Solution for Global Banking</td>
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
            <fieldset width="50%" class="fieldsettitle">
                <br></br>
                <TABLE BORDER="0" WIDTH="70%" CELLSPACING="10" CELLPADDING="3" ALIGN="CENTER" style="margin-left: 16.5%;">
                    <TR>
                        <TH colSpan=3>Register user mobile device</TH>
                    </TR>    
                    <TR>
                        <TD class="caption" width="20%">Username</TD>
                        <TD width="60%" colspan="2"><input type="text" id="deviceUsername" value="<%=userid%>" readonly tabindex="-1" class="input-display"></TD>

                    </TR>

                    <!--    <TR>
                                    <TD colspan="3">
                                    <a href="javascript:void(0);" onclick="registerDevice();"  class="hyperlink" >Request SMS to bind mobile device</a>
                                    </TD>
                        </TR>
                    -->
                    <TR>
                        <TD colspan="3">Please choose one option to bind your mobile :</TD>

                    </TR>
                    <TR>
                        <TD colspan="3">
                            <a href="javascript:void(0);" onclick="registerDevice();"  class="hyperlink" >Request SMS to bind mobile device with QR Code</a>
                        </TD>
                    </TR>
                    <TR>
                        <TD colspan="3">
                            <a href="javascript:void(0);" onclick="reqOneTimePin();"  class="hyperlink" >Request SMS to bind mobile device with User ID</a>
                        </TD>
                    </TR>
                    <TR>
                        <TD>
                        </TD>
                    </TR>
                    <TR>
                        <TD colspan="3" align=center>
                            <div id="qrDiv"></div>
                            <div id="oneTimePinInfo" class="hide-row">
                                <TABLE BORDER="0" WIDTH="70%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER">
                                    <TR>
                                        <TD colspan=5 class="caption" width="20%">Please input the following details to bind your device :</TD>
                                    </TR>
                                    <TR>
                                        <TD colspan=5 class="caption" width="20%">Client ID : <%=userid%></TD>
                                    </TR>
                                    <TR>
                                        <TD colspan=5 class="caption" width="20%">Passcode  : <text id="passcode"></text></TD>
                                    </TR>

                                </TABLE>
                            </div>
                        </TD>
                    </TR>

                </TABLE>

                <TABLE BORDER="0" WIDTH="70%" CELLSPACING="10" CELLPADDING="3" ALIGN="CENTER">
                    <TR>
                        <TD colspan=3 width="70%"></TD>
                        <TD width="50%" style="float:right;"><INPUT onclick="logout();" name="btnclear" type=button value="Cancel" class=button-login></TD>
                    </TR>
                </TABLE>

                <br></br>  
            </fieldset>
        </center>
        <div id="error-message" class="alert-box error" align="center">
            <span align="center">error: </span><text id="error-text"></text>
            <!--    <a href = "javascript:void(0)" style="text-align:right; display:block;" onclick = "backtomain();">Close</a>-->
        </div>  
        <div id="success-message-noclose" class="alert-box success-noclose" align="center">
            <span align="center">success: </span><text id="success-text-noclose"></text>
        </div>                  
        <div id="success-message" class="alert-box success" align="center">
            <span align="center">success: </span><text id="success-text"></text>
            <!--   <a href = "javascript:void(0)" style="text-align:right; display:block;" onclick = "backtoregister();">Close</a>-->
        </div>  
        <div id="notice-message" class="alert-box notice" align="center"><span align="center">notice: </span><text id="notice-text"></text></div>  
        <div id="fade" class="black_overlay"></div>   
        <a id="showlink" href = "javascript:void(0)" style="text-align:left; display:block;" onclick = "document.getElementById('showlink').style.display = 'none';
        document.getElementById('logcontent').style.display = 'block';">Show</a>
        <div id="logcontent" style="display:none;">
            <a href = "javascript:void(0)" style="text-align:left;" onclick = "document.getElementById('logcontent').style.display = 'none';document.getElementById('showlink').style.display = 'block';">Hide</a>    
            <text id="displayconsolelog" name="displayconsolelog"><%=consolelog%></text>
        </div>
        <INPUT name="consolelog" id="consolelog" type="hidden" value="<%=consolelog%>">
        </body>
    </form>

</html>
