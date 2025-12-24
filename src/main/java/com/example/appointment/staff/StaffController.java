package com.example.appointment.staff;

import com.example.appointment.security.CurrentUser;
import com.example.appointment.staff.dto.StaffAppointmentListItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService){
        this.staffService=staffService;
    }


    @GetMapping("/appointments")
    public ResponseEntity<List<StaffAppointmentListItem>> myAppointments(){
        long userId= CurrentUser.id();
        return ResponseEntity.ok(staffService.myAppointments(userId));
    }
}
