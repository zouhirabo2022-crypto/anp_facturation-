package org.example.anpfacturationbackend.controller;

import org.example.anpfacturationbackend.dto.JwtResponse;
import org.example.anpfacturationbackend.dto.LoginRequest;
import org.example.anpfacturationbackend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    org.springframework.security.authentication.dao.DaoAuthenticationProvider daoAuthenticationProvider;

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("ldapAuthenticationProvider")
    org.springframework.security.authentication.AuthenticationProvider ldapAuthenticationProvider;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        System.out.println("Login attempt for user: " + loginRequest.getUsername() + " with provider: "
                + loginRequest.getProvider());
        String provider = loginRequest.getProvider() != null ? loginRequest.getProvider() : "local";

        Authentication authentication;
        try {
            if ("ldap".equalsIgnoreCase(provider)) {
                if (ldapAuthenticationProvider == null) {
                    throw new org.springframework.security.authentication.AuthenticationServiceException(
                            "LDAP Authentication not configured");
                }
                authentication = ldapAuthenticationProvider.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                loginRequest.getPassword()));
            } else {
                authentication = daoAuthenticationProvider.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                loginRequest.getPassword()));
            }
        } catch (org.springframework.security.core.AuthenticationException e) {
            System.err.println("Authentication failed for " + loginRequest.getUsername() + ": " + e.getMessage());
            return ResponseEntity.status(401).body(Map.of("message", "Authentication failed: " + e.getMessage()));
        }

        System.out.println("Authentication successful for " + loginRequest.getUsername());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);
        String refresh = jwtUtils.generateRefreshToken(loginRequest.getUsername());

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, refresh, userDetails.getUsername(), roles));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken != null && jwtUtils.validateJwtToken(refreshToken)) {
            String username = jwtUtils.getUserNameFromJwtToken(refreshToken);
            String newAccessToken = jwtUtils.generateTokenFromUsername(username, 86400000); // 1 day
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } else {
            return ResponseEntity.badRequest().body("Invalid Refresh Token");
        }
    }
}
