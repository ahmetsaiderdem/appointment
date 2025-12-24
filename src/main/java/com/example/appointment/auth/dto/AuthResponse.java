package com.example.appointment.auth.dto;

import java.util.List;

public class AuthResponse {

    private String accessToken;
    private String tokenType="Bearer";
    private long expiresInSeconds;
    private List<String> roles;


    public AuthResponse(String accessToken,long expiresInSeconds,List<String> roles){
        this.accessToken=accessToken;
        this.expiresInSeconds=expiresInSeconds;
        this.roles=roles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public List<String> getRoles() {
        return roles;
    }
}
