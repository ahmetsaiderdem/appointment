package com.example.appointment.staff;

import com.example.appointment.staff.dto.AvailabilityItem;
import com.example.appointment.staff.dto.CreateAvailabilityRequest;
import com.example.appointment.staff.repo.StaffAvailabilityRepositoryJdbc;
import com.example.appointment.staff.repo.StaffRepositoryJdbc;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
public class StaffAvailabilityService {

    private final StaffRepositoryJdbc staffRepo;
    private final StaffAvailabilityRepositoryJdbc availabilityRepo;

    public StaffAvailabilityService(StaffRepositoryJdbc staffRepo, StaffAvailabilityRepositoryJdbc availabilityRepo){
        this.staffRepo=staffRepo;
        this.availabilityRepo=availabilityRepo;
    }

    public List<AvailabilityItem> myAvailability(long currentId){
        long staffId=staffRepo.getStaffIdByUserId(currentId);
        return availabilityRepo.listByStaff(staffId);
    }

    @Transactional
    public long add(long currentUserId, CreateAvailabilityRequest req){
        long staffId=staffRepo.getStaffIdByUserId(currentUserId);

        int day=req.getDayOfWeek();
        LocalTime start=req.getStartTime();
        LocalTime end=req.getEndTime();
        if (!start.isBefore(end)){
            throw new IllegalArgumentException("startTime endTime'dan küçük olmalı");

        }

        if (availabilityRepo.existsOvelap(staffId,day,start,end)){
            throw new IllegalStateException("Bu gün için seçilen saat araliği mevcut müsaitlikle çakışıyor");
        }
        return availabilityRepo.insert(staffId,day,start,end);
    }

    @Transactional
    public void delete(long currenUserId,long availabilityId){
        long staffId=staffRepo.getStaffIdByUserId(currenUserId);

        int deleted=availabilityRepo.deleteOwned(availabilityId,staffId);
        if (deleted==0){
            throw new IllegalArgumentException("Kayıt bulunamadı veya size ait değil.");
        }
    }
}
