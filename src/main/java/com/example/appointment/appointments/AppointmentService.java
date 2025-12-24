package com.example.appointment.appointments;

import com.example.appointment.appointments.dto.AppointmentListItem;
import com.example.appointment.appointments.dto.AppointmentResponse;
import com.example.appointment.appointments.dto.CreateAppointmentRequest;
import com.example.appointment.appointments.repo.AppointmentRepositoryJdbc;
import com.example.appointment.services.repo.ServiceRepositoryJdbc;
import com.example.appointment.staff.repo.StaffAvailabilityRepositoryJdbc;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepositoryJdbc apptRepo;
    private final ServiceRepositoryJdbc serviceRepo;
    private final StaffAvailabilityRepositoryJdbc availabilityRepo;

    public AppointmentService( AppointmentRepositoryJdbc apptRepo,ServiceRepositoryJdbc serviceRepo, StaffAvailabilityRepositoryJdbc availabilityRepo){
        this.apptRepo=apptRepo;
        this.serviceRepo=serviceRepo;
        this.availabilityRepo=availabilityRepo;
    }

    @Transactional
    public AppointmentResponse create(long customerUserId, CreateAppointmentRequest req) {

        long staffId = req.getStaffId();
        long serviceId = req.getServiceId();


        int durationMin = serviceRepo.getDurationMinutes(serviceId);

        LocalDateTime startAt = req.getStartAt();
        LocalDateTime endAt = startAt.plusMinutes(durationMin);


        if (!startAt.toLocalDate().equals(endAt.toLocalDate())) {
            throw new IllegalArgumentException("Randevu aynı gün içinde bitmelidir.");
        }


        int dayOfWeek = startAt.getDayOfWeek().getValue();
        LocalTime startTime = startAt.toLocalTime();
        LocalTime endTime = endAt.toLocalTime();

        boolean ok = availabilityRepo.isWithinAvailability(staffId, dayOfWeek, startTime, endTime);
        if (!ok) {
            throw new IllegalArgumentException("Seçilen saat, çalışanın müsaitlik aralığında değil.");
        }


        apptRepo.lockStaffRow(staffId);


        if (apptRepo.existsOverlapBooked(staffId, startAt, endAt)) {
            throw new IllegalStateException("Bu çalışan için seçilen saat dolu. Başka saat seç.");
        }


        long id = apptRepo.insert(customerUserId, staffId, serviceId, startAt, endAt, req.getNotes());

        return new AppointmentResponse(id, staffId, serviceId, startAt, endAt, "BOOKED");
    }

    public void cancel(long customerId,long appointmentId){
        int updated= apptRepo.cancelIfOwned(appointmentId,customerId);
        if (updated==0){
            throw new IllegalArgumentException("Randevu bulunamadı / size ait değil / zaten iptal.");
        }
    }

    public List<AppointmentListItem> listForCustomer(long customerId){
        return apptRepo.findByCustomer(customerId);
    }
}




