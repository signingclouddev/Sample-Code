var menuDashboard;
var menuSelfService;
var menuCompanies;
var menuGroup;
var menuRules;
var menuAdministrators;
var menuActivities;
var menuTokens;
var menuCertificates;
var menuSetting;
var menuStatus;
var sessionTimeout;
var autoLogoutTimer;

//API Page
var menuGettingStarted
var menuWebApi
var menuSaml

var menuNotification;
var sessionTimeout ;
var autoLogoutTimer ;

var ar = new Array(33, 34, 35, 36, 38, 40);

$(document).ajaxSuccess(function (event, request, settings)
{
    if (settings.url.indexOf("refreshtoken") === -1 && sessionTimeout > 0)
    {
        clearTimeout(autoLogoutTimer);
        autoLogoutTimer = setTimeout(function ( )
        {
            $("#logout_link").attr("href", "logout?msg=Session%20timeout.")
            $("#logout_link") [ 0 ].click( );
        }, sessionTimeout);
    }
});

$(document).ready(function () {
    initSubMenu();
    subMenuHideAll("");

    $("#menuDashboard").hover(function () {
        clearTimeout(menuDashboard);
        subMenuAddHover("menuDashboard");
    }, function () {
        menuDashboard = setTimeout(function () {
            subMenuRemoveHover("menuDashboard");
        }, 10);
    });

    $("#menuGettingStarted").hover(function () {
        clearTimeout(menuGettingStarted);
        subMenuAddHover("menuGettingStarted");
    }, function () {
        menuGettingStarted = setTimeout(function () {
            subMenuRemoveHover("menuGettingStarted");
        }, 10);
    });

    $("#menuWebApi").hover(function () {
        clearTimeout(menuWebApi);
        subMenuAddHover("menuWebApi");
    }, function () {
        menuWebApi = setTimeout(function () {
            subMenuRemoveHover("menuWebApi");
        }, 10);
    });

    $("#menuSaml").hover(function () {
        clearTimeout(menuWebApi);
        subMenuAddHover("menuSaml");
    }, function () {
        menuWebApi = setTimeout(function () {
            subMenuRemoveHover("menuSaml");
        }, 10);
    });


    $("#menuSelfService").hover(function () {
        subMenuHideAll("ulSelfService");
        clearTimeout(menuSelfService);
        subMenuAddHover("menuSelfService");
        $('#ulSelfService').fadeIn();
    }, function () {
        menuSelfService = setTimeout(function () {
            $('#ulSelfService').fadeOut();
            subMenuRemoveHover("menuSelfService");
        }, 1000);
    });

    $("#menuCompanies").hover(function () {
        subMenuHideAll("ulCompanies");
        clearTimeout(menuCompanies);
        subMenuAddHover("menuCompanies");
        $('#ulCompanies').fadeIn();
    }, function () {
        menuCompanies = setTimeout(function () {
            $('#ulCompanies').fadeOut();
            subMenuRemoveHover("menuCompanies");
        }, 1000);
    });

    $("#menuGroup").hover(function () {
        subMenuHideAll("ulGroup");
        clearTimeout(menuCompanies);
        subMenuAddHover("menuGroup");
        $('#ulGroup').fadeIn();
    }, function () {
        menuCompanies = setTimeout(function () {
            $('#ulGroup').fadeOut();
            subMenuRemoveHover("menuGroup");
        }, 1000);
    });

    $("#menuRules").hover(function () {
        subMenuHideAll("ulRules");
        clearTimeout(menuCompanies);
        subMenuAddHover("menuRules");
        $('#ulRules').fadeIn();
    }, function () {
        menuCompanies = setTimeout(function () {
            $('#ulRules').fadeOut();
            subMenuRemoveHover("menuRules");
        }, 1000);
    });

    $("#menuAdministrators").hover(function () {
        subMenuHideAll("ulAdministrators");
        clearTimeout(menuAdministrators);
        subMenuAddHover("menuAdministrators");
        $('#ulAdministrators').fadeIn();
    }, function () {
        menuAdministrators = setTimeout(function () {
            $('#ulAdministrators').fadeOut();
            subMenuRemoveHover("menuAdministrators");
        }, 1000);
    });

    $("#menuActivities").hover(function () {
        subMenuHideAll("ulActivities");
        clearTimeout(menuActivities);
        subMenuAddHover("menuActivities");
        $('#ulActivities').fadeIn();
    }, function () {
        menuActivities = setTimeout(function () {
            $('#ulActivities').fadeOut();
            subMenuRemoveHover("menuActivities");
        }, 1000);
    });

    $("#menuTokens").hover(function () {
        subMenuHideAll("ulTokens");
        clearTimeout(menuTokens);
        subMenuAddHover("menuTokens");
        $('#ulTokens').fadeIn();
    }, function () {
        menuTokens = setTimeout(function () {
            $('#ulTokens').fadeOut();
            subMenuRemoveHover("menuTokens");
        }, 1000);
    });

    $("#menuCertificates").hover(function () {
        if ($('#ulCertificates').length > 0)
            subMenuHideAll("ulCertificates");
        else
            subMenuHideAll("ulCertificatesShort");

        clearTimeout(menuCertificates);
        subMenuAddHover("menuCertificates");

        if ($('#ulCertificates').length > 0)
            $('#ulCertificates').fadeIn();
        else
            $('#ulCertificatesShort').fadeIn();
    }, function () {
        menuCertificates = setTimeout(function () {
            if ($('#ulCertificates').length > 0)
                $('#ulCertificates').fadeOut();
            else
                $('#ulCertificatesShort').fadeOut();
            subMenuRemoveHover("menuCertificates");
        }, 1000);
    });

    $("#menuSetting").hover(function () {
        if ($('#ulSetting').length > 0)
            subMenuHideAll("ulSetting");
        else
            subMenuHideAll("ulSettingShort");

        clearTimeout(menuSetting);
        subMenuAddHover("menuSetting");

        if ($('#ulSetting').length > 0)
            $('#ulSetting').fadeIn();
        else
            $('#ulSettingShort').fadeIn();
    }, function () {
        menuSetting = setTimeout(function () {
            if ($('#ulSetting').length > 0)
                $('#ulSetting').fadeOut();
            else
                $('#ulSettingShort').fadeOut();

            subMenuRemoveHover("menuSetting");
        }, 1000);
    });

    $("#menuPlans").hover(function () {
        subMenuHideAll("ulPlans");
        clearTimeout(menuSetting);
        subMenuAddHover("menuPlans");
        $('#ulPlans').fadeIn();
    }, function () {
        menuSetting = setTimeout(function () {
            $('#ulPlans').fadeOut();
            subMenuRemoveHover("menuPlans");
        }, 1000);
    });

    $("#menuStatus").hover(function () {
        subMenuHideAll("ulStatus");
        clearTimeout(menuStatus);
        subMenuAddHover("menuStatus");
        $('#ulStatus').fadeIn();
    }, function () {
        menuStatus = setTimeout(function () {
            $('#ulStatus').fadeOut();
            subMenuRemoveHover("menuStatus");
        }, 1000);
    });
    
    $("#menuNotification").hover(function() {
        subMenuHideAll("ulNotification");
        clearTimeout(menuNotification);
        subMenuAddHover("menuNotification");
        $('#ulNotification').fadeIn();
    }, function() {
    	menuNotification = setTimeout(function() {
            $('#ulNotification').fadeOut();
            subMenuRemoveHover("menuNotification");
        }, 1000);
    });

    $("#menuWebApi").hover(function () {
        subMenuHideAll("ulWebApi");
        clearTimeout(menuWebApi);
        subMenuAddHover("menuWebApi");
        $('#ulWebApi').fadeIn();
    }, function () {
        menuStatus = setTimeout(function () {
            $('#ulWebApi').fadeOut();
            subMenuRemoveHover("menuWebApi");
        }, 1000);
    });

    $('#error_confirmation_link').click(function () {
        showNotification('1', 'this is custom message');
    });
    $('#notice_confirmation_link').click(function () {
        showNotification('2', 'this is notification custom message');
    });
    $('#info_confirmation_link').click(function () {
        showNotification('3', 'this is info custom message');
    });
    $('#success_confirmation_link').click(function () {
        showNotification('4', 'Add user success.');
    });

    $('#close_sidebar').click(function () {
        close_sidebar();

        $("#leftpanel").css("width", "96%");

        $(".content-box").css("width", "96%");

        $(".inner-page-title").css("width", "98%");

        var sidebarHeight = $("#sidebar").height();

        $("#page-content-wrapper").css({"minHeight": sidebarHeight});

        $(this).addClass("active");

        $("#sidebar").css("position", "static");
    });

    $('#open_sidebar').click(function () {
        open_sidebar();
        $("#leftpanel").css("width", "720px");
        $(".content-box").css("width", "98%");
        $(".inner-page-title").css("width", "100%");
        var sidebarHeight = $("#sidebar").height();
        $("#sidebar").css("position", "relative");
        $("#page-content-wrapper").css({"minHeight": sidebarHeight});
    });


    /* Dialog box setup */
    $(function () {
        $("#input_empty_dialog").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "OK": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#fido_authorise_dialog").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "OK": function () {
                    $(this).dialog("close");
                }
            }
        });
    });
    $(function () {
        $("#modal_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Delete": function () {
                    $(this).dialog("close");
                },
                Cancel: function () {
                    $(this).dialog("close");
                }
            }
        });
    });
    $(function () {
        $("#com_save_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preRegisterCompany();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#com_update_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preUpdateCompany();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#com_validateinput_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    if (!$(".ui-dialog").is(":visible")) {
                        $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    }
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {

            },
            close: function (event, ui) {
                if (!$(".ui-dialog").is(":visible")) {
                    if (hasScrollBar()) {
                        $("html").css({'margin-left': '0px', overflow: 'auto'});
                        $(document).unbind("keydown");
                    } else {
                        $("html").css({overflow: 'auto'});
                    }
                }

            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Ok": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#Invalid_input_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Ok": function () {
                    $(this).dialog("close");
                    //focus the text
                    var focusId = $("#Invalid_input_confirmation").data('focusId');
                    setTimeout(function () {
                        $("#" + focusId).focus();
                    }, 1);
                }
            }
        });

        $("#grp_save_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preAddGroup();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#grp_update_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preUpdateGroup();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#user_save_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preAddUser();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#user_update_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preUpdateUser();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#selfservice_save_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    changePassword();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#selfservice_save_confirmation_password").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    changePassword();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $('#plan_delete_confirmation').dialog({
            open: function (event, ui) {
                $("body").css({'margin-left': '-17px', overflow: 'hidden'});
            },
            beforeClose: function (event, ui) {
                $("body").css({'margin-left': 'inherit', overflow: 'inherit'});
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    planDelete();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#plan_add_confirmation").dialog({
            open: function (event, ui) {
                $("body").css({'margin-left': '-17px', overflow: 'hidden'});
            },
            beforeClose: function (event, ui) {
                $("body").css({'margin-left': 'inherit', overflow: 'inherit'});
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");

                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });



        $("#grp_delete_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    groupDelete();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#lic_update_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    $('#updateForm').submit();
                },
                "No": function () {
                    $(this).dialog("close");

                }
            }
        });

        $("#tkn_code_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Request Code": function () {
                    $(this).dialog("close");
                    preReqActCode();

                },
                Cancel: function () {
                    $(this).dialog("close");
                }
            }
        }),
                $("#tkn_act_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Activate": function () {
                    $(this).dialog("close");
                    preActCode();
                },
                Cancel: function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#tkn_unreg_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Unregister Token": function () {
                    $(this).dialog("close");
                    preUnregisterToken();

                },
                Cancel: function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#tkn_mobile_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Add Mobile Token": function () {
                    $(this).dialog("close");
                    preAddMobileToken();

                },
                Cancel: function () {
                    $(this).dialog("close");
                }
            }
        });


        $("#tkn_reg_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Register Token": function () {
                    $(this).dialog("close");
                    preRegisterToken();

                },
                Cancel: function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#tkn_update_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Update": function () {
                    $(this).dialog("close");
                    preUpdateToken();
                },
                Cancel: function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#tkn_unlock_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Update": function () {
                    $(this).dialog("close");
                    preUnlockToken();
                },
                Cancel: function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#tkn_sync_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preSyncToken();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });


        $("#tkn_defmob_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preDefaultMobile();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#app_save_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preAddApp();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#app_update_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preUpdateApp();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });
        $("#app_delete_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preDeleteApp();

                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#tkn_sync_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preSyncToken();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });



        $("#tkn_userunreg_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preUserUnregisterToken();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#user_editprofile_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preEditUser();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });
        $("#cert_unreg_inreg_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preUnregisterCertInRegister();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#template_save_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preSaveTemplate();

                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#dev_unreg_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preUnregisterDevice();

                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        }),
                $("#rules_del_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preDeleteRules();

                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        }),
                $("#smtp_save_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preSaveSmtp();

                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });



        $("#searchPopup").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false
        });


        $("#searchUserPopup").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            width: "450",
            modal: true,
            autoOpen: false,
            draggable: false
        });


        $("#tokenDetails").dialog({
            maxHeight: $(window).height() * 0.9,
            open: function (event, ui) {
                if (hasScrollBar()) {
                    // $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            width: "500",
            modal: true,
            autoOpen: false,
            draggable: false
        });


        $("#deviceDetails").dialog({
            maxHeight: $(window).height() * 0.9,
            open: function (event, ui) {
                if (hasScrollBar()) {
                    // $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            width: "500",
            modal: true,
            autoOpen: false,
            draggable: false,
        });

        $("#cont_save_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preAddCont();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });
        $("#cont_update_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preUpdateCont();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });
        $("#cont_delete_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preDeleteCont();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#trust_save_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preSaveTrust();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#smsgateway_save_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preSaveSmsGateway();

                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });


        $("#device_update_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    if (!$(".ui-dialog").is(":visible")) {
                        $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    }
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if ($('#deviceDetails').dialog('isOpen')) {
                } else {
                    if (hasScrollBar()) {
                        $("html").css({'margin-left': '0px', overflow: 'auto'});
                        $(document).unbind("keydown");
                    } else {
                        $("html").css({overflow: 'auto'});
                    }
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preUpdateDevice();
                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#secimage_save_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preSaveImage();

                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#question_save_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");
                    preSaveQuestion();

                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });



        $("#secquest_reset_confirmation").dialog({
            open: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '-17px', overflow: 'hidden'});
                    $(document).keydown(function (e) {
                        var key = e.which;
                        //console.log(key);
                        //if(key==35 || key == 36 || key == 37 || key == 39)
                        if ($.inArray(key, ar) > -1) {
                            e.preventDefault();
                            return false;
                        }
                        return true;
                    });
                } else {
                    $("html").css({overflow: 'hidden'});
                }
            },
            beforeClose: function (event, ui) {
                if (hasScrollBar()) {
                    $("html").css({'margin-left': '0px', overflow: 'auto'});
                    $(document).unbind("keydown");
                } else {
                    $("html").css({overflow: 'auto'});
                }
            },
            resizable: false,
            height: "auto",
            modal: true,
            autoOpen: false,
            draggable: false,
            buttons: {
                "Yes": function () {
                    $(this).dialog("close");

                    preResetSecQuest();

                },
                "No": function () {
                    $(this).dialog("close");
                }
            }
        });


    });
});

