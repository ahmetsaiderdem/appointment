package com.example.appointment.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static Map<String, Object> base(HttpStatus status, String message, HttpServletRequest req) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.name());
        body.put("message", message);
        body.put("path", req.getRequestURI());
        return body;
    }

    // 1) Validation hataları (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }

        Map<String, Object> body = base(HttpStatus.BAD_REQUEST, "Validation failed", req);
        body.put("fieldErrors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 2) 400 - yanlış input
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException ex, HttpServletRequest req) {
        Map<String, Object> body = base(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 3) 409 - çakışma / iş kuralı çarpışması
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> conflict(IllegalStateException ex, HttpServletRequest req) {
        Map<String, Object> body = base(HttpStatus.CONFLICT, ex.getMessage(), req);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // 4) 401 - authentication
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> unauthorized(AuthenticationException ex, HttpServletRequest req) {
        Map<String, Object> body = base(HttpStatus.UNAUTHORIZED, "Unauthorized", req);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // 5) 403 - yetkisiz erişim
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> forbidden(AccessDeniedException ex, HttpServletRequest req) {
        Map<String, Object> body = base(HttpStatus.FORBIDDEN, "Forbidden", req);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // 6) catch-all (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> unknown(Exception ex, HttpServletRequest req) {
        ex.printStackTrace(); // <-- GERÇEK HATA ARTIK CONSOLE'A DÜŞECEK
        Map<String, Object> body = base(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", req);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}


