/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function trim(str){
    if ((typeof (str) !== 'undefined') && (str !== null)) {
        return str.replace(/^\s*([\S\s]*?)\s*$/, '$1');
    } else{
        return "";
    }
}
function focusText(id){
    document.getElementById(id).focus();
    document.getElementById(id).select();
}
function focusCbo(id){
    document.getElementById(id).focus();
}

function allownumbers(e) {
    
     //below section is to fix firefox unable to retrieve event object
    //when e.which is empty
    if (e.which){
        //do nothing
    }else{
        if (typeof (event) == "undefined"){
            return true;
        }
    }
    
    //var key = window.event ? e.keyCode : e.which;
    var key = (e.which) ? e.which : event.keyCode;
            var keychar = String.fromCharCode(key);
            var reg = new RegExp("[0-9]")
            if (key == 8 || key == 46 || key == 16 || key == 9) { //backspace, left, right, del, shift
        keychar = "8";
    }
    if (key == 46) {//46= "dot", not user why it will passed the regexp
        return false;
    }
    return reg.test(keychar);
}

function getCaretPos(input) {
    // Internet Explorer Caret Position (TextArea)
    if (document.selection && document.selection.createRange) {
        var range = document.selection.createRange();
        var bookmark = range.getBookmark();
        var caret_pos = bookmark.charCodeAt(2) - 2;
    } else {
        // Firefox Caret Position (TextArea)
        if (input.setSelectionRange)
            var caret_pos = input.selectionStart;
    }

    return caret_pos;
}

function allowphonenumbers(e, obj) {
    //below section is to fix firefox unable to retrieve event object
    //when e.which is empty
    if (e.which){
        //do nothing
    }else{
        if (typeof (event) == "undefined"){
            return true;
        }
    }
    
    if (getCaretPos(obj)==0){
        
        var key = (e.which) ? e.which : event.keyCode;
        var keychar = String.fromCharCode(key);
        var reg = new RegExp("[0-9]");
        if (key == 8 || key == 46 || key == 16 || key == 9) { //backspace, del, shift, tab
            keychar = "8";
        }        
        if (key == 46) {//46= "dot", not user why it will passed the regexp
            return false;
        }
        if (key == 43) {//43= "+, true
            if (obj.value.indexOf("+")>=0){
                //already exist, do not allow
                return false;
            }else{
                return true;
            }
        }        
        if (obj.value.indexOf("+")>=0){
            //if there is a '+', then cannot add anything infront
            return false;
        }else{
            return reg.test(keychar);
        }
    }else{
        var key = (e.which) ? e.which : event.keyCode;
        var keychar = String.fromCharCode(key);
        var reg = new RegExp("[0-9]");
        if (key == 8 || key == 46 || key == 16 || key == 9) { //backspace, del, shift, tab
            keychar = "8";
        }        
        if (key == 46) {//46= "dot", not user why it will passed the regexp
            return false;
        }
        return reg.test(keychar);
    }
        
}

