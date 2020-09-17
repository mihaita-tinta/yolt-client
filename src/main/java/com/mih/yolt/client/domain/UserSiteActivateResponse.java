package com.mih.yolt.client.domain;

public class UserSiteActivateResponse {

    private String redirectUrl;
    private String loginType;
    private UserSite userSite;

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public UserSite getUserSite() {
        return userSite;
    }

    public void setUserSite(UserSite userSite) {
        this.userSite = userSite;
    }

    public static UserSiteActivateResponse of(String redirectUrl) {
        UserSiteActivateResponse r = new UserSiteActivateResponse();
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
