<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page language = "java" %>
<%@ page session= "true" %>
<%@ page import = "java.util.*" %>
<%@ page import = "com.google.gson.internal.LinkedTreeMap" %>
<%@ page import = "com.securemetric.web.util.Config"%>
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
    ArrayList<LinkedTreeMap<String, String>> questions = (ArrayList) request.getSession().getAttribute("qnaQues");
    //int quesSize = Integer.parseInt((String)request.getSession().getAttribute("quesSize"));
    String quesSize = (String) request.getSession().getAttribute("quesSize");
    int quesNum = 0;

    String pkiDisplay = "";
    String qnaDisplay = "";
    String crOtpDisplay = "";
    String smsDisplay = "";
    String otpDisplay = "";
    String pkiEnabled = (String) request.getSession().getAttribute("pkiEnabled");
    String qnaEnabled = (String) request.getSession().getAttribute("qnaEnabled");
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
    if (qnaEnabled != null && qnaEnabled.equals("true")) {
        qnaDisplay = "style=\"visibility: hidden\"";
    }

    if (returnCode != null && returnCode.equals("0")) {
        classStyle = "readonly tabindex=-1 class=input-display ";
        userid = (String) request.getSession().getAttribute("userid");
        password = (String) request.getSession().getAttribute("password");
        successMsg = (String) request.getSession().getAttribute("successMsg");
    }

    if (authCode != null && authCode.equals("0")) {
        classStyle = "readonly tabindex=-1 class=input-display ";
        classStyle2 = "readonly tabindex=-1 disabled";
        userid = (String) request.getSession().getAttribute("userid");
        password = (String) request.getSession().getAttribute("password");
        successMsg = (String) request.getSession().getAttribute("successMsg");
        authMode = (String) request.getSession().getAttribute("authMode");
        crOtpChallenge = (String) request.getSession().getAttribute("crOtpChallenge");
    }

    String displayAlert = (String) request.getSession().getAttribute("displayAlert");
    if (displayAlert == null) {
        displayAlert = "N";
    }
    request.getSession().removeAttribute("displayAlert");

    request.getSession().removeAttribute("returnMsg");
%>

<HTML>
    <HEAD>
        <TITLE>User Log-In</TITLE>
        <style type="text/css"><%@ include file="/styles/login.css" %></style>
        <style type="text/css"><%@ include file="/styles/styles.css" %></style>
        <script language="javascript" src="javascript/JSfunction.js"></script>
        <script language="javascript" src="javascript/JSFormat.js"></script>
