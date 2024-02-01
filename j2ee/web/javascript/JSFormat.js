  
var dateFormatPattern	= 'yyyy-MM-dd';			// temporary for malaysia testing

var today = new Date();
var day = today.getDate(), month = today.getMonth(), whichOne = 0;

function formatDate(field)
{ 
  if (field =="")
  {    
    return field;
  }
  
  var First,Second,Third=0;
  
  var DD = new String();
  var MM = new String();
  var YY = new String();
  
  var DAY_value = new String();
  var MONTH_value = new String();
  var YEAR_value = new String();
  var SPT; 
  
  DD=field;
  SPT_CNT = 0
  
  if (DD.indexOf("/")>0)
  {
     SPT="/";
     SPT_CNT += 1
  }
  
  if (DD.indexOf("-")>0)
  {
     SPT="-";
     SPT_CNT += 1
  }
  
  if (DD.indexOf(" ")>0)
  {
     SPT=" ";
     SPT_CNT += 1
  }  
     
  if (DD.indexOf(".")>0)
  {
     SPT=".";
     SPT_CNT += 1
  }         
  
  if(SPT_CNT > 1)
  { return "";}
  
  TMP = new String(field);
  DateArr = TMP.split(SPT);
  if(DateArr.length==3)
  {  
  	  DAY_value   = DateArr[2];
  	  MONTH_value = DateArr[1];
  	  YEAR_value  = DateArr[0];
  }
  else
   if(DateArr.length==2)
   {  
  	   DAY_value   = DateArr[1];
  	   MONTH_value = DateArr[0];
  	   YEAR_value  = "CY";
   }
  else if (SPT_CNT == 0 && (field.length == 2||field.length==1))
  {
  	   DAY_value   = field.substring(0,2);
  	   MONTH_value = "CM";
  	   YEAR_value  = "CY";
  }
  else if (SPT_CNT == 0 && field.length == 4)
  {
  	   DAY_value   = field.substring(2,4);
  	   MONTH_value = field.substring(0,2);
  	   YEAR_value  = "CY";
  }
  else if (SPT_CNT == 0 && field.length == 6)
  {
  	   DAY_value   = field.substring(4,6);
  	   MONTH_value = field.substring(2,4);
  	   YEAR_value  = field.substring(0,2);
  }
  else if (SPT_CNT == 0 && field.length == 8)
  {
  	   DAY_value   = field.substring(6,8);
  	   MONTH_value = field.substring(4,6);
  	   YEAR_value  = field.substring(0,4);
  }
  else
  { return "";}  
  
  final_Date=Date_Check(DAY_value,MONTH_value,YEAR_value,field);   
  
  return final_Date; 
    
 }//formatDate(field)
 
function Date_Check(D,M,Y,field)
{  
   var MyDate = new Date();  
   var Dd=Math.floor(D);
   var Mm=Math.floor(M);     
   var Yy=Math.floor(Y);   
   var CurYear = new String(MyDate.getFullYear()); //Get Current Year
   var CurMonth = new String(MyDate.getMonth()+1); //Get Current month
   
   var LEAP=false;
      
   //Check Year   
   if (Yy<10000)
   { 
     if(Y=="CY")
     {
       Yy=CurYear;
     }
     else
	      if(Yy<10)
	      {Yy=CurYear.substring(0,3)+Yy;}
	      else
	       if((Yy<100)&&(Yy>=10))         
	       {Yy=CurYear.substring(0,2)+Yy;}  
	       else
	       	if((Yy<1000)&&(Yy>=100))
	         {Yy=CurYear;}
   }   
   else
   { Yy=CurYear;}                   
   
   //Check Leap Year
   
   if ((Yy%4)==0)
   {
     LEAP=true;   
   } 
   
   if(M == 'CM')
   {
      Mm = CurMonth;
   }
   //Check Month And Day
   if ((Mm<13)&& (Mm>0))
   { 
        if((Dd>31)||(Dd<=0)||(!parseInt(Dd)))
        {        
           field="";         
           return field; 
        }
        //Check Day with the Specificed Month
        switch(parseInt(Mm))
        { 
          
          case 2: if(Dd>29)
                  { 
                      field="";                    
                      return field; 
                  }                   
                 else
                  if((Dd==29)&&(LEAP==false))
                  { 
                      field="";                    
                      return field; 
                  }                
                 break;  
          case 4 :if(Dd==31)
                  { 
                      field="";                      
                      return field; 
                  }                   
                  break;  
          case 6 :if(Dd==31)
                  { 
                      field="";                      
                      return field; 
                  }                   
                  break;  
          case 9 :if(Dd==31)
                  { 
                      field="";                      
                      return field; 
                  }                   
                  break;  
          case 11 :if(Dd==31)
                   { 
                      field="";                     
                      return field; 
                   }                   
                  break;                                           
          default:break;         
        }//End of Switch
   }
   else
   { field ="";   
     return field;
   }   
      
      
   if(Dd<10)
   {Dd="0"+Dd;}
   
   if(Mm<10)
   {Mm="0"+Mm;}
    
   //return field=Dd+"/"+Mm+"/"+Yy;
   return field=Yy+"-"+Mm+"-"+Dd;    
    
}//Date_Check() 
