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
    String authToken = (String) request.getSession().getAttribute("authToken");
    String userid = (String) request.getSession().getAttribute("userid");
    String secretCode = (String) request.getSession().getAttribute("secretCode");
    String loginSession = (String) request.getSession().getAttribute("loginSession");
    String groupId = (String) request.getSession().getAttribute("groupId");
    String role = (String) request.getSession().getAttribute("role");
    String email = (String) request.getSession().getAttribute("email");
    String defDeviceName = (String) request.getSession().getAttribute("defDeviceName");
    String centagateUserId = (String) request.getSession().getAttribute("centagateUserId");
   String consolelog = (String)request.getSession().getAttribute("consolelog");
   String centoken = (String)request.getSession().getAttribute("cenToken");
   String fidoAuth = (String) request.getSession().getAttribute("FIDO");
   if(consolelog == null) {
       consolelog = "";
   }
   DateCalculator dateCalculator = new DateCalculator ();
   String todayDate = dateCalculator.convertToDate (  System.currentTimeMillis(),"yyyy-MM-dd");
   
    System.out.println("DEBUG Message[Self register page] : userid " + userid);
    System.out.println("DEBUG Message[Self register page] : authToken " + authToken);
     System.out.println("DEBUG Message[Self register page] : secretCode " + secretCode);
     
   if (fidoAuth == null) {
       fidoAuth = "N";
   }

%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"> 
        <title>Add User</title>
        <style type="text/css"><%@ include file="/styles/login.css" %></style>
        <style type="text/css"><%@ include file="/styles/styles.css" %></style>
        <style type="text/css"><%@ include file="/styles/w3.css" %></style>
        <script language="javascript" src="javascript/JSfunction.js"></script>
        <script language="javascript" src="javascript/JSFormat.js"></script>
        <script language="javascript" src="javascript/JSInvoke.js"></script>
        <script language="javascript" src="javascript/jquery-3.5.1.min.js"></script>
        <script language="javascript" src="javascript/jquery-ui-1.10.4.js"></script>
        <script type="text/javascript" src="javascript/fingerprint2.min.js"></script>

        <script language="javascript">
  
  
  
  var loginUsername = '<%=userid%>';
  var APIUrl = '<%=BaseURL%>';
  var baseURL = getBaseURL();
  d = new Date();
  var scanStateCheckService;
  
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
    if('<%=fidoAuth%>' !== "N") {
        document.getElementById('fidolist').style.display='block';
    }
    
    setConsoleLog("User registration process start");
   // getDeviceList();

    getGroupDetails();
}

function sendFidoRegistration() {
            
            centoken = window.document.FORM.centoken.value;
            makeCredentialOptions('<%=userid%>', '<%=userid%>', centoken).then(function(returnInfo) {

                    if (returnInfo.successMsg !== undefined) {
                            //fido registration sucessful
                            fetchCredentials(centoken, '<%=userid%>');
                    } else {
                            //fido failed return error message
                            msgbox('1', returnInfo.errorMsg);
                    }   
            });

}

function deleteFido() {
    centoken = window.document.FORM.centoken.value;
    deleteCredential(centoken, '<%=userid%>');
}

function getDeviceList() {
    setConsoleLog("Get Device List ..");
    var url = "./registerServlet?mode=5&userId=<%=userid%>";
    $.ajaxSetup({
        cache: false
    });
    
    $.ajax({
        url: url,
        type: "POST",
        timeout: 5000
    }).done(function(data) {  
         console.log(data);
        jsonvalue = JSON.parse(data);
        codeValue = jsonvalue.code;  
   
        if (codeValue === "0") {
            if (jsonvalue.object !== "") {
       
                groupDetails= JSON.parse(jsonvalue.object);
                var str ='Device Name: ';
        }
        else {
                var str ='Device Name: Not bound to any device';
        }
    }
        document.getElementById("authlist").innerHTML =str;
    }).fail(function(jqXHR, textStatus, errorThrown) {
        // log the error to the console
        console.error(
                "The following error occured: " +
                textStatus, errorThrown
                );
    });
}

