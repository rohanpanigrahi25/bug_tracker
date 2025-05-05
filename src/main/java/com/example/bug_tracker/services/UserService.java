package com.example.bug_tracker.services;

import com.example.bug_tracker.entities.User;
import com.example.bug_tracker.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public User registerUser(@Valid User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        notificationService.createNotification(
                savedUser.getId(),
                null,
                "Welcome to Bug Tracker! Your account has been created."
        );
        return savedUser;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
