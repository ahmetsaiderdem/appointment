package com.example.appointment.appointments.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class CreateAppointmentRequest {

    @NotNull
    private Long staffId;

    @NotNull
    private Long serviceId;

    @NotNull
    private LocalDateTime startAt;

    private String notes;

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