function getGroupDetails() {
    setConsoleLog("Get group details ..");
    var url = "./groupServlet?mode=2&groupId=<%=groupId%>";
    $.ajaxSetup({
        cache: false
    });
    
    $.ajax({
        url: url,
        type: "POST",
        timeout: 5000
    }).done(function(data) {  
         console.log(data);
        jsonvalue = JSON.parse(data);
        codeValue = jsonvalue.code;  
        window.document.FORM.centoken.value=jsonvalue.centoken;        
        var authCount = 0;
        if (codeValue === "0") {
            if (jsonvalue.object !== "") {
       
                groupDetails= JSON.parse(jsonvalue.object);
                console.log(groupDetails);
                authOpt=groupDetails.authOpt;
                var str ='<select name=authmethod id=authmethod class=mandatory>';
                
                 for (var i = 0; i < authOpt.length; i++) {
                        switch (i) {
                            case 0:
//                                if (authOpt[i] !== 0) {
//                                    str +='<option value=cert>PKI</option>';
//                                }
                                break;
                            case 1:
                                 break;
                            case 3:
//                                if ((authOpt[1] !== 0) || (authOpt[3] !== 0)) {
//                                     str +='<option value=otp>OTP</option>';
//                                } 
                                break;
                            case 2:
//                                if (authOpt[i] !== 0) {
//                                    str +='<option value=sms>SMS</option>';
//                                }
                                break;
                            case 5:
                                break;
                            case 10:
//                                 if (authOpt[i] !== 0) 
//                                 {
//                                     str +='<option value=fprint>SecureGen Fingerprint</option>'; 
//                                 }
                                break;
                            case 7:
                                if (authOpt[7] !== 0) {
                                     if("<%=fidoAuth%>" === "N") {
                                        str +='<option value=fido>FIDO Token</option>';
                                        authCount++;
                                     }
                                }
                                break;
                            case 8:
                                if (authOpt[8] !== 0) {
                                     authMobile="true";
                                }  
                                break;
                            case 11:
                                if (authOpt[11] !== 0) {
                                     authMobile="true";
                                }
                                break;
                            default:
                                 break;
                        }
            }
            var devicename="<%=defDeviceName%>";
            if(devicename!=="" && devicename!=="null") {
                authMobile="false";
            }
            if(authMobile==="true") {
                str +='<option value=device>Device Provisining</option>';
                authCount++;
            }
            str +='</select>';
        }
        else {
            var str ='<select name=authmethod id=authmethod class=mandatory><option value=empty >no data</option></select>';
        }
    }
        console.log("authCount = "+ authCount);
        if(authCount !== 0) {
            document.getElementById("authlist").innerHTML =str;
        }
        else {
            document.getElementById('register').style.display='none'; 
        }
    }).fail(function(jqXHR, textStatus, errorThrown) {
        // log the error to the console
        console.error(
                "The following error occured: " +
                textStatus, errorThrown
                );
    });
}

function register() {
    
       
        authmethod  = window.document.FORM.authmethod.value;
        if(authmethod === "device") {    
            document.getElementById('register').style.display='none'; 
            document.getElementById('registered').style.display='none'; 
            document.getElementById('device').style.display='block';
            reqOneTimePin();
        }
        else {
           sendFidoRegistration();
        }
    //        document.getElementById('activation').style.display='block';  

    //        window.document.FORM.actUsername.value=username;
    //        window.document.FORM.actMobileNo.value=cmobile+mobileno;
    //        window.document.FORM.actCodeTxt.className="mandatory";

  
}


  
  function cancel()
  {
        window.document.FORM.username.className="mandatory";
        window.document.FORM.username.readOnly=false;
        window.document.FORM.username.tabIndex="";
        window.document.FORM.username.value="";
        
        window.document.FORM.password.className="mandatory";
        window.document.FORM.password.readOnly=false;
        window.document.FORM.password.tabIndex="";
        window.document.FORM.password.value="";
        
        window.document.FORM.cpassword.className="mandatory";
        window.document.FORM.cpassword.readOnly=false;
        window.document.FORM.cpassword.tabIndex="";
        window.document.FORM.cpassword.value="";
        
        window.document.FORM.cmobile.className="mandatory";
        window.document.FORM.cmobile.readOnly=false;
        window.document.FORM.cmobile.tabIndex="";
        window.document.FORM.cmobile.value="";
        
        window.document.FORM.mobileno.className="mandatory";
        window.document.FORM.mobileno.readOnly=false;
        window.document.FORM.mobileno.tabIndex="";
        window.document.FORM.mobileno.value="";

        window.document.FORM.actCodeTxt.className="";
        window.document.FORM.actCodeTxt.value="";
        
        window.document.FORM.btnregister.disabled=false; 
        window.document.FORM.btnactivate.disabled=false; 
        window.document.FORM.btnclear.disabled=false; 
        
        document.getElementById ( "register" ).style.color = "blue";
        document.getElementById('register').style.display='block';  
        document.getElementById('activation').style.display='none';  
        document.getElementById('device').style.display='none'; 
  }
  
