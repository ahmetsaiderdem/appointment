package com.example.appointment.services.repo;

import com.example.appointment.services.dto.ServiceResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
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

    public long insert(String name,int durationMinutes,java.math.BigDecimal price){
        KeyHolder kh=new GeneratedKeyHolder();

        jdbc.update( con -> {
            PreparedStatement ps=con.prepareStatement(
                    "INSERT INTO services(name, duration_minutes, price, active) VALUES(?,?,?,1)",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1,name);
            ps.setInt(2,durationMinutes);
            ps.setBigDecimal(3,price);
            return ps;
        },kh);

        Number key=kh.getKey();
        if (key==null)throw new IllegalStateException("Service id üretilemedi");
        return key.longValue();
    }

    public int updatePartial(long id,String name,Integer durationMinutes,java.math.BigDecimal price,Boolean active){
        return jdbc.update(
                """
                        UPDATE services
                        SET
                            name = COALESCE(?,name),
                            duration_minutes = COALESCE(?,duration_minutes),
                            price = COALESCE(?,price),
                            active = COALESCE(?,active)
                        WHERE id= ?   
                        """,
                name,durationMinutes,price,active,id
        );
    }

    public boolean existsById(long id){
        Integer cnt= jdbc.queryForObject("SELECT COUNT(*) FROM services WHERE id = ?",Integer.class,id);
        return cnt != null && cnt>0;
    }


}
