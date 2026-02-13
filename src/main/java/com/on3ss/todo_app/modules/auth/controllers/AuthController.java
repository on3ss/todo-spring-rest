package com.on3ss.todo_app.modules.auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.on3ss.todo_app.modules.auth.dto.LoginRequest;
import com.on3ss.todo_app.modules.auth.dto.LoginResponse;
import com.on3ss.todo_app.modules.auth.dto.RegisterRequest;
import com.on3ss.todo_app.modules.auth.dto.RegisterResponse;
import com.on3ss.todo_app.modules.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request.getEmail(), request.getPassword());
        RegisterResponse response = new RegisterResponse("User registered successfully!");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        LoginResponse response = new LoginResponse(token, "Bearer");
        return ResponseEntity.ok(response);
    }
}
