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
        <script language="javascript" src="javascript/jquery-3.5.1.min.js"></script>
        <script language="javascript" src="javascript/jquery-ui-1.10.4.js"></script>
        <script type="text/javascript" src="javascript/fingerprint2.min.js"></script>
        <script language="javascript" src="javascript/centagate-agent.js"></script>
        <script type="text/javascript" src="javascript/json2.js"></script>            
        <script type="text/javascript" src="javascript/base64url-arraybuffer.js"></script>
        <script type="text/javascript" src="javascript/fido.js"></script>
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
                document.getElementById('uploadQr').style.display = 'block';
                document.getElementById('accountTransfer').style.display = 'none';
                
                document.getElementById('details').style.display = 'none';
                document.getElementById('showdetails').style.display = 'none';
                document.getElementById('hidedetails').style.display = 'none';
            }

            function upload()
            {
               const reader = new FileReader();

               var imagefile = document.querySelector('input[type=file]').files[0];
               
                reader.addEventListener("load", function () {
                    reader.result;
                    var data = { image: reader.result};
                    qrCodeAnalyse("1", data);
                }, false);
        
                if(imagefile){
                    reader.readAsDataURL(imagefile);
                } 
                
                document.getElementById('uploadQr').style.display = 'none';
                document.getElementById('accountTransfer').style.display = 'block';
                
                document.getElementById('showdetails').style.display = 'block';
            }
            
             function proceed()
            {
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
                    
                    document.getElementById('showdetails').style.display = 'none';
                    document.getElementById('details').style.display = 'none';
                    mobilePush();
                } else {
                    window.location.replace(getBaseURL() + "/qrcodeupload.jsp");
                }
            }
            
            function mobilePush(){
                amt = window.document.FORM.amount.value;
                fromacct = window.document.FORM.fromaccount.value;
                toacc = window.document.FORM.toaccount.value;
                effdate = window.document.FORM.effdate.value;
                recmail = window.document.FORM.recemail.value;
                desc = window.document.FORM.desc.value;
                
                qrCodeTrans('8', "<%=userid%>", amt, fromacct, toacc, effdate, recmail, desc);
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
                if (amt <= 0) { //type A
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
                } else if (req === '6' || req === '8' || req === '9') {
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
                    
                    if(req === '6') {
                    
                       var plainText = WebSafeBase64.encode ( amt + fromacct + toacc + effdate + recmail + desc, true) ;
                       
                       document.getElementById ( "plainTextField" ).value = plainText ;
                       //p7SignByP11(false, plainText, 0, onSignCallback) ;
                       p7SignByCsp(false, plainText, 0, onSignCallback);

                    } else {
                        getInvoke(req, "<%=userid%>", amt, fromacct, toacc, effdate, recmail, desc);
                    }
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
            
            function showDetails(){
                document.getElementById('details').style.display = 'block';
                document.getElementById('showdetails').style.display = 'none';
                document.getElementById('hidedetails').style.display = 'block';
            }
            
             function hideDetails(){
                document.getElementById('details').style.display = 'none';
                document.getElementById('showdetails').style.display = 'block';
                document.getElementById('hidedetails').style.display = 'none';
            }
            
            
            var WebSafeBase64={

                                _keyStr:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_=",

                                encode:function(e, isPaddingOmitted)
                                {
                                        var t="";
                                        var n,r,i,s,o,u,a;
                                        var f=0;
                                        e=this._utf8_encode(e);

                                        while( f < e.length )
                                        {
                                                n=e.charCodeAt(f++);
                                                r=e.charCodeAt(f++);
                                                i=e.charCodeAt(f++);
                                                s=n>>2;
                                                o=(n&3)<<4|r>>4;
                                                u=(r&15)<<2|i>>6;
                                                a=i&63;
                                                if(isNaN(r))
                                                {
                                                        u=a=64;
                                                }
                                                else if(isNaN(i))
                                                {
                                                        a=64;
                                                }
                                                t=t+this._keyStr.charAt(s)+this._keyStr.charAt(o)+this._keyStr.charAt(u)+this._keyStr.charAt(a);
                                        }
                                        
                                        if(isPaddingOmitted === true)
                                        {
                                            t = t.replace(/=/g,"");
                                        }
                                        return t;
                                },

                                decode:function(e)
                                {
                                        var t="";
                                        var n,r,i;
                                        var s,o,u,a;
                                        var f=0;
                                        e=e.replace(/[^A-Za-z0-9\-\_=]/g,"");
                                        
                                        if(e.length %4 != 0 )
                                        {
                                            e = e + "====".substring(0 , (e.length %4 ));
                                        }
                                        
                                        while(f<e.length)
                                        {
                                                s=this._keyStr.indexOf(e.charAt(f++));
                                                o=this._keyStr.indexOf(e.charAt(f++));
                                                u=this._keyStr.indexOf(e.charAt(f++));
                                                a=this._keyStr.indexOf(e.charAt(f++));
                                                n=s<<2|o>>4;r=(o&15)<<4|u>>2;i=(u&3)<<6|a;
                                                t=t+String.fromCharCode(n);
                                                if(u!=64)
                                                {
                                                        t=t+String.fromCharCode(r);
                                                }
                                                if(a!=64)
                                                {
                                                        t=t+String.fromCharCode(i);
                                                }
                                        }

                                        t=this._utf8_decode(t);
                                        return t;
                                },

                                _utf8_encode:function(e)
                                {
                                        e=e.replace(/rn/g,"n");
                                        var t="";
                                        for(var n=0;n<e.length;n++)
                                        {
                                                var r=e.charCodeAt(n);
                                                if(r<128)
                                                {
                                                        t+=String.fromCharCode(r);
                                                }
                                                else if(r>127&&r<2048)
                                                {
                                                        t+=String.fromCharCode(r>>6|192);
                                                        t+=String.fromCharCode(r&63|128);
                                                }
                                                else
                                                {
                                                        t+=String.fromCharCode(r>>12|224);
                                                        t+=String.fromCharCode(r>>6&63|128);
                                                        t+=String.fromCharCode(r&63|128);
                                                }
                                        }
                                        return t;
                                },

                                _utf8_decode:function(e)
                                {
                                        var t="";
                                        var n=0;
                                        var r=c1=c2=0;
                                        while(n<e.length)
                                        {
                                                r=e.charCodeAt(n);
                                                if(r<128)
                                                {
                                                        t+=String.fromCharCode(r);
                                                        n++
                                                }
                                                else if(r>191&&r<224)
                                                {
                                                        c2=e.charCodeAt(n+1);
                                                        t+=String.fromCharCode((r&31)<<6|c2&63);
                                                        n+=2;
                                                }
                                                else
                                                {
                                                        c2=e.charCodeAt(n+1);
                                                        c3=e.charCodeAt(n+2);
                                                        t+=String.fromCharCode((r&15)<<12|(c2&63)<<6|c3&63);
                                                        n+=3;
                                                }
                                        }
                                        return t;
                                }
                        };
                        
                        function onSignCallback(status, signature)
                        {
                            console.log(signature);
                            var plainText = document.getElementById ( "plainTextField" ).value ;
                            getInvokeTAC("6", "<%=userid%>", plainText, signature);
                        }
                        
                        function signP7 ( plainText )
			{
				var obj = document.getElementById ( "bpi" ) ;
				var ret = obj.Pkcs7Sign ( 0 , "" , 2 , 0 , plainText , "" ) ;

				return ret ;
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
                    <div id="uploadQr" class="" >
                        <TABLE BORDER="0" WIDTH="70%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER">
                            <TR>
                                <TH colSpan=5>QR Code Upload</TH>
                            </TR>    

                            <input type="hidden" name="desc" id="desc" value="">    
                        </TABLE>
                    
                        <TABLE BORDER="0" WIDTH="70%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER">
                            <TR>
                                <TD width="15%"><INPUT id="uploadFile" type="file" name="file" size="50" accept="image/*"/></TD>
                            </TR>
                            <TR>
                                <TD width="35%"></TD>
                                <TD width="15%"><INPUT onclick="upload();" name="btnupload" type=button value="Upload" class=button-login></TD>
                                <TD width="35%"></TD>
                            </TR>
                        </TABLE>
                    </div>
                    
                    <div id="accountTransfer" class="hide-row">
                        <TABLE BORDER="0" WIDTH="70%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER">
                            <TR>
                                <TH colSpan=5>Account Transfer</TH>
                            </TR>
                            <TR>
                                <TD class="caption" width="20%">From Account</TD>
                                <TD width="30%"><input type="text" name="fromaccount" id="fromaccount" value="11402236836SA" readonly tabindex="-1" class="input-display"></TD>
                                <TD width="5%"></TD>
                                <TD class="caption" width="20%">To Account</TD>
                                <TD width="30%"><input type="text" name="toaccount" id="toaccount" value="" readonly tabindex="-1" class="input-display"></TD> 
                            </TR>
                            <TR>
                                <TD class="caption" width="20%">From User</TD>
                                <TD width="30%"><input type="text" name="fromuseraccount" id="fromuseraccount" value="" readonly tabindex="-1" class="input-display"></TD>
                                <TD width="5%"></TD>
                                <TD class="caption" width="20%">To User</TD>
                                <TD width="30%"><input type="text" name="touseraccount" id="touseraccount" value="" readonly tabindex="-1" class="input-display"></TD> 
                            </TR>
                            
                            <TR>
                                <TD class="caption" width="20%">Effective Date</TD>
                                <TD width="30%"><input type="text" name="effdate" id="effdate" value="<%=todayDate%>" class="mandatory" onChange="formatDate(this.value);" placeholder="yyyy-mm-dd"></TD>
                                <TD width="5%"></TD>
                                <TD class="caption" width="20%"></TD>
                                <TD width="30%"><input type="hidden" name="recemail" id="recemail" value="" ></TD> 
                            </TR>
                            <TR>
                                <TD class="caption" width="20%">Amount</TD>
                                <TD width="30%"><input type="text" name="amount" id="amount" value="" class="mandatory"></TD>
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
                                <TD width="15%"><INPUT onclick="proceed();" name="btnproceed" type=button value="Proceed" class=button-login></TD>
                                <TD width="15%"><INPUT onclick="cancel();" name="btnclear" type=button value="Clear" class=button-login></TD>
                                <TD width="35%"></TD>
                            </TR>
                        </TABLE>
                    </div>
                                
                    <div id="showdetails" class="hide-row"> 
                        <TABLE BORDER="0" WIDTH="70%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER">
                            <TR>
                                <a onclick="showDetails();" class="text-center" style="float: left;margin-top: 3.5%;color: blue;text-decoration: underline;">Show Details</a>
                            </TR>        
                        </TABLE>
                    </div>
                    <div id="hidedetails" class="hide-row"> 
                        <TABLE BORDER="0" WIDTH="70%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER">
                            <TR>
                                <a onclick="hideDetails();" class="text-center" style="float: left;margin-top: 3.5%;color: blue;text-decoration: underline;">Hide Details</a>
                            </TR>        
                        </TABLE>
                    </div>
                                
                                
                    <div id="details" class="hide-row">
                        <br></br><br/>
                        <TABLE BORDER="0" WIDTH="70%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER">
                            <TR>
                                <TH colSpan=5>More Details</TH>
                            </TR>    

                            <input type="hidden" name="desc" id="desc" value="">    
                        </TABLE>
                    
                        <TABLE BORDER="0" WIDTH="70%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER">
                            
                            <TR>
                                <TD class="caption" width="20%">Payment System:</TD>
                                <TD width="30%"><input type="text" name="paymentsystem" id="paymentsystem" value="" readonly tabindex="-1" class="input-display"></TD>
                                <TD width="5%"></TD>
                                <TD class="caption" width="20%">Merchant Id:</TD>
                                <TD width="30%"><input type="text" name="merchantid" id="merchantid" value="" readonly tabindex="-1" class="input-display"></TD> 
                            </TR>
                            <TR>
                                <TD class="caption" width="20%">Merchant Country:</TD>
                                <TD width="30%"><input type="text" name="merchantcountry" id="merchantcountry" value="" readonly tabindex="-1" class="input-display"></TD>
                                <TD width="5%"></TD>
                                <TD class="caption" width="20%">Merchant City:</TD>
                                <TD width="30%"><input type="text" name="merchantcity" id="merchantcity" value="" readonly tabindex="-1" class="input-display"></TD> 
                            </TR>
                            <TR>
                                <TD class="caption" width="20%">Version:</TD>
                                <TD width="30%"><input type="text" name="version" id="version" value="" readonly tabindex="-1" class="input-display"></TD>
                                <TD width="5%"></TD>
                                <TD class="caption" width="20%">Qr Code:</TD>
                                <TD width="30%"><input type="text" name="qrCode" id="qrCode" value="" readonly tabindex="-1" class="input-display"></TD> 
                            </TR>
                        </TABLE>
                        <br/><br/><br/><br/>
                    </div>

                    <div id="transOption" class="hide-row">
                        <br></br><br/>
                        <TABLE BORDER="0" WIDTH="70%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER">
                            <TR>
                                <TH colspan=6>Transaction Authorization</TH>
                            </TR>  
                            <TR>
                                <TD colspan=6 class="caption">Please select a method below to verify and complete the transaction.</TD>
                            </TR>
                            <TR>          
                                <TD><INPUT onclick="verifyTrans('2');" name="btnSMSLogin" type="button" value="SMS" class=button-login <%=smsDisplay%>></TD>
<!--                                <TD><INPUT onclick="verifyTrans('3');" name="btnOTPLogin" type="button" value="OTP" class=button-login <%=otpDisplay%>></TD>                                                                                             -->
                                <TD><INPUT onclick="verifyTrans('5');" name="btnCrOTPLogin" type="button" value="Sign CROTP" class=button-login <%=crOtpDisplay%>></TD>
                                <TD><INPUT onclick="verifyTrans('8');" name="btnPushCr" type=button value="Mobile Push" class=button-login <%=pushDisplay%>></TD> 
<!--                                <TD><INPUT onclick="verifyTrans('4');" name="btnQRLogin" type=button value="QR Code" class=button-login <%=qrDisplay%>></TD>-->
                                <TD><INPUT onclick="verifyTrans('6');" name="btnPKILogin" type="button" value="PKI" class=button-login <%=pkiDisplay%>></TD>   
                                <TD><INPUT onclick="verifyTrans('9');" name="btnFidoLogin" type="button" value="FIDO" class=button-login <%=fidoDisplay%>></TD>
                            </TR>
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
                                <TD width="15%"> <INPUT onclick="transReq('2');" name="btnSMSLogin" type=submit value="" class=imgSMS></TD>
                                <TD width="15%"><INPUT onclick="transReq('3');" name="btnOTPLogin" type=submit value="" class=imgCROtp ></TD>
                                <TD width="15%"><INPUT onclick="transReq('4');" name="btnQRLogin" type=submit value="" class=imgQRCode ></TD>
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
                            <input type="hidden" id="fidoPublicKeyCredential" name="fidoPublicKeyCredential"/>
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
                <a href = "javascript:void(0)" style="text-align:right; display:block;" onclick = "backtoqrupload();">Close</a>
            </div>  
            <div id="success-message-noclose" class="alert-box success-noclose" align="center">
                <span align="center">success: </span><text id="success-text-noclose"></text>
                <br>
                <img src="images/spinner-small.gif" border=0 height="50" width="50" align="center">
            </div>                  
            <div id="success-message" class="alert-box success" align="center">
                <span align="center">success: </span><text id="success-text"></text>
                <a href = "javascript:void(0)" style="text-align:right; display:block;" onclick = "backtoqrupload();">Close</a>
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
