package com.example.appointment.staff.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StaffRepositoryJdbc {

    private final JdbcTemplate jdbc;

    public StaffRepositoryJdbc(JdbcTemplate jdbc){
        this.jdbc=jdbc;
    }


    public long getStaffIdByUserId(long userId){
        Long staffId=jdbc.queryForObject(
                "SELECT id FROM staff WHERE user_id = ? AND active = 1",
                Long.class,
                userId
        );
        if (staffId==null){
            throw new IllegalArgumentException("Staff profili bulunamadı veya pasif");

        }
        return staffId;
    }
}
