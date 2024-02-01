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
    String BaseURL = sysProp.getProperty(Config.BASE_URL);
    //ResourceBundle resource = ResourceBundle.getBundle("system");
    // String BaseURL = resource.getString("baseurl");

    String loginSession = (String) request.getSession().getAttribute("loginSession");
    String returnCode = (String) request.getSession().getAttribute("returnCode");
    String authCode = (String) request.getSession().getAttribute("authCode");
    String errorMsg = (String) request.getSession().getAttribute("returnMsg");
    String multiStep = (String) request.getSession().getAttribute("multiStepAuth");
    String authToken = (String) request.getSession().getAttribute("authToken");
    System.out.println("multistepTrans.jsp authToken #######" + authToken);
    String adaptiveScore = (String) request.getSession().getAttribute("adaptiveScore");
    String authMethods = (String) request.getSession().getAttribute("authMethods");
    String consolelog = (String) request.getSession().getAttribute("consolelog");
    String userid = "";
    String password = "";
    String successMsg = "";
    String authMode = "";
    String crOtpChallenge = "";
    String classStyle = "class=mandatory";
    String classStyle2 = "";
    String smsEnabled = (String) request.getSession().getAttribute("smsEnabled");
    String otpEnabled = (String) request.getSession().getAttribute("otpEnabled");
    String crOtpEnabled = (String) request.getSession().getAttribute("crOtpEnabled");
    String displayString = (String) request.getSession().getAttribute("displayString");
    String qrCodeEnabled = (String) request.getSession().getAttribute("qrCodeEnabled");
    String mSoftCertEnabled = (String) request.getSession().getAttribute("mobileSoftCertEnabled");
    String pkiEnabled = (String) request.getSession().getAttribute("pkiEnabled");
    // String qnaEnabled = (String) request.getSession().getAttribute("qnaEnabled");
    String amt = (String) request.getSession().getAttribute("amt");
    String fromacct = (String) request.getSession().getAttribute("fromacct");
    String toacc = (String) request.getSession().getAttribute("toacc");
    String effdate = request.getParameter("effdate");
    String recmail = request.getParameter("recmail");
    String desc = request.getParameter("desc");

    String pkiDisplay = "";
    String crOtpDisplay = "";
    String smsDisplay = "";
    String otpDisplay = "";
    String qrCodeDisplay = "";
    String mSoftCertDisplay = "";

    request.getSession().setAttribute("multiStep", multiStep);
    String authOption = sysProp.getProperty(Config.AUTH_OPTION);//resource.getString("authOption");

    String otpChallengeDisplay = (String) request.getSession().getAttribute("otpChallengeDisplay");

    if (consolelog.length() > 5000) {
        consolelog = "";
    }
    //  String otpChallenge = "";
    DateCalculator dateCalculator = new DateCalculator();
    String todayDate = dateCalculator.convertToDate(System.currentTimeMillis(), "yyyy-MM-dd");
    String otpChallenge = (String) request.getSession().getAttribute("otpChallenge");

    userid = (String) request.getSession().getAttribute("userid");
    password = (String) request.getSession().getAttribute("password");
    classStyle2 = "readonly tabindex=-1 disabled";
    if (otpEnabled != null && otpEnabled.equals("true")) {
        otpDisplay = "style=\"visibility: hidden\"";
    }
    if (crOtpEnabled != null && crOtpEnabled.equals("true")) {
        crOtpDisplay = "style=\"visibility: hidden\"";
    }

    if (smsEnabled != null && smsEnabled.equals("true")) {
        smsDisplay = "style=\"visibility: hidden\"";
    }
    if (pkiEnabled != null && pkiEnabled.equals("true")) {
        pkiDisplay = "style=\"visibility: hidden\"";
    }

    if (qrCodeEnabled != null && qrCodeEnabled.equals("true")) {
        qrCodeDisplay = "style=\"visibility: hidden\"";
    }
    if (mSoftCertEnabled != null && mSoftCertEnabled.equals("true")) {
        mSoftCertDisplay = "style=\"visibility: hidden\"";
    }


