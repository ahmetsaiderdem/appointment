package com.example.appointment.services;


import com.example.appointment.services.dto.ServiceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService service;

    public ServiceController(ServiceService service){
        this.service=service;
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponse>> listActive(){
        return ResponseEntity.ok(service.listActive());
    }
}
