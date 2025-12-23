package com.example.appointment.security;

import com.example.appointment.config.AppJwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    private static final String CLAIM_ROLES="roles";

    private final AppJwtProperties props;
    private final Key key;

    public JwtService(AppJwtProperties props){
        this.props=props;

        if (props.getSecret()==null || props.getSecret().isBlank()){
            throw new IllegalStateException("app.jwt.secret boş olamaz ");

        }
        byte[] bytes=props.getSecret().getBytes(StandardCharsets.UTF_8);
        if (bytes.length<32){
            throw new IllegalStateException("app.jwt.secret en az 32 karakter olmalı (HS256)");
        }

        this.key= Keys.hmacShaKeyFor(bytes);
    }


    public String generateAccessToken(String subject, List<String> roles){
        Instant now = Instant.now();
        Instant exp=now.plus(props.getAccessTokenMinutes(), ChronoUnit.MINUTES);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .addClaims(Map.of(CLAIM_ROLES,roles))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean isValid(String token){
        try {
            parse(token);
            return true;
        }
        catch (JwtException | IllegalArgumentException e){
            return false;
        }

    }

    public String getSubject(String token){
        return parse(token).getBody().getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token){
        Object v=parse(token).getBody().get(CLAIM_ROLES);
        if (v==null)return List.of();
        return (List<String>) v;
    }

    public Claims getClaims(String token){
        return parse(token).getBody();
    }

    private Jws<Claims> parse(String token){
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }


}
