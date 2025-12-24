package com.example.appointment.appointments.dto;

import java.time.LocalDateTime;

public class AppointmentListItem {


    private long id;
    private long staffId;
    private long serviceId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String status;

    public AppointmentListItem(long id,long staffId,long serviceId,LocalDateTime startAt,LocalDateTime endAt,String status){
        this.id=id;
        this.staffId=staffId;
        this.serviceId=serviceId;
        this.startAt=startAt;
        this.endAt=endAt;
        this.status=status;
    }

    public long getId() { return id; }
    public long getStaffId() { return staffId; }
    public long getServiceId() { return serviceId; }
    public LocalDateTime getStartAt() { return startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public String getStatus() { return status; }
}
