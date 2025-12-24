package com.example.appointment.auth.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepositoryJdbc {

    private final JdbcTemplate jdbc;

    public RoleRepositoryJdbc(JdbcTemplate jdbc){
        this.jdbc=jdbc;
    }

    public long getRoleIdByName(String roleName){

        Long id=jdbc.queryForObject(
                "SELECET id FROM roles WHERE name = ?",
                Long.class,
                roleName
        );
        if (id == null){
            throw  new IllegalStateException("Role bulunamadı: " + roleName);
        }
        return id;
    }
}