function subMenuHideAll(id) {
    if (id !== "ulSelfService")
        $('#ulSelfService').hide();
    if (id !== "ulCompanies")
        $('#ulCompanies').hide();
    if (id !== "ulGroup")
        $('#ulGroup').hide();
    if (id !== "ulRules")
        $('#ulRules').hide();
    if (id !== "ulAdministrators")
        $('#ulAdministrators').hide();
    if (id !== "ulActivities")
        $('#ulActivities').hide();
    if (id !== "ulTokens")
        $('#ulTokens').hide();
    if (id !== "ulCertificates")
        $('#ulCertificates').hide();
    if (id !== "ulCertificatesShort")
        $('#ulCertificatesShort').hide();
    if (id !== "ulSetting")
        $('#ulSetting').hide();
    if (id !== "ulSettingShort")
        $('#ulSettingShort').hide();
    if (id !== "ulLog")
        $('#ulLog').hide();
    if (id !== "ulStatus")
        $('#ulStatus').hide();
    if (id !== "ulNotification")
        $('#ulNotification').hide();
    if (id !== "ulWebApi")
        $('#ulWebApi').hide();
}
function subMenuRemoveAllHover() {
    $('#menuDashboard').removeClass("hover");
    $('#menuDashboard').children('a').removeClass("hover");

    $('#menuSelfService').removeClass("hover");
    $('#menuSelfService').children('a').removeClass("hover");

    $('#menuCompanies').removeClass("hover");
    $('#menuCompanies').children('a').removeClass("hover");

    $('#menuGroup').removeClass("hover");
    $('#menuGroup').children('a').removeClass("hover");

    $('#menuRules').removeClass("hover");
    $('#menuRules').children('a').removeClass("hover");

    $('#menuAdministrators').removeClass("hover");
    $('#menuAdministrators').children('a').removeClass("hover");

    $('#menuActivities').removeClass("hover");
    $('#menuActivities').children('a').removeClass("hover");

    $('#menuDevices').removeClass("hover");
    $('#menuDevices').children('a').removeClass("hover");

    $('#menuTokens').removeClass("hover");
    $('#menuTokens').children('a').removeClass("hover");

    $('#menuCertificates').removeClass("hover");
    $('#menuCertificates').children('a').removeClass("hover");

    $('#menuSetting').removeClass("hover");
    $('#menuSetting').children('a').removeClass("hover");

    $('#menuStatus').removeClass("hover");
    $('#menuStatus').children('a').removeClass("hover");
    
    $('#menuNotification').removeClass("hover");
    $('#menuNotification').children('a').removeClass("hover");

    //API page
    $('#menuGettingStarted').removeClass("hover");
    $('#menuGettingStarted').children('a').removeClass("hover");

    $('#menuWebApi').removeClass("hover");
    $('#menuWebApi').children('a').removeClass("hover");

    $('#menuSaml').removeClass("hover");
    $('#menuSaml').children('a').removeClass("hover");

}
function subMenuRemoveHover(id) {
    $('#' + id).removeClass("hover");
    $('#' + id).children('a').removeClass("hover");
}
function subMenuAddHover(id) {
    subMenuRemoveAllHover();
    $('#' + id).addClass("hover");
    $('#' + id).children('a').addClass("hover");
}
function initSubMenu() {
    $('#ulSelfService').width(680);
    $('#ulCompanies').width(500);
    $('#ulGroup').width(300);
    $('#ulRules').width(550);
    $('#ulAdministrators').width(500);
    $('#ulActivities').width(200);
    $('#ulDevices').width(150);
    $('#ulTokens').width(380);
    $('#ulWebApi').width(350);

    if ($('#ulCertificates').length > 0)
        $('#ulCertificates').width(620);
    else
        $('#ulCertificatesShort').width(320);

    if ($('#ulSetting').length > 0)
        $('#ulSetting').width(920);
    else
        $('#ulSettingShort').width(440);

    $('#ulLog').width(300);
    $('#ulStatus').width(300);
    $('#ulNotification').width(350);
}

