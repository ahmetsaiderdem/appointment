package com.example.appointment.services;

import com.example.appointment.services.dto.ServiceCreateRequest;
import com.example.appointment.services.dto.ServiceUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/services")
public class AdminServiceController {

    private final ServiceService service;

    public AdminServiceController(ServiceService service){
        this.service=service;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ServiceCreateRequest req){
        long id= service.create(req);
        return ResponseEntity.ok(Map.of("id",id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id, @Valid @RequestBody ServiceUpdateRequest req){
        service.update(id,req);
        return ResponseEntity.ok().build();
    }
}
