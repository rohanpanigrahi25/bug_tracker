package com.example.bug_tracker.controllers;

import com.example.bug_tracker.entities.User;
import com.example.bug_tracker.services.AuthService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @Value("${app.jwt.cookie-name}")
    private String cookieName;

    @Value("${app.jwt.cookie-max-age}")
    private int cookieMaxAge;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = authService.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String jwt = authService.generateJwtToken(user);
        Cookie cookie = new Cookie(cookieName, jwt);
        cookie.setHttpOnly(true);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(cookieMaxAge);
        response.addCookie(cookie);
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid User user, HttpServletResponse response) {
        User savedUser = authService.registerUser(user);
        String jwt = authService.generateJwtToken(savedUser);
        Cookie cookie = new Cookie(cookieName, jwt);
        cookie.setHttpOnly(true);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(cookieMaxAge);
        response.addCookie(cookie);
        return ResponseEntity.ok(new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getRole()));
    }
}

@Getter
@Setter
class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}

@Getter
class UserResponse {
    private final String id;
    private final String name;
    private final String email;
    private final User.Role role;

    public UserResponse(String id, String name, String email, User.Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