// Sidebar close/open 
function close_sidebar() {
    $("#sidebar").addClass('closed-sidebar').animate({"speed": "1500"});
    $("#page_wrapper #page-content #page-content-wrapper #main-panel").addClass("no-bg-image wrapper-full");
    $("#open_sidebar").show();
    $("#close_sidebar, .hide_sidebar").hide();
}

function open_sidebar() {
    $("#sidebar").removeClass('closed-sidebar').animate(1500);
    $("#page_wrapper #page-content #page-content-wrapper #main-panel").removeClass("no-bg-image wrapper-full");
    $("#open_sidebar").hide();
    $("#close_sidebar, .hide_sidebar").show();
}

function hideshowSideMenu(titleid, menuid) {

    if ($('#' + menuid).is(":visible")) {

        $('#' + titleid).removeClass("ui-icon-circle-arrow-s");
        $('#' + titleid).addClass("ui-icon-circle-arrow-n");
        $('#' + menuid).hide();
    } else {
        $('#' + titleid).removeClass("ui-icon-circle-arrow-n");
        $('#' + titleid).addClass("ui-icon-circle-arrow-s");
        $('#' + menuid).show();
    }
}


function navigate(url, param) {
    subMenuHideAll("");

    url = url + "?" + param;
    $.ajax({
        url: url,
        cache: false,
        success: function (data) {
            $("#leftpanel").html(data);
            return false;
        }
    });
}


