package com.healthcare.user.service;

import com.healthcare.user.model.Role;
import com.healthcare.user.model.UserEntity;
import com.healthcare.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity register(String username, String password, Role role) {
        logger.info("Register request for username={}", username);
        if (userRepository.findByUsername(username).isPresent()) {
            logger.warn("Registration failed - username already exists: {}", username);
            throw new IllegalArgumentException("Username already exists");
        }
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        UserEntity saved = userRepository.save(user);
        logger.info("User registered successfully: {}", saved.getUsername());
        return saved;
    }

    public Optional<UserEntity> authenticate(String username, String password) {
        logger.info("Login attempt for username={}", username);
        Optional<UserEntity> result = userRepository.findByUsername(username)
            .filter(user -> passwordEncoder.matches(password, user.getPassword()));
        if (result.isEmpty()) {
            logger.warn("Invalid login attempt for username={}", username);
        } else {
            logger.info("User authenticated successfully: {}", username);
        }
        return result;
    }
}
