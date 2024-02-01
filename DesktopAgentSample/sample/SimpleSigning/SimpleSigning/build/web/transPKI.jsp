<%-- 
    Document   : index
    Created on : May 10, 2016, 9:48:44 AM
    Author     : User
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    String plainText = "";
    String signature = "";

    
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta http-equiv="Pragma" content="no-cache" /> 
        <meta http-equiv="Cache-Control" content="private, no-store, no-cache, must-revalidate" /> 
		<title>BPI Signing Sample</title>
		
                <script language="javascript" src="javascript/jquery-1.11.1.js"></script>
                <script language="javascript" src="javascript/jquery-ui-1.10.4.js"></script>
                <script language="javascript" src="javascript/centagate-agent.js"></script>
		<script language="javascript">
			//var Base64={_keyStr:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",encode:function(e){var t="";var n,r,i,s,o,u,a;var f=0;e=Base64._utf8_encode(e);while(f<e.length){n=e.charCodeAt(f++);r=e.charCodeAt(f++);i=e.charCodeAt(f++);s=n>>2;o=(n&3)<<4|r>>4;u=(r&15)<<2|i>>6;a=i&63;if(isNaN(r)){u=a=64}else if(isNaN(i)){a=64}t=t+this._keyStr.charAt(s)+this._keyStr.charAt(o)+this._keyStr.charAt(u)+this._keyStr.charAt(a)}return t},decode:function(e){var t="";var n,r,i;var s,o,u,a;var f=0;e=e.replace(/[^A-Za-z0-9+/=]/g,"");while(f<e.length){s=this._keyStr.indexOf(e.charAt(f++));o=this._keyStr.indexOf(e.charAt(f++));u=this._keyStr.indexOf(e.charAt(f++));a=this._keyStr.indexOf(e.charAt(f++));n=s<<2|o>>4;r=(o&15)<<4|u>>2;i=(u&3)<<6|a;t=t+String.fromCharCode(n);if(u!=64){t=t+String.fromCharCode(r)}if(a!=64){t=t+String.fromCharCode(i)}}t=Base64._utf8_decode(t);return t},_utf8_encode:function(e){e=e.replace(/rn/g,"n");var t="";for(var n=0;n<e.length;n++){var r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r)}else if(r>127&&r<2048){t+=String.fromCharCode(r>>6|192);t+=String.fromCharCode(r&63|128)}else{t+=String.fromCharCode(r>>12|224);t+=String.fromCharCode(r>>6&63|128);t+=String.fromCharCode(r&63|128)}}return t},_utf8_decode:function(e){var t="";var n=0;var r=c1=c2=0;while(n<e.length){r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r);n++}else if(r>191&&r<224){c2=e.charCodeAt(n+1);t+=String.fromCharCode((r&31)<<6|c2&63);n+=2}else{c2=e.charCodeAt(n+1);c3=e.charCodeAt(n+2);t+=String.fromCharCode((r&15)<<12|(c2&63)<<6|c3&63);n+=3}}return t}}

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
                            var username = document.getElementById ( "usernameField" ).value ;
                            var plainText = document.getElementById ( "plainTextField" ).value ;
                            
                            document.getElementById ( "signatureField" ).value = signature ;
                            console.log(signature);
                            alert(signature);
        
