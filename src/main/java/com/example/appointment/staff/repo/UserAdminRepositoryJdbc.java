package com.example.appointment.staff.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserAdminRepositoryJdbc {

    private final JdbcTemplate jdbc;

    public UserAdminRepositoryJdbc(JdbcTemplate jdbc){
        this.jdbc=jdbc;


    }



    public boolean existsUser(long userId){
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE id = ?", Integer.class,userId);
        return cnt != null && cnt>0;
    }

    public boolean isUserEnabled(long userId){
        Integer v= jdbc.queryForObject("SELECT enabled FROM users WHERE id = ?",Integer.class,userId);
        return v != null && v == 1;
    }
}
