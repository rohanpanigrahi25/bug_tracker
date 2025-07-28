package com.example.bug_tracker.services;

import com.example.bug_tracker.config.CustomUserDetails;
import com.example.bug_tracker.entities.User;
import com.example.bug_tracker.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return new CustomUserDetails(user);
    }

    public String generateJwtToken(User user) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }

    @Transactional
    public User registerUser(@Valid User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Check if the requested role is ADMIN
        if (user.getRole() == User.Role.ADMIN) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new SecurityException("Only authenticated admins can create admin users");
            }
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (userDetails.getAuthorities().stream()
                    .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
                throw new SecurityException("Only admins can create admin users");
            }
        }

        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
        }
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}