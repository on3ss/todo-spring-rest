package com.on3ss.todo_app.modules.auth.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.on3ss.todo_app.modules.auth.dto.LoginRequest;
import com.on3ss.todo_app.modules.auth.dto.RegisterRequest;
import com.on3ss.todo_app.modules.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request.getEmail(), request.getPassword());
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");

        return ResponseEntity.ok(response.toString());
    }
}
