
$(document).ready(function() {
    initNotification();
});

function initNotification(){
    hideAllNotification();
    
    $('#error_resultbox').children('div').click(function(e) {  
        $('#error_resultbox').fadeOut();
    });
        
    $('#notice_resultbox').children('div').click(function(e) {  
        $('#notice_resultbox').fadeOut();
    });
        
    $('#info_resultbox').children('div').click(function(e) {  
        $('#info_resultbox').fadeOut();
    });
        
    $('#success_resultbox').children('div').click(function(e) {  
        $('#success_resultbox').fadeOut();
    });
    
}

function hideAllNotification(){
    $('#error_resultbox').hide();
    $('#notice_resultbox').hide();
    $('#info_resultbox').hide();
    $('#success_resultbox').hide();
}

function showNotification(mode, message){
    hideAllNotification();
    if (mode==1){
        $('#error_resultbox').children('p').html(message);
        $('#error_resultbox').fadeIn("fast","swing",null);        
        $('#error_resultbox').css({
            position: "fixed", 
            top: "0",            
            width: "550px",
            "z-index": "1"
        });
    }else if (mode==2){
        $('#notice_resultbox').children('p').html(message);
        $('#notice_resultbox').fadeIn("fast","swing",null);
        $('#notice_resultbox').css({
            position: "fixed", 
            top: "0",            
            width: "550px",
            "z-index": "1"
        });
    }else if (mode==3){
        $('#info_resultbox').children('p').html(message);
        $('#info_resultbox').fadeIn("fast","swing",null);
        $('#info_resultbox').css({
            position: "fixed", 
            top: "0",            
            width: "550px",
            "z-index": "1"
        });
    }else if (mode==4){
        $('#success_resultbox').children('p').html(message);
        $('#success_resultbox').fadeIn("fast","swing",null);
        $('#success_resultbox').css({
            position: "fixed", 
            top: "0",            
            width: "550px",
            "z-index": "1"
        });
    }else if (mode==5){
        $('#error_resultbox').children('p').html(message);
        $('#error_resultbox').fadeIn("fast","swing",null);        
        $('#error_resultbox').css({
            position: "fixed", 
            top: "0",            
            width: "550px",
            "z-index": "1"
        });
      }else if (mode==6){
        $('#success_resultbox').children('p').html(message);
        $('#success_resultbox').fadeIn("fast","swing",null);        
        $('#success_resultbox').css({
            position: "fixed", 
            top: "0",            
            width: "550px",
            "z-index": "1"
        });
    }
    
    setTimeout(function() {
       hideAllNotification();
    }, 5000)
    
}