function unregister(type){

    window.document.FORM.action = baseURL + "/registerServlet?mode=7&type=" + type;
    window.document.FORM.submit();
}


 
var username;
var mobileno;
var cmobile;
var userId;
var activationCode;
var tokenId;
var companyId;
var userEmail;
var password;
var cpassword;

function reqAction(obj){
        //remove onclick event
        if (obj.id=="register"){
            //do nothing
        }else{
            obj.onclick="";
            obj.style.color="#bbbbbb";
        }
        smsRequestCode();
}

function smsRequestCode() {
    setConsoleLog("Requesting SMS activation code");
    var url = "./registerServlet?mode=2&tokenId=" + tokenId + "&idUserActivationCode="+activationCode+"&userId="+userId+"&userCompanyId="+companyId+"&userEmail="+userEmail;
    $.ajaxSetup({
        cache: false
    });
    
    $.ajax({
        url: url,
        type: "POST"
    }).done(function(data) {       

        jsonvalue = JSON.parse(data);
        codeValue = jsonvalue.code;  

        if (codeValue == "0"){
            //success
            msgbox('2', 'User SMS activation code have been send to your mobile');
            setConsoleLog("User SMS activation code request succesfully");
        }else{
            //failed
            document.getElementById ( "register" ).onclick = function ( )
            {
                reqAction(document.getElementById ( "register" ));
            } ;
            document.getElementById ( "register" ).style.color = "blue";
            
            msgbox('1', 'User SMS activation code request failed');
            setConsoleLog("User SMS activation code request failed");
        }
        
        $('#actCodeTxt').focus();
    }).fail(function(jqXHR, textStatus, errorThrown) {
        // log the error to the console
        console.error(
                "The following error occured: " +
                textStatus, errorThrown
                );
    });
}

function activate(){
    var regexAct = /^[0-9]{6,8}$/;
    var actCode = trim($('#actCodeTxt').val());
    
    if(CheckMandatory(window.document.FORM))
    {
        
        if(!(regexAct.test(actCode))){
            $('#actCodeTxt').focus();
            msgbox('1', 'Invalid SMS code');
            window.document.FORM.btnactivate.disabled=false;  
         }else{   
            window.document.FORM.btnactivate.disabled=true;  

            activateUser(actCode);
         }
    }
}

function reqOneTimePin() {
    document.getElementById('qrDiv').style.display='block';
    document.getElementById('oneTimePinInfo').style.display='none';
    setConsoleLog("Sending request to register mobile device");
    var url = "./registerServlet?mode=6&username=<%=userid%>&userEmail=<%=email%>";
    $.ajaxSetup({
        cache: false
    });
    
    $.ajax({
        url: url,
        type: "POST"
    }).done(function(data) {       

        jsonvalue = JSON.parse(data);
        codeValue = jsonvalue.code;  
   
        if (codeValue == "0") {
            //display the QR code
             
            if (data.object != "") {
                objectValue = JSON.parse(jsonvalue.object);
                document.getElementById("qrDiv").innerHTML = "<img src=\"./qrcode?qrtext=" + encodeURIComponent(objectValue.qr) + "\" /> <br/> Scan the following QR code using your mobile device";
                
                document.getElementById("passcode").innerHTML = objectValue.passcode;
                document.getElementById("usernameDisplay").innerHTML = username;
                setConsoleLog("Request to register mobile device successful");
                scanStateCheckService = setInterval(function() {
                          refreshQRState();
                      }, interval);
                
            } else {
                document.getElementById("qrDiv").innerHTML = "Unable generate QR Code. Please try again.";
                setConsoleLog("Request to register mobile device failed");
            }
        }
        else {

            msgbox('1', 'Generate QR code request failed');
        }
    }).fail(function(jqXHR, textStatus, errorThrown) {
        // log the error to the console
        console.error(
                "The following error occured: " +
                textStatus, errorThrown
                );
    });
}

