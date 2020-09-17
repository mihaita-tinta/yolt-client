package com.mih.yolt.client.domain;

public class RedirectResponse {

    private String userSiteId;
    private RedirectUrl redirect;

    public String getUserSiteId() {
        return userSiteId;
    }

    public void setUserSiteId(String userSiteId) {
        this.userSiteId = userSiteId;
    }

    public RedirectUrl getRedirect() {
        return redirect;
    }

    public void setRedirect(RedirectUrl redirect) {
        this.redirect = redirect;
    }

    @Override
    public String toString() {
        return "YoltUserSite{" +
                "userSiteId='" + userSiteId + '\'' +
                ", redirect=" + redirect +
                '}';
    }
}
