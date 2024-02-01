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
        <link href='https://fonts.googleapis.com/css?family=Montserrat+Alternates' rel='stylesheet'>
        <link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet'>
        <style type="text/css"><%@ include file="/styles/login.css" %></style>
        <style type="text/css"><%@ include file="/styles/styles.css" %></style>
        <style type="text/css"><%@ include file="/styles/w3.css" %></style>
        <script language="javascript" src="javascript/JSfunction.js"></script>
        <script language="javascript" src="javascript/JSFormat.js"></script>
        <script language="javascript" src="javascript/JSInvoke.js"></script>
<!--        <script language="javascript" src="javascript/jquery-latest.js"></script>-->
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
                if (CheckMandatory(window.document.FORM))
                {
                    window.document.FORM.date.className = "input-display";
                    window.document.FORM.date.readOnly = true;
                    window.document.FORM.date.tabIndex = "-1";

                    window.document.FORM.date2.className = "input-display";
                    window.document.FORM.date2.readOnly = true;
                    window.document.FORM.date2.tabIndex = "-1";

                    window.document.FORM.turnover.className = "input-display";
                    window.document.FORM.turnover.readOnly = true;
                    window.document.FORM.turnover.tabIndex = "-1";

                    window.document.FORM.gp.className = "input-display";
                    window.document.FORM.gp.readOnly = true;
                    window.document.FORM.gp.tabIndex = "-1";

                    window.document.FORM.allexp.className = "input-display";
                    window.document.FORM.allexp.readOnly = true;
                    window.document.FORM.allexp.tabIndex = "-1";
                    
                    window.document.FORM.salary.className = "input-display";
                    window.document.FORM.salary.readOnly = true;
                    window.document.FORM.salary.tabIndex = "-1";
                    
                    window.document.FORM.benefit.className = "input-display";
                    window.document.FORM.benefit.readOnly = true;
                    window.document.FORM.benefit.tabIndex = "-1";
                    
                    window.document.FORM.interest.className = "input-display";
                    window.document.FORM.interest.readOnly = true;
                    window.document.FORM.interest.tabIndex = "-1";
                    
                    

                    window.document.FORM.btnproceed.style.visibility = 'hidden';
                    //window.document.FORM.btnclear.style.visibility = 'hidden';  
                    document.getElementById('transOption').style.display = 'block';
                    document.getElementById('fade').style.display = 'block';
                   // displayButtons();
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
                document.getElementById('transOption').style.display = 'none';
//                window.document.FORM.btnclear.style.visibility = 'hidden';
//                window.document.FORM.btnPKILogin.style.visibility = 'hidden';
//                window.document.FORM.btnLogin.style.visibility = 'hidden';

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
                    tax = window.document.FORM.tax.value;
                    totaltax = window.document.FORM.totaltax.value;
                    name = window.document.FORM.name.value;
//                    effdate = window.document.FORM.effdate.value;
//                    recmail = window.document.FORM.recemail.value;
//                    desc = window.document.FORM.desc.value;
                    
                    if(req === '6') {
                    
                       var plainText = WebSafeBase64.encode ( tax + name + totaltax , true) ;
                       
                       document.getElementById ( "plainTextField" ).value = plainText ;
                       //p7SignByP11(false, plainText, 0, onSignCallback) ;
                       p7SignByCsp(false, plainText, 0, onSignCallback);

                    } else {
                        getInvoke(req, "<%=userid%>",  tax, name, totaltax, '', '', '' );
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

            <table cellpadding=0 cellspacing=0 border=0 width="100%">
                <tr><td colspan=2>
                        <table cellpadding=0 cellspacing=0 border=0 width="100%">
                            <tr>
                                <td class=login-body width="40%" align=center colspan="3">
                                    <img src="images/logo.png" border="0" style="width: 170px;float: left;">
                                    <INPUT onclick="logout();" name="btnLogout" type=submit value="Logout" class=imgLogout >
                                </td>
                            </tr>
                            <tr>
                                <td class=login-caption align=left colspan="2" style="width: 100%;"></td>
                                <td align=right style="width: 40%;">
                                    
                                </td>
                            </tr>
                        </table>

                        <TABLE cellSpacing=0 cellPadding=1 width="100" align=center border=0 bordercolor="#FFFFFF" bordercolorlight="#eeeeee" bordercolordark="#ffffff">

                            <TR>   
                                <TD align=center>
                                   
                                </TD>

                            </TR>
                        </TABLE>

                    </td></tr>
            </table>    
            <!-- add --> 
        <center>
            <fieldset class="fieldsettitle">
                <br>
                <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER" style="width: 80%;">
                    <TR>
                        <TH colSpan=5>YEAR OF ASSESSMENT 2020</TH>
                    </TR>    
                    <TR>
                        <TD class="caption" width="20%">Name of Partnership</TD>
                        <td width="35%" colspan="2">
                            <input type="text" name="name" id="name" value="ABCDPARTNERSHIP" readonly="" tabindex="-1" class="input-display" style="width: 60%;">
                            <span class="caption"> Tax Reference No</span>
                        </td>
                        <TD class="caption" width="20%"><input type="text" name="tax" id="tax" value="55204436106" readonly tabindex="-1" class="input-display"></TD>
                        <TD width="30%"></TD> 
                    </TR>
                    
                    <TR>
                        <TD class="caption" colspan="5" ><br></TD>
                    </TR>
                    <tr>
                        <th colspan="5" style="BACKGROUND-COLOR: #b31218;font-size: 14px;">TAX DECLARATION</th>
                    </tr>
                    
                     <TR>
                        <TD class="caption" width="20%">Accounting Period From</TD>
                        <TD width="30%"><input type="text" name="date" id="date" value="2020-01-01" class="mandatory" onChange="formatDate(this.value);" placeholder="yyyy-mm-dd" style="width: 80%;"></TD>
                        <TD width="5%">to</TD>
                        <TD  width="20%"><input type="text" name="date2" id="date2" value="2020-12-31" class="mandatory" onChange="formatDate(this.value);" placeholder="yyyy-mm-dd"></TD>
                        <TD class="small-td"></TD> 
                    </TR>
                    
                    <TR>
                        <TD class="caption" colspan="5" ><br></TD>
                    </TR>
                    
                    <tr>
                        <th colspan="5" style="BACKGROUND-COLOR: TRANSPARENT;font-size: 14px;COLOR: BLACK;FONT-WEIGHT: 1000;">1. TRADE, BUSINESS, PROFESSION OR VOCATION              <span><img src="images/notice.png" border="0" style="width: 14px;"></span></th>
                    </tr>
                    
                    <tr>
                        <td style="width: 35%;"></td>
                        <td width="5%"></td>
                        <td class="caption" width="20%">$</td>
                        <td class="small-td"></td> 
                    </tr>
                    
                    <tr>
                        <td class="caption" width="20%"></td>
                        <td width="30%">(a) Turnover</td>
                        <td width="5%"></td>
                        <td class="caption" width="20%"><input type="text" name="turnover" id="turnover" value="500000" class="mandatory"></td>
                        <td class="small-td">.00</td> 
                    </tr>
                    <tr>
                        <td class="caption" width="20%"></td>
                        <td width="30%">(b) Gross Profit/Loss</td>
                        <td width="5%"></td>
                        <td class="caption" width="20%"><input type="text" name="gp" id="gp" value="450000" class="mandatory"></td>
                        <td class="small-td">.00</td> 
                    </tr>
                    <tr>
                        <td class="caption" width="20%"></td>
                        <td width="30%">(c) Allowable Business Expenses</td>
                        <td width="5%"></td>
                        <td class="caption" width="20%"><input type="text" name="allexp" id="allexp" value="100000" class="mandatory"></td>
                        <td class="small-td">.00</td> 
                    </tr>
                    
                    <tr>
                        <td class="caption" width="20%"></td>
                        <td width="30%">[Exclude Partner's Salary, Bonus, CPF & Other Benefits, etc]</td>
                        <td width="5%"></td>
                        <td class="caption" width="20%"></td>
                        <td class="small-td"></td> 
                    </tr>
                    
                      <tr>
                        <td class="caption" width="20%"></td>
                        <td width="30%">(d) Adjusted Profit/Loss [d] = [b] - [c]</td>
                        <td width="5%"></td>
                        <td class="caption" width="20%">350,000</td>
                        <td class="small-td">.00</td> 
                    </tr>
                    
                     <tr>
                        <td  width="20%">Less:</td>
                        <td width="30%"></td>
                        <td width="5%"></td>
                        <td class="caption" width="20%"></td>
                        <td class="small-td"></td> 
                    </tr>
                    
                    <tr>
                        <td class="caption" width="20%"></td>
                        <td width="30%">(e) Partner's Salary, Bonus & CPF</td>
                        <td width="5%"></td>
                        <td class="caption" width="20%"><input type="text" name="salary" id="salary" value="20000" class="mandatory"></td>
                        <td class="small-td">.00</td> 
                    </tr>
                    <tr>
                        <td class="caption" width="20%"></td>
                        <td width="30%">(f) Partner's Other Benefits</td>
                        <td width="5%"></td>
                        <td class="caption" width="20%"><input type="text" name="benefit" id="benefit" value="0" class="mandatory"></td>
                        <td class="small-td">.00</td> 
                    </tr>
                    <tr>
                        <td class="caption" width="20%"></td>
                        <td width="30%">(g) Divisible Profit/Loss[g] = [d] - [e] - [f]</td>
                        <td width="5%"></td>
                        <td class="caption" width="20%">330,000</td>
                        <td class="small-td">.00</td> 
                    </tr>
                    
                     <tr>
                        <th width="20%" style="BACKGROUND-COLOR: TRANSPARENT;font-size: 14px;COLOR: BLACK;FONT-WEIGHT: 1000;">2. INTEREST            <span><img src="images/notice.png" border="0" style="width: 14px;"></span></th>
                        <td width="30%"></td>
                        <td width="5%"></td>
                        <td class="caption" width="20%"><input type="text" name="interest" id="interest" value="0" class="mandatory"></td>
                        <td class="small-td">.00</td> 
                    </tr>
                    
                     <TR>
                        <TD class="caption" colspan="5" ><br></TD>
                    </TR>
                    <tr>
                        <th colspan="5" style="BACKGROUND-COLOR: #b31218;font-size: 14px;">SUMMARY</th>
                    </tr>
                    
                    <tr>
                        <th width="20%" style="BACKGROUND-COLOR: TRANSPARENT;font-size: 14px;COLOR: BLACK;FONT-WEIGHT: 1000;">TOTAL TAX CHARGED</th>
                        <td width="30%"></td>
                        <td width="5%"></td>
                        <td class="caption" width="20%"><input type="text" name="totaltax" id="totaltax" value="30000" class="mandatory"></td>
                        <td class="small-td">.00</td> 
                    </tr>
                    
                    <TR rowspan="2">
                        <td colspan="5">&nbsp;</td> 
                    </TR> 
                    <input type="hidden" name="desc" id="desc" value="">    
                </TABLE>

                <TABLE BORDER="0" WIDTH="80%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER">
                    <TR>
                        <TD ></TD>
                        <TD ></TD>
                        <TD ></TD>
                        <TD style="width: 12%;"><INPUT onclick="proceed();" name="btnproceed" type=button value="Proceed" class=button-login></TD>
                    </TR>
                </TABLE>            

<!--                <div id="transOption" class="hide-row">
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
                            <TD><INPUT onclick="verifyTrans('3');" name="btnOTPLogin" type="button" value="OTP" class=button-login <%=otpDisplay%>></TD>                                                                                             
                            <TD><INPUT onclick="verifyTrans('5');" name="btnCrOTPLogin" type="button" value="Sign CROTP" class=button-login <%=crOtpDisplay%>></TD>
                            <TD><INPUT onclick="verifyTrans('8');" name="btnPushCr" type=button value="Mobile Push" class=button-login <%=pushDisplay%>></TD> 
                            <TD><INPUT onclick="verifyTrans('4');" name="btnQRLogin" type=button value="QR Code" class=button-login <%=qrDisplay%>></TD>
                            <TD><INPUT onclick="verifyTrans('6');" name="btnPKILogin" type="button" value="PKI" class=button-login <%=pkiDisplay%>></TD>   
                            <TD><INPUT onclick="verifyTrans('9');" name="btnFidoLogin" type="button" value="FIDO" class=button-login <%=fidoDisplay%>></TD>
                        </TR>
                    </TABLE>
                    <br/><br/><br/><br/>
                </div>-->

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
        <div id="transOption" class="hide-row alert-box" 
             style="display: block;z-index: 1003;position: absolute;background-color: #fff;padding: 20px;padding-top: 0px;margin: 20px auto;border-radius: 6px;box-shadow: 0px 24px 25px rgba(36, 43 ,82, 0.2);width: -moz-max-content;display: none;overflow: hidden;height: 250px;width:675px;opacity: unset;">
            <br><br><br>
            <table border="0" width="70%" cellspacing="0" cellpadding="3" align="CENTER">
                <tbody><tr>
                    <th colspan="6">Confirm Submmision</th>
                </tr>  
                <tr>
                    <td colspan="6" class="caption">Please select a method below to complete the submission.</td>
                </tr>
                <tr>          
<!--                            <TD><INPUT onclick="verifyTrans('2');" name="btnSMSLogin" type="button" value="SMS" class=button-login style="visibility: hidden"></TD>
                    <TD><INPUT onclick="verifyTrans('3');" name="btnOTPLogin" type="button" value="OTP" class=button-login style="visibility: hidden"></TD>                                                                                             
                    <TD><INPUT onclick="verifyTrans('5');" name="btnCrOTPLogin" type="button" value="Sign CROTP" class=button-login style="visibility: hidden"></TD>
                    <TD><INPUT onclick="verifyTrans('8');" name="btnPushCr" type=button value="Mobile Push" class=button-login style="visibility: hidden"></TD> 
                    <TD><INPUT onclick="verifyTrans('4');" name="btnQRLogin" type=button value="QR Code" class=button-login style="visibility: hidden"></TD>-->
                    <td><input onclick="verifyTrans('6');" name="btnPKILogin" type="button" value="PKI" class="button-login" ></td>   
                    <td><input onclick="verifyTrans('9');" name="btnFidoLogin" type="button" value="FIDO" class="button-login"></td>
                </tr>
            </tbody></table>
            <br><br><br><br>
        </div>
          

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
