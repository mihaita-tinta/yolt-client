package com.mih.yolt.client.domain;

public class Account {

    private String id;
    private String accountHolder;
    private UserSite userSite;
    private Double balance;
    private String currency;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public UserSite getUserSite() {
        return userSite;
    }

    public void setUserSite(UserSite userSite) {
        this.userSite = userSite;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "YoltAccount{" +
                "id='" + id + '\'' +
                ", accountHolder='" + accountHolder + '\'' +
                ", userSite=" + userSite +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
