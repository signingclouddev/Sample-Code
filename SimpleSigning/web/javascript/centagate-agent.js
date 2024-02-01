/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var host = "https://local.bpisecuretoken.com:48080";
var xhttp = new XMLHttpRequest();


function p7SignByCsp(detached, webSafeB64Data,  algo, callback) 
{
    var urlParam = host + "/p7signbycsp?";
    
    urlParam += "{";
    urlParam += "\"data\"" + ":" + "\"" + webSafeB64Data + "\",";
    urlParam += "\"detach\"" + ":" + "\"" + ((detached === true) ? 1 : 0) + "\",";
    urlParam += "\"algo\"" + ":" + "\"" + algo + "\"";
    urlParam += "}";

    
    xhttp.open("GET",urlParam,false);
    xhttp.onreadystatechange=function() {
        var errorCode;
        var signature = "";
        
        if(xhttp.readyState === 4) {
            if(xhttp.status === 404)
                errorCode = 404;
            else if(xhttp.status !== 200)
                errorCode = 404;
            else {
               
                var jsonObject = JSON.parse(xhttp.responseText);                
                if(jsonObject.status === "0")
                {
                    if( jsonObject.object.signature === undefined) {
                        errorCode = 404;
                    } else {
                        errorCode = 0;
                        signature = jsonObject.object.signature;
                    }
                }
                else
                    errorCode = (jsonObject.status === undefined) ? 404  : parseInt( jsonObject.status );
                
            }
        }
        else 
            errorCode = 404;
       
        if(typeof callback === "function")
            callback(errorCode, signature);
        
        
    };

    xhttp.setRequestHeader('Content-Type', 'text/plain;charset=UTF-8');
    xhttp.send();     
}


function p7SignByP11(detached, webSafeB64Data,  algo, callback) 
{
    var urlParam = host + "/p7signbyp11?";
    
    urlParam += "{";
    urlParam += "\"data\"" + ":" + "\"" + webSafeB64Data + "\",";
    urlParam += "\"detach\"" + ":" + "\"" + ((detached === true) ? 1 : 0)  + "\",";
    urlParam += "\"algo\"" + ":" + "\"" + algo + "\"";
    urlParam += "}";

    
    xhttp.open("GET",urlParam,false);
    
    xhttp.onreadystatechange=function() {
        var errorCode;
        var signature = "";
        
        if(xhttp.readyState === 4) {
            if(xhttp.status === 404)
                errorCode = 404;
            else if(xhttp.status !== 200)
                errorCode = 404;
            else {
                //success 
               
                var jsonObject = JSON.parse(xhttp.responseText);                
                if(jsonObject.status === "0")
                {
                    if( jsonObject.object.signature === undefined) {
                        errorCode = 404;
                    } else {
                        errorCode = 0;
                        signature = jsonObject.object.signature;
                    }
                }
                else
                    errorCode = (jsonObject.status === undefined) ? 404  : parseInt( jsonObject.status );
                
            }
        }
        else 
            errorCode = 404;
       
        if(typeof callback === "function")
            callback(errorCode, signature);
        
        
    };

    xhttp.setRequestHeader('Content-Type', 'text/plain;charset=UTF-8');
    xhttp.send();     
}