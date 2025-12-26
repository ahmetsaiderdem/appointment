package com.example.appointment.staff.repo;

import com.example.appointment.staff.dto.StaffListItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class StaffAdminRepositoryJdbc {

    private final JdbcTemplate jdbc;

    public StaffAdminRepositoryJdbc(JdbcTemplate jdbc){
        this.jdbc=jdbc;
    }

    public boolean existsStaffByUserId(long userId){
        Integer cnt= jdbc.queryForObject("SELECT COUNT(*) FROM staff WHERE user_id = ?", Integer.class,userId);
        return cnt!= null && cnt>0;
    }

    public long insertStaff(long userId,String title){
        KeyHolder kh=new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps= con.prepareStatement(
                    "INSERT INTO staff(user_id, title, active) VALUES(?,?,1)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1,userId);
            ps.setString(2,title);
            return ps;

        },kh);
        Number key= kh.getKey();
        if (key==null)throw new IllegalStateException("Staff id üretilemedi.");
        return key.longValue();
    }

    public List<StaffListItem> listStaff(){
        return jdbc.query(
                """
                        SELECT s.id AS staff_id, s.user_id, u.full_name, u.email, s.title, s.active
                        FROM staff s
                        JOIN users u ON u.id = s.user_id
                        ORDER BY s.id DESC
                        """,
                (rs,rowNum)->new StaffListItem(
                        rs.getLong("staff_id"),
                        rs.getLong("user_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("title"),
                        rs.getBoolean("active")
                )
        );
    }

    public int updateActive(long staffId,boolean active){
        return jdbc.update("UPDATE staff SET active = ? WHERE id = ?",active ? 1 :0,staffId);
    }
}
