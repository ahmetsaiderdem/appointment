package com.example.appointment.services.repo;

import com.example.appointment.services.dto.ServiceResponse;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class ServiceRepositoryJdbc {

    private final JdbcTemplate jdbc;

    public ServiceRepositoryJdbc(JdbcTemplate jdbc){
        this.jdbc=jdbc;
    }

    public int getDurationMinutes(long serviceId){
        Integer m=jdbc.queryForObject(
                "SELECT duration_minutes FROM services WHERE id = ? AND active = 1",
                Integer.class,
                serviceId
        );
        if (m==null){
            throw new IllegalArgumentException("Hizmet bulunamadı veya pasif");
        }
        return m;
    }

    public List<ServiceResponse> listActive(){
        return jdbc.query(
                """
                        SELECT id, name, duration_minutes, price, active
                        FROM services
                        WHERE active = 1
                        ORDER BY name
                        """,
                (rs,rowNum) ->new ServiceResponse(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getInt("duration_minutes"),
                        rs.getBigDecimal("price"),
                        rs.getBoolean("active")
                )
        );
    }


}