function getSelectedText() {
    var text = "";
    if (typeof window.getSelection != "undefined") {
        text = window.getSelection().toString();
    } else if (typeof document.selection != "undefined" && document.selection.type == "Text") {
        text = document.selection.createRange().text;
    }
    alert(text) ;
}
function textFieldLimit(textField, maxInput){
    if (textField.value.indexOf("-")!==0){                        
        //only allow 3 digit
        if (textField.value.length >= maxInput){            
            return false;
        }          
    }
    return true;
}
function allowNegetiveNumbers(maxInput, e, obj) {
    //below section is to fix firefox unable to retrieve event object
    //when e.which is empty
    if (e.which){
        //do nothing
    }else{
        if (typeof (event) == "undefined"){
            return true;
        }
    }
    
    if (getCaretPos(obj)==0){
        
        var key = (e.which) ? e.which : event.keyCode;
        var keychar = String.fromCharCode(key);
        var reg = new RegExp("^-?[0-9]\d*$");
        if (key == 8 || key == 46 || key == 16 || key == 9) { //backspace, del, shift, tab
            keychar = "8";
        }        
        if (key == 46) {//46= "dot", not user why it will passed the regexp
            return false;
        }
        if (key == 45) {//45= "-", true
            if (obj.value.indexOf("-")>=0){
                if(obj.selectionStart==0 && obj.selectionEnd==obj.value.length){
                    //return textFieldLimit(obj,maxInput);
                    return true;    
                }else{
                    return false;
                }
            }else{
                //return textFieldLimit(obj,maxInput);
                return true;
            }
        }        
        if (obj.value.indexOf("-")>=0){
            if (typeof window.getSelection != "undefined") {
                if(obj.selectionStart==0 && obj.selectionEnd==obj.value.length){
                    //return textFieldLimit(obj,maxInput);
                    return true;    
                }else{
                    return false;
                }
            } else if (typeof document.selection != "undefined" && document.selection.type == "Text") {
                if(obj.selectionStart==0 && obj.selectionEnd==obj.value.length){
                    //return textFieldLimit(obj,maxInput);
                    return true;    
                }else{
                    return false;
                }                
            } else {
                //if there is a '-', then cannot add anything infront
                return false;
            }            
        }else{
            var ok= reg.test(keychar);
            if (ok){
                if (typeof window.getSelection != "undefined") {
                    if(obj.selectionStart==0 && obj.selectionEnd==obj.value.length){
                        //return textFieldLimit(obj,maxInput);
                        return true;    
                    }else{
                        return false;
                    }
                } else if (typeof document.selection != "undefined" && document.selection.type == "Text") {
                    if(obj.selectionStart==0 && obj.selectionEnd==obj.value.length){
                        //return textFieldLimit(obj,maxInput);
                        return true;    
                    }else{
                        return false;
                    }                
                } else {
                    return textFieldLimit(obj,maxInput);
                }  
            }else{
                return ok;
            }
        }
    }else{        
        var key = (e.which) ? e.which : event.keyCode;
        var keychar = String.fromCharCode(key);
        var reg = new RegExp("[0-9]");
        if (key == 8 || key == 46 || key == 16 || key == 9) { //backspace, del, shift, tab
            keychar = "8";
        }        
        if (key == 46) {//46= "dot", not user why it will passed the regexp
            return false;
        }
        var ok = reg.test(keychar);
        if (ok){
            return textFieldLimit(obj,maxInput);
        }else{
            return ok;
        }
    }
        
}

function generateAuthToken ( fnCallBack ) {
    var dt = new Date () ;
    var ajax = dt.getTime () ;    
    var url = "./restfulUtil?ajax=" + ajax + "&mode=gentoken" ;
   
    $.ajax({
        url: url,
        cache : false,
        type: "POST",
        timeout: 10000
    }).done(function ( output ){
    //    alert(output);
        var response = output.split ( "|" ) ;
        response[1] = trim ( response[1] ) ;
        
        if ( response[0] == "1" ) {
            //success
           // console.log("success. centoken="+ trim(response[1]));
            //"connectRestful(centoken)" is the implementation methods that should be write on each file that call the generateAuthToken
            fnCallBack(trim(response[1]));
        } else if ( trim ( response[0] ) == "-1" ) {
            window.location = "./logout?errMsg=error-invalid-session" ;
        } else if ( trim ( response[0] ) == "-2" ) {
            window.location = "./index.jsp?c=nopermission&m=" ;
        } else if ( trim ( response[0] ) == "-3" ) {
            window.location = "./logout?errMsg=possible-attack" ;        
        } else {
            window.location = "./logout?errMsg=error-invalid-session" ;
        }
    }).fail(function (jqXHR, textStatus, errorThrown){                
        // log the error to the console
        console.error(
            "The following error occured during generate auth token: "+
            textStatus, errorThrown
        );
    });
}

function indexOf ( array , object )
{
    if ( array.length )
    {
        /* Sent array is really an array */
        var length = array.length ;
        
        for ( var i = 0 ; i < length ; i ++ )
            if ( array [ i ] === object )
                return i ;
    }
    
    return -1 ;
}

function dateCheck( startDateString, endDateString) {
    //format is mm/dd/yy
    
    var partsStart = startDateString.split('/');
    var partsEnd = endDateString.split('/');
    
    //year, month, day
    var fDate = new Date(partsStart[2], partsStart[0], partsStart[1]);
    var lDate = new Date(partsEnd[2], partsEnd[0], partsEnd[1]);
    
    if(fDate <= lDate) {
        return true;
    }
    return false;
}