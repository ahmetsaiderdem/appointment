package com.example.appointment.staff.dto;

import java.time.LocalTime;

public class AvailabilityItem {

    private long id;
    private int dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean active;

    public AvailabilityItem(long id, int dayOfWeek, LocalTime startTime, LocalTime endTime, boolean active) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.active = active;
    }

    public long getId() { return id; }
    public int getDayOfWeek() { return dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public boolean isActive() { return active; }
}

