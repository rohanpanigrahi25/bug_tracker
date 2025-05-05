package com.example.bug_tracker.config;

import com.example.bug_tracker.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthService authService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.cookie-name}")
    private String cookieName;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(authService, jwtSecret, cookieName);

        http
                .csrf(csrf -> csrf.disable()) // CSRF disabled for stateless API
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll() // Public endpoints for login/register
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN") // Admin-only user management
                        .requestMatchers("/api/v1/projects/**").hasRole("ADMIN") // Admin-only project management
                        .requestMatchers("/api/v1/milestones/**").hasRole("ADMIN") // Admin-only milestone creation
                        .requestMatchers("/api/v1/**").authenticated() // All other endpoints require authentication
                        .anyRequest().denyAll() // Deny any unmatched requests
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}

