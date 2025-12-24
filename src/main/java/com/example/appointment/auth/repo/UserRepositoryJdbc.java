package com.example.appointment.auth.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryJdbc {

    private final JdbcTemplate jdbc;

    public UserRepositoryJdbc(JdbcTemplate jdbc){
        this.jdbc=jdbc;
    }

    public boolean existsByEmail(String email){

        Integer cnt=jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?",
                Integer.class,
                email
        );
        return cnt!=null&& cnt >0;
    }

    public long insertUser(String email,String passwordHash,String fullName,String phone){
        KeyHolder kh=new GeneratedKeyHolder();

        jdbc.update(con ->{
            PreparedStatement ps=con.prepareStatement(
                    "INSERT INTO users(email, password_hash, full_name, phone, enabled) VALUES(?,?,?,?,1)",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1,email);
            ps.setString(2,passwordHash);
            ps.setString(3,fullName);
            ps.setString(4,phone);
            return ps;
        },kh);


        Number key= kh.getKey();
        if (key==null)throw new IllegalStateException("User id üretilemedi.");
        return key.longValue();
    }

    public void addRole(long userId,long roleId){
        jdbc.update(
                "INSERT INTO user_roles(user_id, role_id) VALUES(?,?)",
                userId,roleId
        );
    }

    public Optional<UserAuthView> findAuthByEmail(String email){
        List<UserAuthView> list =jdbc.query(
                "SELECT id, email, password_hash, enabled FROM users WHERE email = ?",
                (rs,rowNum)->new UserAuthView(
                        rs.getLong("id"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getBoolean("enabled")
                ),
                email
        );
        return list.stream().findFirst();
    }

    public List<String> findRoleNamesByUserId(long userId){
        return jdbc.query(
                """
                        SELECT r.name
                        FROM user_roles ur
                        JOIN roles r ON r.id = ur.role_id
                        WHERE ur.user_id = ?
                        """,
                (rs,rowNum)->rs.getString("name"),userId
        );
    }

    public record UserAuthView(long id,String email,String passwordHash,boolean enabled){}

}