<!--        <script language="javascript" src="javascript/jquery-latest.js"></script>-->
        <script language="javascript" src="javascript/jquery-3.5.1.min.js"></script>
        <script language="javascript" src="javascript/jquery-ui-1.10.4.js"></script>
        <script type="text/javascript" src="javascript/fingerprint2.min.js"></script>
        <script language="javascript">

            var baseURL = getBaseURL();
            d = new Date();
            function getBaseURL() {

                var url = location.href;  // entire url including querystring - also: window.location.href;
                var baseURL = url.substring(0, url.indexOf('/', 14));


                if (baseURL.indexOf('http://localhost') !== -1) {
                    // Base Url for localhost
                    var url = location.href;  // window.location.href;
                    var pathname = location.pathname;  // window.location.pathname;
                    var index1 = url.indexOf(pathname);
                    var index2 = url.indexOf("/", index1 + 1);
                    var baseLocalUrl = url.substr(0, index2);

                    return baseLocalUrl;
                } else {
                    // Root Url for domain name
                    return baseURL + "/j2ee";
                }
            }

            function setWINDOW()
            {

                if ('<%=loginSession%>' === '1') {//User has already login.
                    document.getElementById('notice-message').style.display = 'block';
                    document.getElementById('notice-text').innerHTML = "Already Login";
                    setTimeout(function () {
                        window.location = (baseURL + "/main.jsp");
                    }, 1000);
                } else {//User has not login yet 
                    if ('<%=authCode%>' === '0')
                    {
                        // log = document.getElementById('consolelog').value;
                        // log = log + "<br>" + d.toLocaleString()+" : "+ "Request SMS code successful";
                        // log = log + "<br>" + d.toLocaleString()+" : "+ "OTP Challenge = <%=crOtpChallenge%>";
                        //  document.getElementById('consolelog').value =log;
                        //  document.getElementById('displayconsolelog').innerHTML =log;
                        if ('<%=authMode%>' === '5') {
                            document.getElementById("requestTokenTitleLogin").innerHTML = "CR OTP :"
                            document.getElementById('input0').style.display = 'table-row';
                            document.getElementById('input1').style.display = 'table-row';

                        } else if ('<%=authMode%>' === '2') {
                            document.getElementById("requestTokenTitleLogin").innerHTML = "SMS OTP :"
                            document.getElementById('input1').style.display = 'table-row';
                        } else if ('<%=authMode%>' === '3') {
                            document.getElementById('input1').style.display = 'table-row';
                            window.document.FORM.otp.className = "mandatory";

                        } else if ('<%=authMode%>' === '7') {

                            document.getElementById('qnalist').style.display = 'table';
                        }

                    }



                    if ('<%=displayAlert%>' === "Y") {
                        log = document.getElementById('consolelog').value;
                        log = log + "<br>" + d.toLocaleString() + " : " + "Multi-step verification is require";
                        log = log + "<br>" + d.toLocaleString() + " : " + "Username = <%=userid%>";
                        log = log + "<br>" + d.toLocaleString() + " : " + "Multi-step flag = <%=multiStep%>";
                        log = log + "<br>" + d.toLocaleString() + " : " + "Adaptive Score = <%=adaptiveScore%>";
                        log = log + "<br>" + d.toLocaleString() + " : " + "Available auth method = <%=authMethods%>";
                        log = log + "<br>" + d.toLocaleString() + " : " + "Authorize Token = <%=authToken%>";
                        document.getElementById('consolelog').value = log;
                        document.getElementById('displayconsolelog').innerHTML = log;

                        //        document.getElementById('fade').style.display='block';
                        //        document.getElementById('notice-message').style.display='block'; 
                        //        document.getElementById('notice-text').innerHTML ="Adaptive Score : <%=adaptiveScore%> , Multi-step verification is require";
                        //
                        //        setTimeout(function(){ 
                        //            document.getElementById('notice-message').style.display='none';
                        //            document.getElementById('fade').style.display='none';
                        //            document.getElementById('table_content').style.display='block';
                        //        }, 10000); 
                    } else {

                        if ('<%=multiStep%>' === "null") {
                            document.getElementById('table_content').style.display = 'none';
                            document.getElementById('fade').style.display = 'block';
                            document.getElementById('notice-message').style.display = 'block';
                            document.getElementById('notice-text').innerHTML = "Require to login";
                            setTimeout(function () {
                                window.location = (baseURL + "/login.jsp");
                            }, 2000);
                        } else {
                            document.getElementById('table_content').style.display = 'block';
                        }
                    }
                }
            }

            function displayContent()
            {
                document.getElementById('notice-message').style.display = 'none';
                document.getElementById('fade').style.display = 'none';
                document.getElementById('table_content').style.display = 'block';
            }

            function login(mode)
            {
                window.document.FORM.action = baseURL + "/LoginServlet?MODE=" + mode;
                window.document.FORM.submit();
            }

            function authReq(req)
            {
                if (CheckMandatory(window.document.FORM))
                {
                    window.document.FORM.btnOTPLogin.disabled = true;


                    if (req === '3') {
                        document.getElementById("requestTokenTitleLogin").innerHTML = "OTP Token Code:"
                        document.getElementById("LoginAuthMode").value = '3';
                        document.getElementById('input1').style.display = 'table-row';
                        window.document.FORM.otp.className = "mandatory";
                    }
                    if (req == '2') {
                        document.getElementById("requestTokenTitleLogin").innerHTML = "SMS OTP :"
                        window.document.FORM.action = baseURL + "/LoginServlet?MULTISTEP=1&REQ=" + req;
                        document.getElementById('input1').style.display = 'table-row';
                        window.document.FORM.otp.className = "mandatory";
                        window.document.FORM.submit();
                    }
                    if (req == '5') {
                        document.getElementById("requestTokenTitleLogin").innerHTML = "Cr OTP :"
                        document.getElementById("LoginAuthMode").value = '5';
                        window.document.FORM.action = baseURL + "/LoginServlet?MULTISTEP=1&REQ=" + req;
                        document.getElementById('input1').style.display = 'table-row';
                        window.document.FORM.otp.className = "mandatory";
                        window.document.FORM.submit();
                    }
                    if (req == '7') {
                        window.document.FORM.btnSMSLogin.disabled = true;
                        //    document.getElementById('input1').style.display='table-row';
                        //    window.document.FORM.otp.className="mandatory";
                        //    window.document.FORM.otp.focus();
                        window.document.FORM.action = baseURL + "/LoginServlet?MULTISTEP=1&REQ=" + req;
                        window.document.FORM.submit();
                    }
                } else
                {
                    return;
                }
            }

            function authLogin()
            {
                var mode = document.getElementById("LoginAuthMode").value;
                if (CheckMandatory(window.document.FORM))
                {
                    window.document.FORM.btnAuthLogin.disabled = true;
                    window.document.FORM.action = baseURL + "/LoginServlet?MULTISTEP=1&MODE=" + mode;
                    window.document.FORM.submit();
                } else
                {
                    return;
                }
            }

            function pkiLogin()
            {
                var mode = '6';

                var secureLoginUrl = baseURL + "/PKILoginServlet?MODE=" + mode;
                if (CheckMandatory(window.document.FORM))
                {
                    window.document.FORM.btnAuthLogin.disabled = true;
                    //	window.document.FORM.action = baseURL+"/LoginServlet?MODE="+mode;
                    secureLoginUrl = secureLoginUrl + "&userid=" + '<%=userid%>';
                    document.getElementById('FORM').method = "get";
                    if (document.getElementById("browserfp")) {
                        secureLoginUrl = secureLoginUrl + "&browserfp=" + document.getElementById("browserfp").value;
                    }
                    //        document.location = baseURL + "/PKILoginServlet?MODE="+mode+"&userid=test2a";
                    document.location = secureLoginUrl;
                    //	window.document.FORM.submit();	 	
                }
            }

        </script>

    </HEAD>
    <FORM id="FORM" name="FORM" method="post" action="multiStep.jsp">
        <input type="hidden" id="browserfp" name="browserfp" value="" />
        <BODY onload="setWINDOW();
        document.FORM.userid.focus()" style="background-position: center center; background-repeat: no-repeat">
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
                        </table>
                    </td></tr>
                <tr>
                    <!--<td width=280>-->

                    <td colspan=2>

                        <TABLE cellSpacing=0 cellPadding=1 width="100" align=center border=0 bordercolor="#FFFFFF" bordercolorlight="#eeeeee" bordercolordark="#ffffff">
                            <TBODY>
                                <TR>   
                                    <TD align=center>
                                        <img src="images/logo.png" border=0 height="60" width="280">
                                    </TD>

                                </TR>
                        </TABLE>
                    </td>
                </tr>

                <tr>

                    <td colspan=2 height=150>
                        <div id="table_content" >
                            <TABLE cellSpacing=1 cellPadding=2 width="250"  cellSpacing=1 cellPadding=2 width="320" align=center border=0 bordercolor="#FFFFFF" bordercolorlight="#eeeeee" bordercolordark="#ffffff">
                                <TBODY> 
                                    <TR>    
                                        <TD>
                                            <TABLE cellSpacing=1 cellPadding=1 width="100%" border=0>
                                                <TBODY>
                                                    <TR>
                                                        <TH colSpan=2>Multi-step Verification</TH></TR>
                                                    <TR>

                                                <INPUT value="<%=userid%>" name="userid" id="userid" type="hidden"> </TD></TR>
                                                <INPUT value="<%=password%>" name="password" id="password" type="hidden" >
                                                <INPUT value="<%=multiStep%>" name="multiStep" id="multiStep" type="hidden" >
                                                <INPUT value="<%=authToken%>" name="authToken" id="authToken" type="hidden" >
                                                </TBODY>
                                            </TABLE>

                                            <TABLE cellSpacing=1 cellPadding=1 width="100%" border=0>

                                                <TBODY> 
                                                    <TR>
                                                        <TD colspan=4 class=caption >Multi-step verification requires you to verify your identity using one of the following authentication methods.</TD>
                                                    </TR>
                                                    <TR>
                                                        <TD colspan=2 width="50%"></TD>
                                                        <TD colspan=2 width="30%"><INPUT type=button value="Cancel" class=button-login onclick="login('CLEAR');"></TD>
                                                        <TD width="1%"></TD>
                                                    </TR>
                                                    <TR align="center">


                                                        <TD width="10%"></TD>
                                                        <TD colspan="2">
                                                            <INPUT onclick="authReq('2');" name="btnSMSLogin" type="button" value="" class=imgSMS <%=classStyle2%> <%=smsDisplay%> >
                                                        <TD><INPUT onclick="authReq('3');" name="btnOTPLogin" type="button" value="" class=imgOtp <%=classStyle2%> <%=otpDisplay%> ></TD>
                                                        <TD><INPUT onclick="authReq('5');" name="btnOTPLogin" type="button" value="" class=imgCROtp <%=classStyle2%> <%=crOtpDisplay%> ></TD>
                                                        <TD><INPUT onclick="return pkiLogin();" name="btnPKILogin" type="button" value="" class=imgPKI <%=classStyle2%> <%=pkiDisplay%> ></TD>
                                                        <TD><INPUT onclick="authReq('7');" name="btnQnaLogin" type="button" value="" class=imgQna <%=classStyle2%> <%=qnaDisplay%> ></TD>

                                                        </TD>
                                                        <TD width="10%"></TD>
                                                    </TR>
                                            </TABLE>
                                            <TABLE> 
                                                <TBODY>
                                                    <TR id="input0" class="hide-row">
                                                        <TD colspan=2 class=caption width="40%"><div id="requestCRChallenge">Challenge :</div></TD>
                                                        <TD  width="30%" 
                                                             value=<%=crOtpChallenge%> name="challenge" id="challenge"><%=crOtpChallenge%>
                                                        </TD>
                                                        <!--          <TD width="20%">
                                                                      <INPUT onclick="requestCrChallenge();" name="btnReqChallenge" type=button value="Request Challenge" 
                                                                             class=button-login>
                                                                  </TD> -->
                                                    </TR>

                                                    <TR id="input1" class="hide-row">
                                                        <TD colspan=2 class=caption width="50%"><div id="requestTokenTitleLogin"></DIV></TD>
                                                        <TD  width="30%">
                                                            <INPUT onclick=this.select() 
                                                                   value="" name="otp" id="otp" autocomplete="off" >
                                                        </TD>
                                                        <TD width="20%">
                                                            <INPUT onclick="authLogin();" name="btnAuthLogin" type=submit value="Submit" 
                                                                   class=button-login>
                                                            <input type="hidden" id="LoginAuthMode" value="<%=authMode%>"/>
                                                        </TD>
                                                    </TR>

                                                </TBODY>      
                                            </TABLE>   

                                            <%
                                                if (authMode.equals("7")) {
                                            %>
                                            <TABLE id="qnalist" class ="hide-row" cellSpacing=1 cellPadding=1 width="100%" border=0>  
                                                <TBODY>
                                                    <TR>
                                                        <TD class=caption width="40%" style="bold">Please fill in the following questions:</TD>
                                                    </TR>
                                                    <TR>
                                                        &nbsp; 

                                                    </TR> 
                                                    <% for (LinkedTreeMap<String, String> question : questions) {%>

                                                    <TR>
                                                <input type="hidden" id="<%= quesNum + "id"%>" name="<%= quesNum + "id"%>" value="<%= question.get("id")%>"/>
                                                <TD class=caption width="40%"><%= question.get("question")%></TD>
                                    </TR>
                                    <TR>
                                        <TD>
                                            <INPUT onclick=this.select() value="" name="<%= quesNum + "ans"%>" id="<%= quesNum + "ans"%>" autocomplete="off">
                                        </TD>
                                    </TR>
                                    <%
                                            quesNum++;
                                        }
                                    %>
                                    <TR>
                                        <TD width="60%"/>
                                        <TD width="20%">
                                            <INPUT onclick="authLogin();" name="btnAuthLogin" type=button value="Submit" 
                                                   class=button-login>
                                            <input type="hidden" id="LoginAuthMode" value="<%=authMode%>"/>
                                        </TD>
                                    </TR>

                                </TBODY>

                            </TABLE>

                            <%
                                }
                            %>
                    </TD></TR>
                </TBODY></TABLE>
            </div>                       
            </td>
            <!--<td width=100% height=450></td>-->
            </tr>
        <tr><td colspan=2 height=80 valign=bottom>
                <table cellpadding=0 cellspacing=0 border=0 width="100%">
                    <tr><td class="" width="50%" align=right valign=bottom height=10>
                            <%
                                if (errorMsg != null && !errorMsg.isEmpty()) {
                            %>   
                    <center><font color="red"> Login Error :</font></center>
                    <center><font size=2 color="silver"><%=errorMsg%></font></center>
                        <%
                                request.getSession().removeAttribute("returnMsg");
                            }
                        %>	      		        	
            </td></tr>

    </table>
