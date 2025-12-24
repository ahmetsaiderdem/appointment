package com.example.appointment.services.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ServiceUpdateRequest {

    @Size(max=120)
    private String name;

    @Min(5)
    private Integer durationMinutes;

    private BigDecimal price;

    private Boolean active;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
