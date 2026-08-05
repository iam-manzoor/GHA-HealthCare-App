package com.healthcare.user.controller;

import com.healthcare.user.model.Role;
import com.healthcare.user.model.UserEntity;
import com.healthcare.user.security.JwtUtil;
import com.healthcare.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        logger.info("Received register request for username={}", request.username());
        UserEntity saved = userService.register(request.username(), request.password(), request.role());
        logger.info("Registration completed for username={}", saved.getUsername());
        return ResponseEntity.ok(new RegisterResponse(saved.getId(), saved.getUsername(), saved.getRole()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        logger.info("Received login request for username={}", request.username());
        return userService.authenticate(request.username(), request.password())
            .map(user -> {
                logger.info("Login successful for username={}", request.username());
                return ResponseEntity.ok((Object) new LoginResponse(jwtUtil.generateToken(user.getUsername(), user.getRole().name())));
            })
            .orElseGet(() -> {
                logger.warn("Login failed for username={}", request.username());
                return ResponseEntity.status(401).body("Invalid credentials");
            });
    }

    public record RegisterRequest(String username, String password, Role role) {}
    public record LoginRequest(String username, String password) {}
    public record RegisterResponse(Long id, String username, Role role) {}
    public record LoginResponse(String token) {}
}
