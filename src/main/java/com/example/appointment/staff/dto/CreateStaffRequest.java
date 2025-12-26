package com.example.appointment.staff.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateStaffRequest {

    @NotNull
    private Long userId; // mevcut user'ı staff yapacağız

    @Size(max = 120)
    private String title; // ör: "Berber", "Masaj Terapisti"

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}

