package com.example.appointment.services.dto;

import java.math.BigDecimal;

public class ServiceResponse {


    private long id;
    private String name;
    private int durationMinutes;
    private BigDecimal price;
    private boolean active;

    public ServiceResponse(long id, String name, int durationMinutes, BigDecimal price, boolean active) {
        this.id = id;
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.active = active;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public int getDurationMinutes() { return durationMinutes; }
    public BigDecimal getPrice() { return price; }
    public boolean isActive() { return active; }
}