//                            
//                            var url = "./VerifySignature";
//                            $.ajaxSetup({
//                                cache: false
//                            });
//
//                            $.ajax({
//                                url: url,
//                                data: {
//                                    username: username,
//                                    signature: signature,
//                                    plainText: plainText
//                                },
//                                type: "POST",
//                                async: false
//                            }).done(function(result) {                            
//                                jsonvalue = JSON.parse(result);
//                                codeValue = jsonvalue.code;  
//                                msgValue = jsonvalue.message;
//                                if (codeValue == "0") {
//                                    alert ( "Signature verification Success" ) ;
//                                }
//                                else {
//                                    alert ( msgValue ) ;
//                                }
//                            }).fail(function() {
//                                    alert ( "Error !!" ) ;
//                            });
                        }
    
			function signP7 ( plainText )
			{
				var obj = document.getElementById ( "bpi" ) ;
				var ret = obj.Pkcs7Sign ( 0 , "" , 2 , 0 , plainText , "" ) ;

				return ret ;
			}
			
			function validateForm ( )
			{
				var username = document.getElementById ( "usernameField" ).value ;
				var fromAccount = document.getElementById ( "fromField" ).value ;
				var toAccount = document.getElementById ( "toField" ).value ;
				var amount = document.getElementById ( "amountField" ).value ;
				var date = document.getElementById ( "dateField" ).value ;
                                

				
//				if ( username === "" )
//				{
//					alert ( "You have not entered username" ) ;
//					return false;
//				}
//				else if ( fromAccount === "" )
//				{
//					alert ( "You have not entered from account" ) ;
//					return false;
//				}
//				else if ( toAccount === "" )
//				{
//					alert ( "You have not entered to account" ) ;
//					return false;
//				}
//				else if ( amount === "" )
//				{
//					alert ( "You have not entered amount" ) ;
//					return false;
//				}
//				else if ( date === "" )
//				{
//					alert ( "You have not entered date" ) ;
//					return false;
//				}
//				else
//				{
                                        
					var plainText = WebSafeBase64.encode ( fromAccount + toAccount + amount + date , true) ;

                                        document.getElementById ( "plainTextField" ).value = plainText ;
                                        
                                        //p7SignByP11(false, plainText, 0, onSignCallback) ;
                                        p7SignByCsp(false, plainText, 0, onSignCallback);
                                        
				//}
			}

		</script>
    </head>
    <body>
        		<object CLASSID="clsid:52B60D69-D689-4A91-AF63-7B4168592F05" id="bpi" codebase="CentagateBPI.ocx" src="CentagateBPI.ocx">
		</object>
<br/><br/><br/><br/>
		<h1>Transaction Signing and Verification</h1>

                <form id="Form1" method="post">
				<table border=0>
				<tr>
					<td>Username</td>
					<td>:</td>
					<td>
					
                                                <input id="usernameField" type="text" name="usernameField"/>
					</td>
				</tr>
				
				<tr>
					<td>From</td>
					<td>:</td>
					<td>
			
                                                <input id="fromField" type="text" name="fromField"/>
					</td>
				</tr>
				
				<tr>
					<td>To</td>
					<td>:</td>
					<td>
						
                                                <input id="toField" type="text" name="toField"/>
					</td>
				</tr>
				
				<tr>
					<td>Amount</td>
					<td>:</td>
					<td>
						
                                                <input id="amountField" type="text" name="amountField"/>
					</td>
				</tr>
				
				<tr>
					<td>Date</td>
					<td>:</td>
					<td>
                                           
                                                <input id="dateField" type="text" name="dateField"/>
					</td>
				</tr>
				
				<tr>
					<td colspan="3">
                                                
                                                <input id="btnReset" onclick="validateForm ();" type="submit" value="Sign and Verify"/>
					<!--	<asp:Button id="btnReset" onclientclick="return validateForm()" onclick="btnReset_Click" runat="server" Text="Sign and Verify"></asp:Button>    -->
					</td>
				</tr>
<br/><br/>
                                <tr>
                                        <td colspan="3">
                                        <!--	<asp:Label id="resultLabel" runat="server"></asp:Label> -->
                                                PlainText : 
                                                <textarea  id="plainTextField" value="asd" type="text"></textarea>

                                        </td>
                                </tr>
                                <tr>
                                        <td colspan="3">
                                        <!--	<asp:Label id="resultLabel" runat="server"></asp:Label> -->
                                                Signature : 

                                                <textarea  id="signatureField" value="qwe" type="text"></textarea>
                                        </td>
                                </tr>

			</table>
                        
			<a href="#"><IMG style="Z-INDEX: 102; LEFT: 16px; POSITION: absolute; TOP: 16px" src="logo.jpg"></a>
			
		</form>
    </body>
</html>
