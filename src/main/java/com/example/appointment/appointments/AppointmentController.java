package com.example.appointment.appointments;

import com.example.appointment.appointments.dto.AppointmentListItem;
import com.example.appointment.appointments.dto.AppointmentResponse;
import com.example.appointment.appointments.dto.CreateAppointmentRequest;
import com.example.appointment.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody CreateAppointmentRequest req){
        long userId = CurrentUser.id();
        return ResponseEntity.ok(service.create(userId, req));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable("id") long id){
        long userId = CurrentUser.id();
        service.cancel(userId, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<AppointmentListItem>> myAppointments(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to,

            @RequestParam(required = false) String status
    ) {
        long userId = CurrentUser.id();

        // Parametre yoksa: filtre yok
        if (from == null && to == null && (status == null || status.isBlank())) {
            return ResponseEntity.ok(service.listForCustomer(userId));
        }

        // Parametre varsa: filtreli
        return ResponseEntity.ok(service.listForCustomer(userId, from, to, status));
    }
}
