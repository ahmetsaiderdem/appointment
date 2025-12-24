package com.example.appointment.appointments;

import com.example.appointment.appointments.dto.AppointmentListItem;
import com.example.appointment.appointments.dto.AppointmentResponse;
import com.example.appointment.appointments.dto.CreateAppointmentRequest;
import com.example.appointment.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service){
        this.service=service;
    }


    @PostMapping
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody CreateAppointmentRequest req){
        long userId= CurrentUser.id();
        return ResponseEntity.ok(service.create(userId,req));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable("id") long id){
        long userId=CurrentUser.id();
        service.cancel(userId,id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<AppointmentListItem>> myAppointments(){
        long userId=CurrentUser.id();
        return ResponseEntity.ok(service.listForCustomer(userId));
    }



}
