/*
 * To change this license header;  choose License Headers in Project Properties.
 * To change this template file;  choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.web.object;

/**
 *
 * @author User
 */
public class GroupPolicy {

    public int pwdValidity;
    public int pwdHistory;
    public int pwdLength;
    public int pwdGap;
    public int pwdAllowRepeatChar = 1;  //Set as ALLOW if no value get from database
    public int pwdAllowEqualUsername = 1;   //Set as ALLOW if no value get from database 
    public int pwdComplex;
    public int allowedLoginAttemp;
    public int lockAffective;
    public int timeOut;
    public int dormant;
    public int numOfQuestions;
    public int numOfQuestionsToAnswer;
    public int trustLevel;
    public int defaultGroup;
    public int adAuth;

    public int stepUpOption;
    public int status;
    public int userPerSession;


    public String groupName;
    public String groupId;
    public String updatedBy;
    public String pwdBlackListed;
    public String authOpt;
    public String authOptInText;
    public String ruleBase;
    public String ruleBaseTrans;
    public String adAppId;

    private Integer mfaOption;
    
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public int getPwdValidity() {
        return pwdValidity;
    }

    public void setPwdValidity(int pwdValidity) {
        this.pwdValidity = pwdValidity;
    }

    public int getPwdHistory() {
        return pwdHistory;
    }

    public void setPwdHistory(int pwdHistory) {
        this.pwdHistory = pwdHistory;
    }

    public int getPwdLength() {
        return pwdLength;
    }

    public void setPwdLength(int pwdLength) {
        this.pwdLength = pwdLength;
    }

    public int getPwdGap() {
        return pwdGap;
    }

    public void setPwdGap(int pwdGap) {
        this.pwdGap = pwdGap;
    }

    public int getPwdAllowRepeatChar() {
        return pwdAllowRepeatChar;
    }

    public void setPwdAllowRepeatChar(int pwdAllowRepeatChar) {
        this.pwdAllowRepeatChar = pwdAllowRepeatChar;
    }

    public int getPwdAllowEqualUsername() {
        return pwdAllowEqualUsername;
    }

    public void setPwdAllowEqualUsername(int pwdAllowEqualUsername) {
        this.pwdAllowEqualUsername = pwdAllowEqualUsername;
    }

    public int getPwdComplex() {
        return pwdComplex;
    }

    public void setPwdComplex(int pwdComplex) {
        this.pwdComplex = pwdComplex;
    }

    public String getPwdBlackListed() {
        return pwdBlackListed;
    }

    public void setPwdBlackListed(String pwdBlackListed) {
        this.pwdBlackListed = pwdBlackListed;
    }

    public int getAllowedLoginAttemp() {
        return allowedLoginAttemp;
    }

    public void setAllowedLoginAttemp(int allowedLoginAttemp) {
        this.allowedLoginAttemp = allowedLoginAttemp;
    }

    public int getLockAffective() {
        return lockAffective;
    }

    public void setLockAffective(int lockAffective) {
        this.lockAffective = lockAffective;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public int getDormant() {
        return dormant;
    }

    public void setDormant(int dormant) {
        this.dormant = dormant;
    }

    public String getAuthOpt() {
        return authOpt;
    }

    public void setAuthOpt(String authOpt) {
        this.authOpt = authOpt;
    }

    public void setAuthOptInText(String authOptInText) {
        this.authOptInText = authOptInText;
    }

    public String getRuleBase() {
        return ruleBase;
    }

    public void setRuleBase(String ruleBase) {
        this.ruleBase = ruleBase;
    }
    
    public String getRuleBaseTrans() {
        return ruleBaseTrans;
    }

    public void setRuleBaseTrans(String ruleBaseTrans) {
        this.ruleBaseTrans = ruleBaseTrans;
    }

    public int getTrustLevel() {
        return trustLevel;
    }

    public void setTrustLevel(int trustLevel) {
        this.trustLevel = trustLevel;
    }

    public int getStepUpOption() {
        return stepUpOption;
    }

    public void setStepUpOption(int stepUpOption) {
        this.stepUpOption = stepUpOption;
    }

    public int getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(int defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    public int getAdAuth() {
        return adAuth;
    }

    public void setAdAuth(int adAuth) {
        this.adAuth = adAuth;
    }

    public String getAdAppId() {
        return adAppId;
    }

    public void setAdAppId(String adAppId) {
        this.adAppId = adAppId;
    }

    public Integer getMfaOption() {
        return mfaOption;
    }

    public void setMfaOption(Integer mfaOption) {
        this.mfaOption = mfaOption;
    }

    public int getNumOfQuestions() {
        return this.numOfQuestions;
    }

    public void setNumOfQuestions(int numOfQuestions) {
        this.numOfQuestions = numOfQuestions;
    }

    public int getNumOfQuestionsToAnswer() {
        return numOfQuestionsToAnswer;
    }

    public void setNumOfQuestionsToAnswer(int numOfQuestionsToAnswer) {
        this.numOfQuestionsToAnswer = numOfQuestionsToAnswer;
    }
    
    
    public int getUserPerSession() {
        return userPerSession;
    }

    public void setUserPerSession(int userPerSession) {
        this.userPerSession = userPerSession;
    }
}
