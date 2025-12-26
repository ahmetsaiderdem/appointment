package com.example.appointment.appointments;

import com.example.appointment.appointments.dto.AppointmentListItem;
import com.example.appointment.appointments.dto.AppointmentResponse;
import com.example.appointment.appointments.dto.CreateAppointmentRequest;
import com.example.appointment.appointments.repo.AppointmentRepositoryJdbc;
import com.example.appointment.services.repo.ServiceRepositoryJdbc;
import com.example.appointment.staff.repo.StaffAvailabilityRepositoryJdbc;
import com.example.appointment.staff.repo.StaffRepositoryJdbc;
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
    private final StaffRepositoryJdbc staffRepo;

    public AppointmentService( AppointmentRepositoryJdbc apptRepo,ServiceRepositoryJdbc serviceRepo, StaffAvailabilityRepositoryJdbc availabilityRepo,StaffRepositoryJdbc staffRepo){
        this.apptRepo=apptRepo;
        this.serviceRepo=serviceRepo;
        this.availabilityRepo=availabilityRepo;
        this.staffRepo=staffRepo;
    }

    @Transactional
    public AppointmentResponse create(long customerUserId, CreateAppointmentRequest req) {

        long staffId = req.getStaffId();
        long serviceId = req.getServiceId();

        // 0) startAt normalize + kurallar
        LocalDateTime startAt = normalize(req.getStartAt());
        validateSlot(startAt);
        validateNotPast(startAt);

        // 1) duration ve endAt
        int durationMin = serviceRepo.getDurationMinutes(serviceId);
        LocalDateTime endAt = startAt.plusMinutes(durationMin);

        // 2) gün taşmasın (MVP)
        if (!startAt.toLocalDate().equals(endAt.toLocalDate())) {
            throw new IllegalArgumentException("Randevu aynı gün içinde bitmelidir.");
        }

        // 3) availability kontrolü
        int dayOfWeek = startAt.getDayOfWeek().getValue();
        LocalTime startTime = startAt.toLocalTime();
        LocalTime endTime = endAt.toLocalTime();

        boolean ok = availabilityRepo.isWithinAvailability(staffId, dayOfWeek, startTime, endTime);
        if (!ok) {
            throw new IllegalArgumentException("Seçilen saat, çalışanın müsaitlik aralığında değil.");
        }

        // 4) collision için kilit + overlap kontrol
        apptRepo.lockStaffRow(staffId);

        if (apptRepo.existsOverlapBooked(staffId, startAt, endAt)) {
            throw new IllegalStateException("Bu çalışan için seçilen saat dolu. Başka saat seç.");
        }

        // 5) insert
        long id = apptRepo.insert(customerUserId, staffId, serviceId, startAt, endAt, req.getNotes());

        return new AppointmentResponse(id, staffId, serviceId, startAt, endAt, "BOOKED");
    }


    public void cancel(long customerId,long appointmentId){
        var startAtOpt = apptRepo.findStartAtOwned(appointmentId, customerId);
        if (startAtOpt.isEmpty()) {
            throw new IllegalArgumentException("Randevu bulunamadı / size ait değil.");
        }

        var startAt = startAtOpt.get();
        if (!startAt.isAfter(java.time.LocalDateTime.now())) {
            throw new IllegalStateException("Randevu başladı/geçtiği için iptal edilemez.");
        }

        int updated = apptRepo.cancelIfOwned(appointmentId, customerId);
        if (updated == 0) {
            throw new IllegalArgumentException("Randevu zaten iptal veya bulunamadı.");
        }
    }

    public List<AppointmentListItem> listForCustomer(long customerId){
        return apptRepo.findByCustomer(customerId);
    }

    public java.util.List<com.example.appointment.appointments.dto.AppointmentListItem> listForCustomer(
            long customerUserId,
            java.time.LocalDateTime from,
            java.time.LocalDateTime to,
            String status
    ) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from ve to zorunlu.");
        }
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("from, to'dan küçük olmalı.");
        }
        return apptRepo.findByCustomerFiltered(customerUserId, from, to, status);
    }


    public java.util.List<com.example.appointment.staff.dto.StaffAppointmentListItem> myAppointments(
            long currentUserId,
            java.time.LocalDateTime from,
            java.time.LocalDateTime to,
            String status
    ) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from ve to zorunlu.");
        }
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("from, to'dan küçük olmalı.");
        }

        long staffId = staffRepo.getStaffIdByUserId(currentUserId);
        return apptRepo.findByStaffFiltered(staffId, from, to, status);
    }

    private static java.time.LocalDateTime normalize(java.time.LocalDateTime dt) {
        return dt.withSecond(0).withNano(0);
    }

    private static void validateSlot(java.time.LocalDateTime startAt) {
        int m = startAt.getMinute();
        if (m % 15 != 0) { // 30 dk istersen: m % 30 != 0
            throw new IllegalArgumentException("Randevu başlangıcı 15 dk katı olmalı (00/15/30/45).");
        }
    }

    private static void validateNotPast(java.time.LocalDateTime startAt) {
        if (startAt.isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Geçmiş zamana randevu alınamaz.");
        }
    }


}