function reqOneTimePin2() {
    document.getElementById('qrDiv').style.display='none';
    document.getElementById('alternativeprov').style.display='none'; 
    document.getElementById('oneTimePinInfo').style.display='block';
 //   setConsoleLog("Sending request to register mobile device");
//    var url = "./registerServlet?mode=6&username="+username;
//    $.ajaxSetup({
//        cache: false
//    });
//    
//    $.ajax({
//        url: url,
//        type: "POST"
//    }).done(function(data) {       
//
//        jsonvalue = JSON.parse(data);
//        codeValue = jsonvalue.code;  
//   
//        if (codeValue == "0") {
//            //display the QR code
//            objectValue = JSON.parse(jsonvalue.object);
//
//            if (data.object != "") {
//                document.getElementById("passcode").innerHTML = objectValue.passcode;
//                document.getElementById("usernameDisplay").innerHTML = username;
//                setConsoleLog("Request Pin to register mobile device successful");
//                scanStateCheckService = setInterval(function() {
//                          refreshQRState();
//                      }, interval);
//                
//            } else {
//                document.getElementById("passcode").innerHTML = "Unable to generate One Time Pin. Please try again.";
//                setConsoleLog("Request to register mobile device failed");
//            }
//        }
//        else {
//
//            msgbox('1', 'Generate one time pin request failed');
//            setConsoleLog("Request to register mobile device failed");
//        }
//    }).fail(function(jqXHR, textStatus, errorThrown) {
//        // log the error to the console
//        console.error(
//                "The following error occured: " +
//                textStatus, errorThrown
//                );
//    });
}

function refreshQRState() {
    setConsoleLog("Verifying user device status ..");
    var url = "./registerServlet?mode=5&userId=<%=centagateUserId%>";
    $.ajaxSetup({
        cache: false
    });
    
    $.ajax({
        url: url,
        type: "POST",
        timeout: 5000
    }).done(function(data) {                            
        jsonvalue = JSON.parse(data);
        codeValue = jsonvalue.code;  
   
        if (codeValue == "0") {
            
            if (jsonvalue.object != "") {
                clearInterval(scanStateCheckService);
                msgbox('2', 'User mobile device register succesfully');
                setConsoleLog("User scanning QR Code sucessfully");
                objectValue = JSON.parse(jsonvalue.object);
                
                
                document.getElementById('device').style.display='none';
                document.getElementById('register').style.display='block';
                document.getElementById('registered').style.display='block';
            }
            
        }
    }).fail(function(jqXHR, textStatus, errorThrown) {
        // log the error to the console
        console.error(
                "The following error occured: " +
                textStatus, errorThrown
                );
        clearInterval(scanStateCheckService);
    });
}


function preRegisterFidoToken( response ) {
                  fidoResponse = eval('(' + response.data + ')');   
                  if ( response.status !== "ok" )
                  {
                      if ( response.errorCode === fidoErrorCode.OTHER_ERROR ) {
                           msgbox('1', 'Other device error - '+ response.errorMessage);
                      } else if ( response.errorCode === fidoErrorCode.BAD_REQUEST ) {
                          msgbox('1', 'Bad device request. The URL does not match App ID or not using HTTPS');
                      } else if ( response.errorCode === fidoErrorCode.CONFIGURATION_UNSUPPORTED ) {
                          msgbox('1', 'Client configuration is not supported' );
                      } else if ( response.errorCode === fidoErrorCode.DEVICE_INELIGIBLE ) {
                          msgbox('1', 'Device is not eligible');
                      } else if ( response.errorCode === fidoErrorCode.TIMEOUT ) {
                           msgbox('1', 'FIDO token not detected or authorised');
                      } else {
                           msgbox('1', 'FIDO registration failed');
                      }
                  }
                  else
                  {
                      registerFidoToken();
                  }
}

function registerFidoToken() {  
    
    setConsoleLog("Register FIDO Token ..");
    var url = "./registerServlet?mode=8&credentialId= "+fidoResponse[0].id +"&keyHandler="+fidoResponse[0].handle+"&publicKey="+fidoResponse[0].publicKey;
    $.ajaxSetup({
        cache: false
    });
    
    $.ajax({
        url: url,
        type: "POST",
        timeout: 5000
    }).done(function(data) {                            
        jsonvalue = JSON.parse(data);
        codeValue = jsonvalue.code;  
   
        if (codeValue === "0") {
                msgbox('2', 'FIDO register succesfully');
                document.getElementById('fidolist').style.display='block';
                
                setTimeout(function () {
                   goToPage('selfservice.jsp');
                }, 2000);
                
        } else {
                msgbox('1', 'FIDO register failed');
        }
    }).fail(function(jqXHR, textStatus, errorThrown) {
        // log the error to the console
        console.error(
                "The following error occured: " +
                textStatus, errorThrown
                );
    });
}

