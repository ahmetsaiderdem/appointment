package com.example.appointment.auth;

import com.example.appointment.auth.dto.AuthResponse;
import com.example.appointment.auth.dto.LoginRequest;
import com.example.appointment.auth.dto.RegisterRequest;
import com.example.appointment.auth.repo.RoleRepositoryJdbc;
import com.example.appointment.auth.repo.UserRepositoryJdbc;
import com.example.appointment.security.JwtService;
import io.jsonwebtoken.Jwt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    private final UserRepositoryJdbc users;
    private final RoleRepositoryJdbc roles;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepositoryJdbc users,
                       RoleRepositoryJdbc roles,
                       PasswordEncoder encoder,
                       JwtService jwt){
        this.users=users;
        this.roles=roles;
        this.encoder=encoder;
        this.jwt=jwt;

    }

    @Transactional
    public long register(RegisterRequest req){
        String email =req.getEmail().trim().toLowerCase();

        if (users.existsByEmail(email)){
            throw new IllegalArgumentException("Bu email zaten kayıtlı");

        }

        String passwordHash =encoder.encode(req.getPassword());
        long userId=users.insertUser(email,passwordHash,req.getFullName(),req.getPhone());

        long userRoleId=roles.getRoleIdByName("USER");
        users.addRole(userId,userRoleId);

        return userId;
    }

    public AuthResponse login(LoginRequest req){
        String email=req.getEmail().trim().toLowerCase();

        var u = users.findAuthByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email veya şifre hatalı."));

        if (!u.enabled()){
            throw  new IllegalArgumentException("Hesap pasif.");
        }

        boolean ok =encoder.matches(req.getPassword(),u.passwordHash());

        if (!ok){
            throw  new IllegalArgumentException("Email veya şifre hatalı.");
        }

        List<String> roleName=users.findRoleNamesByUserId(u.id());
        String token=jwt.generateAccessToken(String.valueOf(u.id()),roleName);

        long expiresSec=60L*60L;
        return new AuthResponse(token,expiresSec,roleName);
    }
}