%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Multi Step transaction</title>
        <style type="text/css"><%@ include file="/styles/login.css" %></style>
        <style type="text/css"><%@ include file="/styles/styles.css" %></style>

        <script language="javascript" src="javascript/JSfunction.js"></script>
        <script language="javascript" src="javascript/JSFormat.js"></script>
        <script language="javascript" src="javascript/JSInvoke.js"></script>
<!--        <script language="javascript" src="javascript/jquery-latest.js"></script>-->
        <script language="javascript" src="javascript/jquery-3.5.1.min.js"></script>
        <script language="javascript" src="javascript/jquery-ui-1.10.4.js"></script>
        <script type="text/javascript" src="javascript/fingerprint2.min.js"></script>
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


            function cancel()
            {

                window.document.FORM.btnproceed.style.visibility = 'visible';
//        if ('<%=authOption%>'==='Y') {
//            document.getElementById('otpChallenge').style.display='none';
//        }
//        else {
//            document.getElementById('otpChallenge').style.display='none';
//            document.getElementById('qrCodeDisplay').style.display='none';
//            document.getElementById('qr_click_row').style.display='none';
//        }
                document.getElementById('tac').style.display = 'none';
                document.getElementById('transOption').style.display = 'none';
                document.getElementById('input1').style.display = 'none';
                document.getElementById('input0').style.display = 'none';
                document.getElementById('success-message-noclose').style.display = 'none';
                window.document.FORM.amount.className = "mandatory";
                window.document.FORM.amount.readOnly = false;
                window.document.FORM.amount.tabIndex = "-1";
                window.document.FORM.amount.value = "";

                window.document.FORM.toaccount.className = "mandatory";
                window.document.FORM.toaccount.readOnly = false;
                window.document.FORM.toaccount.tabIndex = "";
                window.document.FORM.toaccount.value = "";

                window.document.FORM.effdate.className = "mandatory";
                window.document.FORM.effdate.readOnly = false;
                window.document.FORM.effdate.tabIndex = "";
                window.document.FORM.effdate.value = "";

                window.document.FORM.recemail.className = "";
                window.document.FORM.recemail.readOnly = false;
                window.document.FORM.recemail.tabIndex = "";
                window.document.FORM.recemail.value = "";

                window.document.FORM.desc.className = "";
                window.document.FORM.desc.readOnly = false;
                window.document.FORM.desc.tabIndex = "";
                window.document.FORM.desc.value = "";

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

            function proceed()
            {
                // window.document.FORM.btnQRLogin.style.visibility = 'visible';
                // window.document.FORM.btnPushCr.style.visibility = 'visible';
                if (CheckMandatory(window.document.FORM))
                {
                    window.document.FORM.amount.className = "input-display";
                    window.document.FORM.amount.readOnly = true;
                    window.document.FORM.amount.tabIndex = "-1";

                    window.document.FORM.toaccount.className = "input-display";
                    window.document.FORM.toaccount.readOnly = true;
                    window.document.FORM.toaccount.tabIndex = "-1";

                    window.document.FORM.effdate.className = "input-display";
                    window.document.FORM.effdate.readOnly = true;
                    window.document.FORM.effdate.tabIndex = "-1";

                    window.document.FORM.recemail.className = "input-display";
                    window.document.FORM.recemail.readOnly = true;
                    window.document.FORM.recemail.tabIndex = "-1";

                    window.document.FORM.desc.className = "input-display";
                    window.document.FORM.desc.readOnly = true;
                    window.document.FORM.desc.tabIndex = "-1";

                    window.document.FORM.btnproceed.style.visibility = 'hidden';
                    //window.document.FORM.btnclear.style.visibility = 'hidden';  
                    document.getElementById('transOption').style.display = 'block';
                    displayButtons();
                } else {
                    window.location.replace(getBaseURL() + "/main.jsp");
                }
            }

            /*  
             * Update 23/03/2017
             * State Bank of Viet Nam
             * ===========================    
             * Type A: <=20M VND  --> SMS
             * Type B: <100M VND  --> OTP, CR OTP
             * Type C: <500M VND  --> Push CR, QR Code
             * Type D: >=500M VND --> PKI
             */
            function displayButtons() {
                amt = eval(window.document.FORM.amount.value);
                if (amt <= 20) { //type A
                    //display all
                } else if (amt < 100) { //type B                       
                    window.document.FORM.btnSMSLogin.style.visibility = 'hidden';
                } else if (amt < 500) { //type C
                    window.document.FORM.btnSMSLogin.style.visibility = 'hidden';
                    window.document.FORM.btnOTPLogin.style.visibility = 'hidden';
                    window.document.FORM.btnCrOTPLogin.style.visibility = 'hidden';
                } else { //type D
                    window.document.FORM.btnSMSLogin.style.visibility = 'hidden';
                    window.document.FORM.btnOTPLogin.style.visibility = 'hidden';
                    window.document.FORM.btnCrOTPLogin.style.visibility = 'hidden';
                    window.document.FORM.btnPushCr.style.visibility = 'hidden';
                    window.document.FORM.btnQRLogin.style.visibility = 'hidden';
                }
            }

            function verifyTrans(req) {
                document.getElementById('transOption').style.display = 'none';
                window.document.FORM.btnclear.style.visibility = 'hidden';
                window.document.FORM.btnQRLogin.style.visibility = 'hidden';
                window.document.FORM.btnPushCr.style.visibility = 'hidden';

                if (req === '2' || req === '3' || req === '4' || req === '5') {
                    document.getElementById('tac').style.display = 'block';
                    transReq(req);
                } else if (req === '6' || req === '8') {
                    transReq(req);
                } else {
                    return;
                }

            }

            function transReq(req)
            {
                if (CheckMandatory(window.document.FORM))
                {
                    if ('<%=authOption%>' === 'Y') {
                        window.document.FORM.btnOTPLogin.disabled = true;
                        window.document.FORM.btnSMSLogin.disabled = true;

                        document.getElementById('input1').style.display = 'table-row';
                        window.document.FORM.tac.className = "mandatory";
                    }
                    //window.document.FORM.action = baseURL+"/LoginServlet?REQ="+req;
                    //window.document.FORM.submit();
                    amt = window.document.FORM.amount.value;
                    fromacct = window.document.FORM.fromaccount.value;
                    toacc = window.document.FORM.toaccount.value;
                    effdate = window.document.FORM.effdate.value;
                    recmail = window.document.FORM.recemail.value;
                    desc = window.document.FORM.desc.value;
                    getInvoke(req, "<%=userid%>", amt, fromacct, toacc, effdate, recmail, desc);
                } else
                {
                    return;
                }
            }




            function TACSubmit(mode)
            {
                if (CheckMandatory(window.document.FORM))
                {
                    window.document.FORM.btnAuthLogin.disabled = true;
                    window.document.FORM.tac.className = "input-display";
                    //window.document.FORM.action = baseURL+"/TransServlet?MODE="+mode;
                    //window.document.FORM.submit();	 
                    getInvokeTAC(
                            window.document.FORM.reqCode.value, "<%=userid%>",
                            window.document.FORM.tac.value, window.document.FORM.challenge.value);
                } else
                {
                    return;
                }
            }

            function logout()
            {
                document.getElementById('fade').style.display = 'block';
                document.getElementById('notice-message').style.display = 'block';
                document.getElementById('notice-text').innerHTML = "Logout Succesful";

                window.document.FORM.action = baseURL + "/LoginServlet?MODE=LOGOUT";
                window.document.FORM.submit();
            }

            function showQrOtpInput() {
                document.getElementById('input1').style.display = 'table-row';
                document.getElementById('input0').style.display = 'table-row';
                //         document.getElementById('otpChallengeDisplay').innerHTML ="<%=otpChallenge%>"; 
                window.document.FORM.tac.className = "mandatory";
                clearInterval(loginStateCheckService);
                return;
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
                        <TH colSpan=5>New Account Transfer</TH>
                    </TR>    
                    <TR>
                        <TD class="caption" width="20%">Amount</TD>
                        <TD width="30%"><input type="text" name="amount" id="amount" value="<%=amt%>" class="mandatory"></TD>
                        <TD colspan=3 > (millions VND)</TD>  
                    </TR>
                    <TR>
                        <TD class="caption" width="20%">From Account</TD>
                        <TD width="30%"><input type="text" name="fromaccount" id="fromaccount" value="<%=fromacct%>" readonly tabindex="-1" class="input-display"></TD>
                        <TD width="5%"></TD>
                        <TD class="caption" width="20%">To Account</TD>
                        <TD width="30%"><input type="text" name="toaccount" id="toaccount" value="<%=toacc%>" class="mandatory"></TD> 
                    </TR>

                    <TR>
                        <TD class="caption" width="20%">Effective Date</TD>
                        <TD width="30%"><input type="text" name="effdate" id="effdate" value="<%=todayDate%>" class="mandatory" onChange="formatDate(this.value);" placeholder="yyyy-mm-dd"></TD>
                        <TD width="5%"></TD>
                        <TD class="caption" width="20%"></TD>
                        <TD width="30%"><input type="hidden" name="recemail" id="recemail" value="" ></TD> 
                    </TR>

                    <TR rowspan="2">
                        <td colspan="5">&nbsp;</td> 
                    </TR> 
                    <input type="hidden" name="desc" id="desc" value="">    
                </TABLE>

                <TABLE BORDER="0" WIDTH="70%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER">
                    <TR>
                        <TD width="35%"></TD>
                        <TD width="15%"><INPUT onclick="cancel();" name="btnclear" type=button value="Clear" class=button-login ></TD>
                        <!--   <TD width="15%"><INPUT onclick="proceed();" name="btnproceed" type=button value="Proceed" class=button-login></TD>-->
                        <TD width="35%"></TD>
                    </TR>
                </TABLE>            

                <div id="transOption" >
                    <br></br><br/>
                    <TABLE BORDER="0" WIDTH="70%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER">
                        <TR>
                            <TH colspan=6>Transaction Authorization</TH>
                        </TR>  
                        <TR>
                            <TD colspan=6 class="caption">Multi-step verification requires you to verify your transaction using one of the following authentication methods.</TD>
                        </TR>
                        <TR>          
                            <TD><INPUT onclick="verifyTrans('2');" name="btnSMSLogin" type="button" value="SMS" class=button-login <%=smsDisplay%> ></TD>
                            <TD><INPUT onclick="verifyTrans('3');" name="btnOTPLogin" type="button" value="OTP" class=button-login   <%=otpDisplay%>></TD>                                                                                             
                            <TD><INPUT onclick="verifyTrans('5');" name="btnCrOTPLogin" type="button" value="CR OTP" class=button-login  <%=crOtpDisplay%> ></TD>
                            <TD><INPUT onclick="verifyTrans('8');" name="btnPushCr" type="button" value="Mobile Push" class=button-login  <%=mSoftCertDisplay%>></TD> 
                            <TD><INPUT onclick="verifyTrans('4');" name="btnQRLogin" type="button" value="QR Code" class=button-login  <%=qrCodeDisplay%>></TD>
                            <TD><INPUT onclick="verifyTrans('6');" name="btnPKILogin" type="button" value="PKI" class=button-login <%=pkiDisplay%> ></TD>                          

                        </TR>
                        <INPUT value="<%=multiStep%>" name="multiStep" id="multiStep" type="hidden" >
                        <INPUT value="<%=authToken%>" name="authToken" id="authToken" type="hidden" >
                        
                    </TABLE>
                    <br/><br/><br/><br/>
                </div>

                <div id="tac" class="hide-row">

                    <TABLE BORDER="0" WIDTH="70%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER">
                        <TR>
                            <TH colSpan=5>Transaction Authorization</TH>
                        </TR>  
                        <% if (authOption != null && authOption.equals("Y")) {%>      
                        <TR>
                            <TD colspan=5 class="caption" width="20%">Choose method below to request the TAC number to complete the transaction.</TD>
                        </TR>
                        <TR>
                        <TR align="center">
                            <TD width="35%"></TD>
                            <TD width="15%"> <INPUT onclick="transReq('2');" name="btnSMSLogin" type=submit value="" class=imgSMS <%=classStyle2%> <%=smsDisplay%>></TD>
                            <TD width="15%"><INPUT onclick="transReq('3');" name="btnOTPLogin" type=submit value="" class=imgCROtp <%=classStyle2%> <%=crOtpDisplay%>  ></TD>
                            <TD width="15%"><INPUT onclick="transReq('4');" name="btnQRLogin" type=submit value="" class=imgQRCode <%=classStyle2%> <%=qrCodeDisplay%>  ></TD>
                            <TD width="35%"></TD>
                        </TR>
                        <% } %>
                    </TABLE>  

                    <TABLE BORDER="0" WIDTH="70%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER"> 

                        <% if (authOption != null && authOption.equals("Y")) {%>     
                        <TR id="otpChallenge" class="hide-row">
                            <TD colspan=2 class=caption width="50%">Challenge Code</TD>
                            <TD  colspan=2 width="50%">
                                <INPUT
                                    value="<%=otpChallenge%>" name="challenge" id="challenge" autocomplete="off" readonly tabindex=-1 class=input-display>
                            </TD>
                            <TD width="50%">&nbsp;</TD>
                        </TR>
                        <% } else {%>
                        <!--TAC for QR Authentication -->
                        <INPUT value="<%=otpChallenge%>" name="challenge" id="challenge" type="hidden">
                        <TR id="otpChallenge" class="hide-row">
                            <TD colspan=5 class=caption width="50%">Please scan this QR Code</TD>
                        </TR>
                        <TR id="qrCodeDisplay" class="hide-row">
                            <TD  colspan=5 align=center>
                                <img id="qrCode" WIDTH=350 HEIGHT=350 src="" />
                            </TD>
                        </TR>  

                        <% }%>

                        <!--TAC for SMS, OTP, CROTP Authentication -->
                        <TR id="tac_click_row" class="hide-row">
                            <td colspan="5" alig="center">
                                <a href="#" onclick="return showTACInput();">Click here to enter the TAC manually</a>
                            </td>
                        </TR>
                        <TR id="tac_info_row" class="hide-row">
                            <TD colspan=5 class="caption" width="20%">Please enter the TAC to complete the transaction.</TD>
                        </TR>
                        <TR id="input0" class="hide-row">
                            <TD width="30%"> </TD>    
                            <TD class=caption width="15%"><text id="challengeDisplay"></text></TD>                      
                            <TD colspan=3><text id="otpChallengeDisplay"></text></TD>
                        </TR> 
                        <TR id="input1" class="hide-row">
                            <TD width="30%"> </TD>    
                            <TD class=caption width="15%"><text id="tacDisplay"></text></TD>
                            <TD  width="15%">
                                <INPUT onclick=this.select() value="" name="tac" id="tac" autocomplete="off" >
                            </TD>
                            <TD width="15%">
                                <INPUT onclick="TACSubmit();" name="btnAuthLogin" type=button value="Submit" class=button-login>
                            </TD>
                            <TD width="30%"></TD>   
                        </TR>                                        
                    </TABLE>   

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
        <INPUT name="reqCode" id="reqCode" type="hidden" > 
        <INPUT name="pageid" id="pageid" type="hidden" value="login"> 
        <a id="showlink" href = "javascript:void(0)" style="text-align:left; display:block;" onclick = "document.getElementById('showlink').style.display = 'none';document.getElementById('logcontent').style.display = 'block';">Show</a>
        <div id="logcontent" style="display:none;">
            <a href = "javascript:void(0)" style="text-align:left;" onclick = "document.getElementById('logcontent').style.display = 'none';
                    document.getElementById('showlink').style.display = 'block';">Hide</a>    
            <text id="displayconsolelog" name="displayconsolelog"><%=consolelog%></text>
        </div>
        <INPUT name="consolelog" id="consolelog" type="hidden" value="<%=consolelog%>">
        </body>
    </form>

</html>
