package com.example.appointment.staff;

import com.example.appointment.security.CurrentUser;
import com.example.appointment.staff.dto.AvailabilityItem;
import com.example.appointment.staff.dto.CreateAvailabilityRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff/availability")
public class StaffAvailabilityController {

    private final StaffAvailabilityService service;

    public StaffAvailabilityController(StaffAvailabilityService service){
        this.service=service;


    }

    @GetMapping
    public ResponseEntity<List<AvailabilityItem>> listMine(){
        long userId= CurrentUser.id();
        return ResponseEntity.ok(service.myAvailability(userId));

    }

    @PostMapping
    public ResponseEntity<?> add(@Valid @RequestBody CreateAvailabilityRequest req){
        long userId=CurrentUser.id();
        long id= service.add(userId,req);
        return ResponseEntity.ok(Map.of("id",id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id){
        long userId=CurrentUser.id();
        service.delete(userId,id);
        return ResponseEntity.ok().build();
    }
}
