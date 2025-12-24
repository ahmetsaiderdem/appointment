package com.example.appointment.services.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ServiceCreateRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotNull
    @Min(5)
    private Integer durationMinutes;

    private BigDecimal price;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

}
