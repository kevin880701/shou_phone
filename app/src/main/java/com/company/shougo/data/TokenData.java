package com.company.shougo.data;

public class TokenData {

    private String token;
    private int expires_sec;
    private String refresh_token;
    private long getTimeZone;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpires_sec() {
        return expires_sec;
    }

    public void setExpires_sec(int expires_sec) {
        this.expires_sec = expires_sec;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public long getGetTimeZone() {
        return getTimeZone;
    }

    public void setGetTimeZone(long getTimeZone) {
        this.getTimeZone = getTimeZone;
    }
}