function navigateOpen(url, param) {
    subMenuHideAll("");

    url = url + "?" + param;
    $.ajax({
        url: url,
        cache: false,
        success: function (data) {
            $("#leftpanel").html(data);
            return false;
        }
    });
}

function navigateDirect(url) {
    subMenuHideAll("");

    $.ajax({
        url: url,
        cache: false,
        success: function (data) {
            $("#leftpanel").html(data);
            return false;
        }
    });
}

function redirect(url)
{
    window.location = url;
}

function msgbox(id, msg) {
    if (id == null || id == "") {
        id = "modal_confirmation";
    }
    $('#' + id).children('p').html(msg);
    $('#' + id).height('170');
    $('#' + id).dialog('open');
}

function msgbox(id, msg, focusId) {
    if (id == null || id == "") {
        id = "modal_confirmation";
    }
    $('#' + id).children('p').html(msg);
    $('#' + id).height('170');

    if (focusId != null) {
        $('#' + id).data('focusId', focusId);
    }

    $('#' + id).dialog('open');
}

function buildPaging(divID, searchUrl, currentPage, totalRecords, maxPerPage, firstString, lastString, resultString) {
    document.getElementById(divID).innerHTML = "";

    if (totalRecords < 0) {
        totalRecords = 0;
    }

    //convert to int
    currentPage = currentPage * 1;

    var totalPage = (totalRecords / maxPerPage >> 0);
    var reminder = totalRecords % maxPerPage;
    if (reminder != 0) {
        totalPage = totalPage + 1;
    }

    if (currentPage <= 0) {
        currentPage = 1;
    }
    if (currentPage > totalPage) {
        currentPage = totalPage;
    }

    var startIndex = currentPage - 3;
    var endIndex = currentPage + 3;

    if (startIndex <= 0) {
        //adjust the missing value to end index        
        endIndex = endIndex - startIndex + 1;
    }
    if (endIndex > totalPage) {
        //adjust the missing value to start index
        startIndex = startIndex - (endIndex - totalPage);
    }

    if (startIndex <= 0) {
        startIndex = 1;
    }
    if (endIndex > totalPage) {
        endIndex = totalPage;
    }

    if (totalRecords == 0) {
        document.getElementById(divID).innerHTML = "<span class=\"total\">(" + totalRecords + "&nbsp;" + resultString + ")</span>";
    } else {
        if (currentPage == 1) {
            document.getElementById(divID).innerHTML = "<ul>\n" +
                    "<li class=\"current\">" + firstString + "</li>\n";
        } else {
            if ((divID === "topUserPaging") || (divID === "bottomUserPaging")) {
                document.getElementById(divID).innerHTML = "<ul>\n" +
                        "<li class=\"previous\"><a href=\"javascript:void(0);\" onclick=\"navigateDirect('" + searchUrl + "&amp;userSearchPage=1')\">" + firstString + "</a></li>\n";
            } else {
                document.getElementById(divID).innerHTML = "<ul>\n" +
                        "<li class=\"previous\"><a href=\"javascript:void(0);\" onclick=\"navigateDirect('" + searchUrl + "&amp;page=1')\">" + firstString + "</a></li>\n";
            }
        }
        for (var i = startIndex; i <= endIndex; i++) {
            if (i == currentPage) {
                document.getElementById(divID).innerHTML += "<li class=\"current\">" + i + "</li>\n";
            } else {
                if ((divID === "topUserPaging") || (divID === "bottomUserPaging")) {

                    document.getElementById(divID).innerHTML += "<li><a href=\"javascript:void(0);\" onclick=\"navigateDirect('" + searchUrl + "&amp;userSearchPage=" + i + "')\">" + i + "</a></li>\n";

                } else {
                    document.getElementById(divID).innerHTML += "<li><a href=\"javascript:void(0);\" onclick=\"navigateDirect('" + searchUrl + "&amp;page=" + i + "')\">" + i + "</a></li>\n";
                }
            }

        }

        if (currentPage == endIndex) {
            document.getElementById(divID).innerHTML += "<li class=\"current\">" + lastString + "</li>\n" +
                    "</ul><span class=\"total\">(" + totalRecords + "&nbsp;" + resultString + ")</span>";
        } else {
            if ((divID === "topUserPaging") || (divID === "bottomUserPaging")) {

                document.getElementById(divID).innerHTML += "<li class=\"next\"><a href=\"javascript:void(0);\" onclick=\"navigateDirect('" + searchUrl + "&amp;userSearchPage=" + totalPage + "')\">" + lastString + "</a></li>\n" +
                        "</ul><span class=\"total\">(" + totalRecords + "&nbsp;" + resultString + ")</span>";

            } else {
                document.getElementById(divID).innerHTML += "<li class=\"next\"><a href=\"javascript:void(0);\" onclick=\"navigateDirect('" + searchUrl + "&amp;page=" + totalPage + "')\">" + lastString + "</a></li>\n" +
                        "</ul><span class=\"total\">(" + totalRecords + "&nbsp;" + resultString + ")</span>";
            }
        }
    }
}

