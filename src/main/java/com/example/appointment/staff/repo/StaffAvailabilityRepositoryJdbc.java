package com.example.appointment.staff.repo;

import com.example.appointment.staff.dto.AvailabilityItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.List;

@Repository
public class StaffAvailabilityRepositoryJdbc {
    private final JdbcTemplate jdbc;

    public StaffAvailabilityRepositoryJdbc(JdbcTemplate jdbc){
        this.jdbc=jdbc;
    }

    public boolean isWithinAvailability(long staffId, int dayOfWeek, LocalTime startTime,LocalTime endTime){

        Integer cnt=jdbc.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM staff_availability
                        WHERE staff_id =?
                            AND day_of_week= ?
                            AND active = 1
                            AND start_time <= ? 
                            AND end_time >= ?
                        """,
                Integer.class,
                staffId,dayOfWeek,startTime,endTime
        );
        return cnt != null && cnt> 0;
    }

    public List<AvailabilityItem> listByStaff(long staffId){
        return jdbc.query(
                """
                        SELECT id, day_of_week, start_time, end_time, active
                        FROM staff_availability
                        WHERE staff_id = ?
                        ORDER BY day_of_week, start_time
                        """,
                (rs,rowNum) -> new AvailabilityItem(
                        rs.getLong("id"),
                        rs.getInt("day_of_week"),
                        rs.getObject("start_time",LocalTime.class),
                        rs.getObject("end_time", LocalTime.class),
                        rs.getBoolean("active")
                ),
                staffId
        );
    }

    public boolean existsOvelap(long staffId,int dayOfWeek,LocalTime startTime,LocalTime endTime){
        Integer cnt=jdbc.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM staff_availability
                        WHERE staff_id = ?
                            AND day_of_week = ?
                            AND active = 1
                            AND start_time < ?
                            AND end_time > ?
                        """,
                Integer.class,
                staffId,dayOfWeek,startTime,endTime
        );
        return cnt != null && cnt> 0;
    }

    public long insert(long staffId,int dayOfWeek,LocalTime startTime,LocalTime endTime){
        KeyHolder kh= new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps= con.prepareStatement(
                    """
                            INSERT INTO staff_availability(staff_id, day_of_week, start_time, end_time, active)
                            VALUES(?,?,?,?,1)
                            """,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1,staffId);
            ps.setInt(2,dayOfWeek);
            ps.setObject(3,startTime);
            ps.setObject(4,endTime);
            return ps;
        },kh);
        Number key = kh.getKey();
        if (key == null) throw new IllegalStateException("Availability id üretilemedi.");
        return key.longValue();
    }

    public int deleteOwned(long id, long staffId) {
        return jdbc.update(
                "DELETE FROM staff_availability WHERE id = ? AND staff_id = ?",
                id, staffId
        );
    }
}