</td></tr>
</TABLE>
<div id="error-message" class="alert-box error" align="center">
    <span align="center">error: </span><text id="error-text"></text>
</div>
<div id="notice-message" class="alert-box notice">
    <span align="center">notice: </span><text id="notice-text"></text>
    <br>&nbsp;</br>
    <a href = "javascript:void(0)" 
       style="text-align:right; display:block;"
       onclick = "displayContent();">
        Close
    </a>
</div>

<div id="fade" class="black_overlay"></div>                
<INPUT name="pageid" id="pageid" type="hidden" value="multistep"> 
<INPUT name="pageid" id="pageid" type="hidden" value="login"> 
<a id="showlink" href = "javascript:void(0)" style="text-align:left; display:block;" onclick = "document.getElementById('showlink').style.display = 'none';document.getElementById('logcontent').style.display = 'block';">Show</a>
<div id="logcontent" style="display:none;">
    <a href = "javascript:void(0)" style="text-align:left;" onclick = "document.getElementById('logcontent').style.display = 'none';
        document.getElementById('showlink').style.display = 'block';">Hide</a>    
    <text id="displayconsolelog" name="displayconsolelog"><%=consolelog%></text>
</div>
<INPUT name="consolelog" id="consolelog" type="hidden" value="<%=consolelog%>">
</FORM>	
</BODY>
<HTML>
