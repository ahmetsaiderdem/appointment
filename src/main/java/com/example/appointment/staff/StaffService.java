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

    public List<StaffAppointmentListItem> myAppointments(long currentUserId,
                                                         java.time.LocalDateTime from,
                                                         java.time.LocalDateTime to,
                                                         String status) {
        long staffId = staffRepo.getStaffIdByUserId(currentUserId);

        // filtre yoksa eskisi gibi hepsi
        if (from == null && to == null && (status == null || status.isBlank())) {
            return apptRepo.findByStaff(staffId);
        }

        // from/to zorunlu birlikte gelsin (takvim mantığı)
        if (from == null || to == null) {
            throw new IllegalArgumentException("Filtre kullanacaksan from ve to birlikte zorunlu.");
        }
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("from, to'dan küçük olmalı.");
        }

        return apptRepo.findByStaffFiltered(staffId, from, to, status);
    }

}
