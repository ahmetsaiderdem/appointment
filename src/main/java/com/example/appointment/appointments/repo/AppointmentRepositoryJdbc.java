package com.example.appointment.appointments.repo;

import com.example.appointment.appointments.dto.AppointmentListItem;
import com.example.appointment.staff.dto.StaffAppointmentListItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AppointmentRepositoryJdbc {

    private final JdbcTemplate jdbc;

    public AppointmentRepositoryJdbc(JdbcTemplate jdbc){
        this.jdbc=jdbc;
    }


    public void lockStaffRow(long staffId){

        Integer ok= jdbc.queryForObject(
                "SELECT 1 FROM staff WHERE id = ? AND active = 1 FOR UPDATE",
                Integer.class,
                staffId
        );

        if (ok==null){
            throw new IllegalArgumentException("Çalışan bulunamadı veya pasif");
        }


    }

    public boolean existsOverlapBooked(long staffId, LocalDateTime startAt,LocalDateTime endAt){
        Integer cnt=jdbc.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM appointments
                        WHERE staff_id = ?
                            AND status = 'BOOKED'
                            AND start_at < ?
                            AND end_At >?
                        """,
                Integer.class,
                staffId,endAt,startAt
        );
        return cnt !=null && cnt>0;
    }

    public long insert(long customerUserId,long staffId,long serviceId,LocalDateTime startAt,LocalDateTime endAt,String notes){
        KeyHolder kh = new GeneratedKeyHolder();

        jdbc.update( con -> {
            PreparedStatement ps = con.prepareStatement(
                    """
                            INSERT INTO appointments(customer_user_id, staff_id, service_id, start_at, end_at, status, notes)
                            VALUES(?,?,?,?,?,'BOOKED',?)
                            """,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1,customerUserId);
            ps.setLong(2,staffId);
            ps.setLong(3,serviceId);
            ps.setObject(4,staffId);
            ps.setObject(5,endAt);
            ps.setString(6,notes);
            return ps;

        },kh);

        Number key =kh.getKey();
        if (key==null )throw new IllegalStateException("Appointment id üretilemedi");

        return key.longValue();
    }

    public int cancelIfOwned(long appointmentId,long customerId){
        return jdbc.update(
                """
                        UPDATE appointments
                        SET status = 'CANCELLED'
                        WHERE id = ?
                            AND customer_user_id = ?
                            AND status = 'BOOKED'
                        """,
                appointmentId,customerId
        );
    }

    public List<AppointmentListItem> findByCustomer(long customerId){
        return jdbc.query(
                """
                        SELECT id, staff_id, service_id, start_at, end_at, status
                        FROM appointments
                        WHERE customer_user_id = ?
                        ORDER BY start_at DESC
                        """,
                (rs,rowNum)->new AppointmentListItem(
                        rs.getLong("id"),
                        rs.getLong("staff_id"),
                        rs.getLong("service_id"),
                        rs.getObject("start_at",LocalDateTime.class),
                        rs.getObject("end_at",LocalDateTime.class),
                        rs.getString("status")
                ),customerId
        );
    }
    public List<StaffAppointmentListItem> findByStaff(long staffId){
        return jdbc.query(
                """
                        SELECT id, customer_user_id, service_id, start_at, end_at, status
                        FROM appointments
                        WHERE staff_id = ?
                        ORDER BY start_at DESC
                        """,
                (rs,rowNum)->new StaffAppointmentListItem(
                        rs.getLong("id"),
                        rs.getLong("customer_user_id"),
                        rs.getLong("service_id"),
                        rs.getObject("start_at",LocalDateTime.class),
                        rs.getObject("end_at",LocalDateTime.class),
                        rs.getString("status")
                ),staffId
        );
    }

    public List<AppointmentListItem> findByCustomerFiltered(long customerId,
                                                            java.time.LocalDateTime from,
                                                            java.time.LocalDateTime to,
                                                            String statusOrNull){
        if (statusOrNull == null || statusOrNull.isBlank()){
            return jdbc.query(
                    """
                            SELECT id, staff_id, service_id, start_at, end_at, status
                            FROM appointments
                            WHERE customer_user_id = ?
                                AND start_at >= ?
                                AND start_at < ?
                            ORDER BY start_at ASC
                            """,
                    (rs, rowNum) -> new com.example.appointment.appointments.dto.AppointmentListItem(
                            rs.getLong("id"),
                            rs.getLong("staff_id"),
                            rs.getLong("service_id"),
                            rs.getObject("start_at",java.time.LocalDateTime.class),
                            rs.getObject("end_at",java.time.LocalDateTime.class),
                            rs.getString("status")
                    ),
                    customerId,from,to
            );
        }
        return jdbc.query(
                """
                        SELECT id, staff_id, service_id, start_at, end_at, status
                        FROM appointments
                        WHERE customer_user_id
                            AND start_at >= ?
                            AND start_at < ?
                            AND status = ?
                        ORDER BY start_at DESC
                        """,
                (rs,rowNum)->new com.example.appointment.appointments.dto.AppointmentListItem(
                        rs.getLong("id"),
                        rs.getLong("staff_id"),
                        rs.getLong("service_id"),
                        rs.getObject("start_at",java.time.LocalDateTime.class),
                        rs.getObject("end_at",java.time.LocalDateTime.class),
                        rs.getString("status")
                ),
                customerId,from,to,statusOrNull
        );
    }


    public List<com.example.appointment.staff.dto.StaffAppointmentListItem> findByStaffFiltered(
            long staffId,
            java.time.LocalDateTime from,
            java.time.LocalDateTime to,
            String statusOrNull
    ) {
        if (statusOrNull == null || statusOrNull.isBlank()) {
            return jdbc.query(
                    """
                    SELECT id, customer_user_id, service_id, start_at, end_at, status
                    FROM appointments
                    WHERE staff_id = ?
                      AND start_at >= ?
                      AND start_at < ?
                    ORDER BY start_at ASC
                    """,
                    (rs, rowNum) -> new com.example.appointment.staff.dto.StaffAppointmentListItem(
                            rs.getLong("id"),
                            rs.getLong("customer_user_id"),
                            rs.getLong("service_id"),
                            rs.getObject("start_at", java.time.LocalDateTime.class),
                            rs.getObject("end_at", java.time.LocalDateTime.class),
                            rs.getString("status")
                    ),
                    staffId, from, to
            );
        }

        return jdbc.query(
                """
                SELECT id, customer_user_id, service_id, start_at, end_at, status
                FROM appointments
                WHERE staff_id = ?
                  AND start_at >= ?
                  AND start_at < ?
                  AND status = ?
                ORDER BY start_at ASC
                """,
                (rs, rowNum) -> new com.example.appointment.staff.dto.StaffAppointmentListItem(
                        rs.getLong("id"),
                        rs.getLong("customer_user_id"),
                        rs.getLong("service_id"),
                        rs.getObject("start_at", java.time.LocalDateTime.class),
                        rs.getObject("end_at", java.time.LocalDateTime.class),
                        rs.getString("status")
                ),
                staffId, from, to, statusOrNull
        );
    }
    public java.util.Optional<java.time.LocalDateTime> findStartAtOwned(long appointmentId, long customerUserId) {
        var list = jdbc.query(
                "SELECT start_at FROM appointments WHERE id = ? AND customer_user_id = ?",
                (rs, rowNum) -> rs.getObject("start_at", java.time.LocalDateTime.class),
                appointmentId, customerUserId
        );
        return list.stream().findFirst();
    }


}
