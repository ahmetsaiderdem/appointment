package com.example.appointment.staff;

import com.example.appointment.auth.repo.RoleRepositoryJdbc;
import com.example.appointment.auth.repo.UserRepositoryJdbc;
import com.example.appointment.staff.dto.CreateStaffRequest;
import com.example.appointment.staff.dto.StaffListItem;
import com.example.appointment.staff.repo.StaffAdminRepositoryJdbc;
import com.example.appointment.staff.repo.UserAdminRepositoryJdbc;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StaffAdminService {

    private final UserAdminRepositoryJdbc userAdminRepo;
    private final StaffAdminRepositoryJdbc staffRepo;
    private final RoleRepositoryJdbc roleRepo;
    private final UserRepositoryJdbc userRepo;

    public StaffAdminService(UserAdminRepositoryJdbc userAdminRepo,
                             StaffAdminRepositoryJdbc staffRepo,
                             RoleRepositoryJdbc roleRepo,
                             UserRepositoryJdbc userRepo){
        this.userAdminRepo=userAdminRepo;
        this.staffRepo=staffRepo;
        this.roleRepo=roleRepo;
        this.userRepo=userRepo;
    }

    @Transactional
    public long makeStaff(CreateStaffRequest req){
        long userId=req.getUserId();

        if (!userAdminRepo.existsUser(userId)){
            throw new IllegalArgumentException("User bulunamadı.");
        }
        if (!userAdminRepo.isUserEnabled(userId)){
            throw new IllegalArgumentException("User pasif, staff yapılamaz");
        }
        if (staffRepo.existsStaffByUserId(userId)){
            throw new IllegalArgumentException("Bu user zaten staff.");
        }

        long staffId = staffRepo.insertStaff(userId, req.getTitle());

        long staffRoleId=roleRepo.getRoleIdByName("STAFF");

        return staffId;
    }

    public List<StaffListItem> listStaff(){
        return staffRepo.listStaff();
    }

    @Transactional
    public void setActive(long staffId,boolean active){
        int updated = staffRepo.updateActive(staffId,active);
        if (updated==0){
            throw new IllegalArgumentException("Staff bulunamadı. ");
        }
    }
}
