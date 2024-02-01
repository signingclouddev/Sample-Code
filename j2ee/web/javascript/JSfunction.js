
var arrMandatory_msg = new Array('Mandatory Field Missing', 'ע��!�����ֶ�', '�`�N!�����r�q' ,'', '', '', '', '');

//Check Mandatory
function CheckMandatory(form)
{
    alertMSG = "Mandatory Field Missing";

    if(window.LANGUAGE_CODE)
      alertMSG = arrMandatory_msg[window.LANGUAGE_CODE];

    eArray= new Array(); //to keep the HTML element objects which is mandatory input
    ClassType = new String();

    for(i=0;i<form.elements.length;i++)
    {

      if(form.elements[i].className=="validate-mandatory")
      {form.elements[i].className="MANDATORY";}
      else
        if(form.elements[i].className=="validate-radio-mandatory")
        {form.elements[i].className="RADIO-MANDATORY";}

      ClassType = form.elements[i].className;

      if((ClassType.toLowerCase()=="mandatory")||(ClassType.toLowerCase()=="radio-mandatory"))
      {
              if((form.elements[i].type=="radio")&&(form.elements[i].checked==false))
         {
            var RadioCheck=false;
            //Loop forward for a set of Radio Button
            for(j=i+1;j<form.elements.length;j++)
            {
               if((form.elements[j].type=="radio")&&(form.elements[j].name==form.elements[i].name))
               {
                  if(form.elements[j].checked==true)
                  {
                     RadioCheck=true;
                     break;
                  }
               }
            }
                 //Loop backward for a set of Radio Button
            for(k=i-1;k>=0;k--)
            {
               if((form.elements[k].type=="radio")&&(form.elements[k].name==form.elements[i].name))
               {
                  if(form.elements[k].checked==true)
                  {
                     RadioCheck=true;
                     break;
                  }
               }
            }

                 if(RadioCheck==false)
                 {eArray[eArray.length] = i;}//keep the position of the mandatory field
         }
         else
            if((form.elements[i].type=="checkbox")&&(form.elements[i].checked==false))
            {eArray[eArray.length] = i;}//keep the position of the mandatory field
         else
            if((trim(form.elements[i].value)=="")&&(form.elements[i].type!="radio"))
            {eArray[eArray.length] = i;}//keep the position of the mandatory field
      }

    }

    if(eArray.length=== 0)
    {
        //all mandatory fields has been filled up
        return CheckNumberField(form,'amount') && CheckDateField(form,'effdate');       
    }
    else
    {
       for(l=0;l<eArray.length;l++)
       {
          if(form.elements[eArray[l]].type=="radio")
          {form.elements[eArray[l]].className = "validate-radio-mandatory"; }
          else
          {
              form.elements[eArray[l]].className = "validate-mandatory";
              form.elements[eArray[l]].focus();
          }
       }

       document.getElementById('fade').style.display='block'
       document.getElementById('error-message').style.display='block'; 
       document.getElementById('error-text').innerHTML =alertMSG;
       setTimeout(function(){ 
           document.getElementById('error-message').style.display='none';
           document.getElementById('fade').style.display='none';
       }, 2000);

       return false;
    }
      
}//End of Mandatory field checking

function trim(s){
  while (s.substring(0,1) == ' ') {
    s = s.substring(1,s.length);
  }
  while (s.substring(s.length-1,s.length) == ' ') {
    s = s.substring(0,s.length-1);
  }
  return s;
}

//replace character by character in string
function replaceString(str, findStr, changeTo)
{
	 //str       => String for replace
	 //find      => find the character to be replaced
	 //changeTo  => character to replace
    arrStr = str.split(findStr);
    strReturn = "";

	 for(k=0; k < arrStr.length; k++)
	 {
	    if((arrStr.length - k) > 1)
	    {
	       strReturn += arrStr[k] + changeTo;
	    }
	    else
	    {
	       strReturn += arrStr[k];
	    }
	 } 

	 return strReturn;
}

/**
 * Define the constant for Password Length
 * @type PasswordLength
 */
var PasswordLength = {
    6 : 6,          //Length 6 characters
    8 : 8,          //Length 8 characters
    10 : 10,        //Length 10 characters
    12 : 12,        //Length 12 characters
    16 : 16,        //Length 16 characters
    32 : 32,        //Length 32 characters
    64 : 64         //Length 64 characters
};

/**
 * Define the constant for Password Length Decription
 * @type PasswordLengthDesc
 */
var PasswordLengthDesc = {
    6 : "6 characters",          //Length 6 characters
    8 : "8 characters",          //Length 8 characters
    10 : "10 characters",        //Length 10 characters
    12 : "12 characters",        //Length 12 characters
    16 : "16 characters",        //Length 16 characters
    32 : "32 characters",        //Length 32 characters
    64 : "64 characters"         //Length 64 characters
};

/**
 * Define the constant for Password Complexity Numbers
 * @type PasswordComplexityNumber
 */
var PasswordComplexityNumber = {
    0 : "",                         //No Restriction
    1 : "1",                        //Must mix letters and digits
    2 : "2",                        //Must mix lower and uppercase letters and digits
    3 : "3"                         //Must mix lower and uppercase letters,digits and special characters
};

/**
 * Define the constant for Password Complexity Numbers Decription
 * @type PasswordComplexityNumberDesc
 */
var PasswordComplexityNumberDesc = {
    0 : "",                         //No Restriction
    1 : "must mix letters and digits",                        
    2 : "must mix lower and uppercase letters and digits",                        
    3 : "must mix lower and uppercase letters, digits and special characters"                      
};

