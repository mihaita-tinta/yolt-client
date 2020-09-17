package com.mih.yolt.client.domain;

public class User {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "YoltUser{" +
                "id='" + id + '\'' +
                '}';
    }

    public static User of(String id) {
        User user = new User();
        user.id = id;
        return user;
    }
}
