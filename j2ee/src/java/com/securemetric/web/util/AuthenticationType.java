/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.web.util;

/**
 *
 * @author deskvdi3
 */
public enum AuthenticationType {
    BASIC_AUTH("1","Basic Authentication"),
    SMS_AUTH("2","SMS Authentication"),
    OTP_AUTH("3","OTP Authentication"),
    QR_AUTH("4","QR Code Authentication"),
    CR_OTP_AUTH("5","CR OTP Authentication"),
    PKI_AUTH("6", "PKI Authentication"),
    QNA_AUTH("7","Q&A Authentication"),
    PUSH_MOBILE_CR_AUTH("8","Push Mobile CR Authentication");

    String code;
    String name;

    private AuthenticationType() {
    }

    private AuthenticationType(String code, String name) {
        this.code = code;
        this.name = name;
    }  

}
