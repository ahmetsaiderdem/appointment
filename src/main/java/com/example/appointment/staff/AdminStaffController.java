package com.example.appointment.staff;


import com.example.appointment.staff.dto.CreateStaffRequest;
import com.example.appointment.staff.dto.StaffListItem;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/staff")
public class AdminStaffController {
    private final StaffAdminService service;

    public AdminStaffController(StaffAdminService service){
        this.service=service;
    }

    @PostMapping
    public ResponseEntity<?> makeStaff(@Valid @RequestBody CreateStaffRequest req){
        long staffId= service.makeStaff(req);
        return ResponseEntity.ok(Map.of("staffId",staffId));
    }

    @GetMapping
    public ResponseEntity<List<StaffListItem>> list(){
        return ResponseEntity.ok(service.listStaff());
    }
    @PutMapping("/{staffId}/active")
    public ResponseEntity<?> setActive(@PathVariable long staffId, @RequestParam boolean value){
        service.setActive(staffId,value);
        return ResponseEntity.ok().build();
    }
}
