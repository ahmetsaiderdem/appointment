package com.example.appointment.staff.dto;

public class StaffListItem {

    private long staffId;
    private long userId;
    private String fullName;
    private String email;
    private String title;
    private boolean active;

    public StaffListItem(long staffId, long userId, String fullName, String email, String title, boolean active) {
        this.staffId = staffId;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.title = title;
        this.active = active;
    }

    public long getStaffId() { return staffId; }
    public long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getTitle() { return title; }
    public boolean isActive() { return active; }
}

