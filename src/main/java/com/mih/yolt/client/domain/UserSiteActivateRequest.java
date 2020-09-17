package com.mih.yolt.client.domain;

public class UserSiteActivateRequest {

    private String redirectUrl;
    private String loginType;

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public static UserSiteActivateRequest of(String redirectUrl) {
        UserSiteActivateRequest r = new UserSiteActivateRequest();
        r.setLoginType("URL");
        r.setRedirectUrl(redirectUrl);
        return r;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
