package com.on3ss.todo_app.modules.auth.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.on3ss.todo_app.infrastructure.exceptions.BusinessException;
import com.on3ss.todo_app.infrastructure.security.JwtUtils;
import com.on3ss.todo_app.modules.auth.domain.User;
import com.on3ss.todo_app.modules.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public User register(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException("User with email " + email + " already exists.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Invalid email or password."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("Invalid email or password.");
        }

        return jwtUtils.generateToken(user.getEmail());
    }
}