/**
 * Define the constant for Password Complexity Numbers Rules
 * @type PasswordComplexityNumberRule
 */
var PasswordComplexityNumberRule = {
    NONE : "",                         //No Restriction
    UPPER_CASE  : /[A-Z]/,                        
    LOWER_CASE : /[a-z]/,
    NUMBER: /[0-9]/,
    SPECIAL_CASE: /[ !"#$%&'()*+,\-./:;<=>?@[\\\]^_`{|}~]/
};

/**
 * 
 * Validate reset password based on the company group policies.
 * 
 * @param {type} passwordMinLength
 *               Min length character of Password
 * @param {type} passwordComplexityNum
 *               The rule complexity of Password
 * 
 */
function validatePasswordBasedGroupPolicies(passwordMinLengthNum, passwordComplexityNum, passwordTextFieldVal) {
    if (!(passwordMinLengthNum === null || passwordMinLengthNum.length === 0)) {
        var passwordMinLengthVal = PasswordLength[passwordMinLengthNum];
        var passwordMinLengthValDesc = PasswordLengthDesc[passwordMinLengthNum];
        if(passwordTextFieldVal.length < passwordMinLengthVal) {
             msgbox('1', "Password must contain at least " + passwordMinLengthValDesc);
             return false;
        }
        
    }
    if (!(passwordComplexityNum === null || passwordComplexityNum.length === 0)) {
        var passwordComplexityNumVal = PasswordComplexityNumber[passwordComplexityNum];
        if(!(passwordComplexityNumVal === null || passwordComplexityNumVal.length === 0)) {
            
            if(passwordComplexityNum === PasswordComplexityNumber[1]) {//Must mix letters and digits
              
              if(!((PasswordComplexityNumberRule.LOWER_CASE.test(passwordTextFieldVal)
                      || PasswordComplexityNumberRule.UPPER_CASE.test(passwordTextFieldVal))
                      && PasswordComplexityNumberRule.NUMBER.test(passwordTextFieldVal))) {
                   msgbox('1', "Password " + PasswordComplexityNumberDesc[passwordComplexityNum]);
                  return false;
              }
            }
            
            if(passwordComplexityNum === PasswordComplexityNumber[2]) {//Must mix lower and uppercase letters and digits
              
              if(!(PasswordComplexityNumberRule.LOWER_CASE.test(passwordTextFieldVal)
                      && PasswordComplexityNumberRule.UPPER_CASE.test(passwordTextFieldVal)
                      && PasswordComplexityNumberRule.NUMBER.test(passwordTextFieldVal))) {
                   msgbox('1', "Password " + PasswordComplexityNumberDesc[passwordComplexityNum]);
                  return false;
              }
            }
            
             if(passwordComplexityNum === PasswordComplexityNumber[3]) {//Must mix lower and uppercase letters,digits and special characters
              
              if(!(PasswordComplexityNumberRule.LOWER_CASE.test(passwordTextFieldVal)
                      && PasswordComplexityNumberRule.UPPER_CASE.test(passwordTextFieldVal)
                      && PasswordComplexityNumberRule.NUMBER.test(passwordTextFieldVal)
                      && PasswordComplexityNumberRule.SPECIAL_CASE.test(passwordTextFieldVal))) {
                   msgbox('1', "Password " + PasswordComplexityNumberDesc[passwordComplexityNum]);
                  return false;
              }
            }
        }
        
    }
    return true;
}

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

//Check Number field
function CheckNumberField(form,fieldName)
{   
    var isValid = true;    

    for(i=0;i<form.elements.length;i++)
    {
        if(form.elements[i].name === fieldName){
            var val = form.elements[i].value;            
            if (isNaN(val)){
                isValid = false;
            }                        
        }
    }    
    if(!isValid)	
    {	   
       document.getElementById('fade').style.display='block';
       document.getElementById('error-message').style.display='block'; 
       document.getElementById('error-text').innerHTML ='Number Field Is Invalid';
       setTimeout(function(){ 
           document.getElementById('error-message').style.display='none';
           document.getElementById('fade').style.display='none';
       }, 5000);

       return false;
    }
    return true;

}//End of number field checking

//Check Date field
function CheckDateField(form,fieldName)
{   
    var isValid = true;    

    for(i=0;i<form.elements.length;i++)
    {
        if(form.elements[i].name === fieldName){
            var val = form.elements[i].value;            
            if (!validateDate(val)){
                isValid = false;
            }           
        }
    }    
    if(!isValid)	
    {	   
       document.getElementById('fade').style.display='block';
       document.getElementById('error-message').style.display='block'; 
       document.getElementById('error-text').innerHTML ='Date Field Is Invalid';
       setTimeout(function(){ 
           document.getElementById('error-message').style.display='none';
           document.getElementById('fade').style.display='none';
       }, 5000);

       return false;
    }
    return true;

}//End of date field checking

function validateDate(sDate){    
    var matches = /^(\d{4})-(\d{2})-(\d{2})$/.exec(sDate);   
    if(matches === null) {
        return false;
    }
    var d = matches[3];
    var m = matches[2]-1;
    var y = matches[1];
    var date = new Date(y,m,d);      
    var result = (date.getDate() === eval(d)) && (date.getMonth() === eval(m)) && ((date.getYear() +1900) === eval(y));   
    return result;
}


function goToPage(page) {
                window.location = (baseURL + "/" + page);
}