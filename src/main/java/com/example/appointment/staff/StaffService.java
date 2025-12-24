package com.example.appointment.staff;

import com.example.appointment.appointments.repo.AppointmentRepositoryJdbc;
import com.example.appointment.staff.dto.StaffAppointmentListItem;
import com.example.appointment.staff.repo.StaffRepositoryJdbc;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffService {

    private final StaffRepositoryJdbc staffRepo;
    private final AppointmentRepositoryJdbc apptRepo;

    public StaffService( StaffRepositoryJdbc staffRepo,AppointmentRepositoryJdbc apptRepo){
        this.apptRepo=apptRepo;
        this.staffRepo=staffRepo;
    }

    public List<StaffAppointmentListItem> myAppointments(long cuurenUserId){
        long staffId= staffRepo.getStaffIdByUserId(cuurenUserId);
        return apptRepo.findByStaff(staffId);
    }
}
