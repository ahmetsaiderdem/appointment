package com.example.appointment.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

    private CurrentUser(){}

    public static long id(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth==null || auth.getPrincipal()==null){
            throw new IllegalStateException("Authentication bulunamadı");

        }

        return Long.parseLong(auth.getPrincipal().toString());

    }
}