function logout()
{
    window.document.FORM.action = baseURL+"/LoginServlet?MODE=LOGOUT";
    window.document.FORM.submit();  
}

function setConsoleLog(message) {
        log = document.getElementById('consolelog').value;
        log = log + "<br>" + d.toLocaleString()+" : "+ message;
        document.getElementById('consolelog').value =log;
        document.getElementById('displayconsolelog').innerHTML =log;
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
        <fieldset width="50%" class="fieldsettitle">
             <br></br>
             
            <div id="registered">
                <TABLE BORDER="0" WIDTH="70%" CELLSPACING="10" CELLPADDING="3" ALIGN="CENTER">
                    <TR>
                        <TH colSpan=3>Registered Authentication Method</TH>
                    </TR>
                    <TR>
			        <TD class="caption" width="20%">Device</TD>
                                <TD width="30%" colspan="2">
                                    <span id="devicelist" >
                                    <% if(defDeviceName!=null) { %>
                                    <%=defDeviceName%>
                                    <a href="javascript:void(0);" id="unregisterdevice" style="padding-left: 0px;color: blue;" onclick="unregister('1');"  >unregister</a>    
                                    <% } %>
                                    </span></TD>
                    </TR>
                    <TR>
			        <TD class="caption" width="20%">FIDO</TD>
                                        <TD width="30%" colspan="2"><span id="fidolist" class="hide-row">FIDO Token
                                        <a href="javascript:void(0);" id=unregisterfido style="padding-left: 0px;color: blue;" onclick="deleteFido();">unregister</a>         
                                    </span></TD>
                    </TR>
                    
                </TABLE>
                
                </div>        
                <div id="register">   
                <TABLE BORDER="0" WIDTH="70%" CELLSPACING="10" CELLPADDING="3" ALIGN="CENTER">
                    <TR>
                        <TH colSpan=3>Choose authentication type for registration</TH>
                    </TR>    
                    <TR>
			        <TD class="caption" width="20%">Username</TD>
			        <TD width="30%" colspan="2"><input type="text" name="username" id="username" value="<%=userid%>" readonly tabindex="-1" class="input-display"></TD>

                    </TR>
                    <TR>
			        <TD class="caption" width="20%">Email</TD>
                                <TD width="30%" colspan="2"><input type="text" name="email" id="email" value="<%=email%>" readonly tabindex="-1" class="input-display"></TD> 
                    </TR>
                    
                    <TR>
			        <TD class="caption" width="20%">Available Authentication Method</TD>
                                <TD width="30%" colspan="2"><span id="authlist" ></span></TD>
                    </TR>
                    
<!--                    <TR>
			        <TD class="caption" width="20%">Mobile No.</TD>
                                <TD width="30%"><span id="country" ></span></TD>
			        <TD width="60%"><input type="text" name="mobileno" id="mobileno" value="" class="mandatory"></TD>
                    </TR>-->
 
                </TABLE>
             
                <TABLE BORDER="0" WIDTH="70%" CELLSPACING="10" CELLPADDING="3" ALIGN="CENTER">
                    <TR>
                      <TD colspan=2 width="70%"></TD>
                      <TD width="15%"><INPUT onclick="cancel();" name="btnclear" type=button value="Clear" class=button-login></TD>
                      <TD width="15%"><INPUT onclick="register();" name="btnregister" type=button value="Register" class=button-login></TD>
                      </TR>
                </TABLE>
           
            </div> 
            
            <div id="activation" class="hide-row">
            
                  <TABLE BORDER="0" WIDTH="70%" CELLSPACING="10" CELLPADDING="3" ALIGN="CENTER" style="margin-left: 16.5%;">
                    <TR>
                        <TH colSpan=3>New user activation</TH>
                    </TR>    
                    <TR>
			        <TD class="caption" width="20%">Username</TD>
			        <TD width="60%" colspan="2"><input type="text" id="actUsername" value="" readonly tabindex="-1" class="input-display"></TD>

                    </TR>
                    <TR>
			        <TD class="caption" width="20%">Email</TD>
			        <TD width="60%"><input type="text" id="actMobileNo" value="" readonly tabindex="-1" class="input-display"></TD>
                    </TR>

                    <TR>
                                <TD  colspan="3">You can activate your account by entering the SMS code.</TD> 
                    </TR>
                    
                    <TR>
			        <TD colspan="3">
                                <input id="actCodeTxt" name="actCodeTxt" autocomplete="off" type="text" style="width: 100px"  value = ""> 
                                <br>
                                <a href="javascript:void(0);" id="register" style="padding-left: 0px;color: blue;" onclick="reqAction(this);"  >Request SMS code</a>
                                </TD>
                    </TR>
                </TABLE>
                
                <TABLE BORDER="0" WIDTH="70%" CELLSPACING="10" CELLPADDING="3" ALIGN="CENTER">
                    <TR>
                      <TD colspan=2 width="70%"></TD>
                      <TD width="15%"></TD>
                      <TD width="15%"><INPUT onclick="activate();" name="btnactivate" type=button value="Activate" class=button-login></TD>
                      </TR>
                </TABLE>
 
            </div>
             
            <div id="device" class="hide-row">
            
                  <TABLE BORDER="0" WIDTH="70%" CELLSPACING="10" CELLPADDING="3" ALIGN="CENTER" style="margin-left: 16.5%;">
                    <TR>
                        <TH colSpan=3>Register user mobile device</TH>
                    </TR>    
                    <TR>
			        <TD class="caption" width="20%">Username</TD>
			        <TD width="60%" colspan="2"><input type="text" id="deviceUsername" value="<%=userid%>" readonly tabindex="-1" class="input-display"></TD>

                    </TR>
                    <TR>
			        <TD class="caption" width="20%">Email</TD>
			        <TD width="60%"><input type="text" id="userEmail" value="<%=email%>" readonly tabindex="-1" class="input-display"></TD>
                    </TR>
<!--                    
                    <TR>
                        <TD colspan="3">Please choose one option to bind your mobile :</TD>

                    </TR>-->

<!--                    <TR>
			        <TD colspan="3">
                                <a href="javascript:void(0);" onclick="registerDevice();"  class="hyperlink" >Request bind mobile device with QR Code</a>
                                </TD>
                    </TR>-->
<!--                    <TR>
			        <TD colspan="3">
                                <a href="javascript:void(0);" onclick="reqOneTimePin();"  class="hyperlink" >Request Email to bind mobile device with User ID</a>
                                </TD>
                    </TR>-->
                    <TR>
			        <TD colspan="3" align=center>
                                <div id="qrDiv"></div>
                                <a href="javascript:void(0);" onclick="reqOneTimePin2();"  id="alternativeprov" class="hyperlink" >Alternative way by manual input</a>
                                <div id="oneTimePinInfo" class="hide-row">
                                    <TABLE BORDER="0" WIDTH="70%" CELLSPACING="0" CELLPADDING="3" ALIGN="CENTER">
                                        <TR>
                                            <TD colspan=5 class="caption" width="20%">Please choose code provisioning at mobile and input below info :</TD>
                                        </TR>
                                        <TR>
                                            <TD colspan=5 class="caption" width="20%">Client ID : <text id="usernameDisplay"></text></TD>
                                  

                                        </TR>
                                        <TR>
                                            <TD colspan=5 class="caption" width="80%">Passcode  : <text id="passcode"></text></TD>
                                        </TR>
                                        
                                    </TABLE>
                                </div>
                                </TD>
                    </TR>
                    
                </TABLE>
                
                <TABLE BORDER="0" WIDTH="70%" CELLSPACING="10" CELLPADDING="3" ALIGN="CENTER">
                    <TR>
                      <TD colspan=2 width="70%"></TD>
                      <TD width="15%"><INPUT onclick="cancel();" name="btnclear" type=button value="Cancel" class=button-login></TD>
                      <TD width="15%"></TD>
                      </TR>
                </TABLE>
 
            </div> 
            
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
<a id="showlink" href = "javascript:void(0)" style="text-align:left; display:block;margin-left:15%;" onclick = "document.getElementById('showlink').style.display='none';document.getElementById('logcontent').style.display='block';">Show</a>
<div id="logcontent" style="display:none;margin-left:15%;">
<a href = "javascript:void(0)" style="text-align:left;margin-left:15%;" onclick = "document.getElementById('logcontent').style.display='none';document.getElementById('showlink').style.display='block';">Hide</a>    
<text id="displayconsolelog" name="displayconsolelog"><%=consolelog%></text>
</div>
<INPUT name="consolelog" id="consolelog" type="hidden" value="<%=consolelog%>">
<INPUT name="centoken" id="centoken" type="hidden" value="<%=centoken%>">
<script type="text/javascript" src="javascript/json2.js"></script>            
<script type="text/javascript" src="javascript/base64url-arraybuffer.js"></script>
<script type="text/javascript" src="javascript/fido.js"></script>
        
</body>
</form>
    
</html>