function buildPagingWithRedirect(divID, searchUrl, currentPage, totalRecords, maxPerPage, firstString, lastString, resultString) {
    document.getElementById(divID).innerHTML = "";

    if (totalRecords < 0) {
        totalRecords = 0;
    }

    //convert to int
    currentPage = currentPage * 1;

    var totalPage = (totalRecords / maxPerPage >> 0);
    var reminder = totalRecords % maxPerPage;
    if (reminder != 0) {
        totalPage = totalPage + 1;
    }

    if (currentPage <= 0) {
        currentPage = 1;
    }
    if (currentPage > totalPage) {
        currentPage = totalPage;
    }

    var startIndex = currentPage - 3;
    var endIndex = currentPage + 3;

    if (startIndex <= 0) {
        //adjust the missing value to end index        
        endIndex = endIndex - startIndex + 1;
    }
    if (endIndex > totalPage) {
        //adjust the missing value to start index
        startIndex = startIndex - (endIndex - totalPage);
    }

    if (startIndex <= 0) {
        startIndex = 1;
    }
    if (endIndex > totalPage) {
        endIndex = totalPage;
    }

    if (totalRecords == 0) {
        document.getElementById(divID).innerHTML = "<span class=\"total\">(" + totalRecords + "&nbsp;" + resultString + ")</span>";
    } else {
        if (currentPage == 1) {
            document.getElementById(divID).innerHTML = "<ul>\n" +
                    "<li class=\"current\">" + firstString + "</li>\n";
        } else {
            document.getElementById(divID).innerHTML = "<ul>\n" +
                    "<li class=\"previous\"><a href=\"javascript:void(0);\" onclick=\"redirect('" + searchUrl + "&amp;page=1')\">" + firstString + "</a></li>\n";
        }
        for (var i = startIndex; i <= endIndex; i++) {
            if (i == currentPage) {
                document.getElementById(divID).innerHTML += "<li class=\"current\">" + i + "</li>\n";
            } else {
                document.getElementById(divID).innerHTML += "<li><a href=\"javascript:void(0);\" onclick=\"redirect('" + searchUrl + "&amp;page=" + i + "')\">" + i + "</a></li>\n";
            }

        }

        if (currentPage == endIndex) {
            document.getElementById(divID).innerHTML += "<li class=\"current\">" + lastString + "</li>\n" +
                    "</ul><span class=\"total\">(" + totalRecords + "&nbsp;" + resultString + ")</span>";
        } else {
            document.getElementById(divID).innerHTML += "<li class=\"next\"><a href=\"javascript:void(0);\" onclick=\"redirect('" + searchUrl + "&amp;page=" + totalPage + "')\">" + lastString + "</a></li>\n" +
                    "</ul><span class=\"total\">(" + totalRecords + "&nbsp;" + resultString + ")</span>";
        }
    }
}


function hasScrollBar() {
    // Get the computed style of the body element
//    var cStyle = document.body.currentStyle||window.getComputedStyle(document.body, "");
//
//    // Check the overflow and overflowY properties for "auto" and "visible" values
//    var hasVScroll = cStyle.overflow == "visible" 
    var hasVScroll = $("body").height() > $(window).height();

//                 || cStyle.overflowY == "visible"
//                 || cStyle.overflow == "auto"
//                 || cStyle.overflowY == "auto";
    //alert(hasVScroll);
    return hasVScroll;
}
