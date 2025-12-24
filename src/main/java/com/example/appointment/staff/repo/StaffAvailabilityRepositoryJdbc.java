package com.example.appointment.staff.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;

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

}
