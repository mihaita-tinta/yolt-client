package com.mih.yolt.client.domain;

import java.util.Date;

public class UserSite {

    private String connectionStatus;
    private String id;
    private Date lastDataFetchTime;
    private String lastDataFetchFailureReason;

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getLastDataFetchTime() {
        return lastDataFetchTime;
    }

    public void setLastDataFetchTime(Date lastDataFetchTime) {
        this.lastDataFetchTime = lastDataFetchTime;
    }

    public String getLastDataFetchFailureReason() {
        return lastDataFetchFailureReason;
    }

    public void setLastDataFetchFailureReason(String lastDataFetchFailureReason) {
        this.lastDataFetchFailureReason = lastDataFetchFailureReason;
    }

    @Override
    public String toString() {
        return "UserSite{" +
                "connectionStatus='" + connectionStatus + '\'' +
                ", id='" + id + '\'' +
                ", lastDataFetchTime=" + lastDataFetchTime +
                ", lastDataFetchFailureReason='" + lastDataFetchFailureReason + '\'' +
                '}';
    }
}
