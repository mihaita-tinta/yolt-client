package com.mih.yolt.client.domain;

public class Site {

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "YoltSite{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                '}';
    }

    public static Site of(String id) {
        Site site = new Site();
        site.id = id;
        return site;
    }
}
