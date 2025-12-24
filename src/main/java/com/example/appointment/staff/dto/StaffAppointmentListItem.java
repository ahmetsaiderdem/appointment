package com.example.appointment.staff.dto;

import java.time.LocalDateTime;

public class StaffAppointmentListItem {

    private long id;
    private long customerUserId;
    private long serviceId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String status;

    public StaffAppointmentListItem(long id, long customerUserId, long serviceId,
                                    LocalDateTime startAt, LocalDateTime endAt, String status) {
        this.id = id;
        this.customerUserId = customerUserId;
        this.serviceId = serviceId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status;
    }

    public long getId() { return id; }
    public long getCustomerUserId() { return customerUserId; }
    public long getServiceId() { return serviceId; }
    public LocalDateTime getStartAt() { return startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public String getStatus() { return status; }
}
