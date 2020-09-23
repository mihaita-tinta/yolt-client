package com.mih.yolt.client.domain;

public class RedirectUrl {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "YoltLoginUrl{" +
                "url='" + url + '\'' +
                '}';
    }

    public String getState() {
        String[] arr = url.split("=");
        return arr[arr.length - 1];
    }

    public static RedirectUrl of(String url) {
        RedirectUrl redirectUrl = new RedirectUrl();
        redirectUrl.url = url;
        return redirectUrl;
    }
}
