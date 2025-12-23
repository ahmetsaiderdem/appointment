package com.example.appointment.config;

public class AppJwtProperties {

    private String secret;

    private int accessTokenMinutes=60;

    public String getSecret(){
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setAccessTokenMinutes(int accessTokenMinutes) {
        this.accessTokenMinutes = accessTokenMinutes;
    }

    public int getAccessTokenMinutes() {
        return accessTokenMinutes;
    }
}
