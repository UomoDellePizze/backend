package com.myapp.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.myapp.backend.service.KeycloakService;
import com.myapp.backend.service.UserService;
import com.myapp.backend.dto.RegisterRequest;
import com.myapp.backend.dto.LoginRequest;
import com.myapp.backend.repository.UserRepository;
import com.myapp.backend.debug.Utility;
@RestController
@RequestMapping("/api/auth")
public class RegisterController{
    private final UserService userService;
    private final KeycloakService keycloakService;
    public RegisterController(UserService userService, KeycloakService keycloakService) {
        System.out.println("\n \u001B[31mRegister Controller started\u001B[0m");
        this.keycloakService = keycloakService;
        this.userService = userService;
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RegisterRequest req) {
        Utility.debug(req.getUsername());

        // esiste in Keycloak?
        if (!keycloakService.userExists(req.getUsername())) {
            Utility.error("Username not found in Keycloak: "+req.getUsername());
            return ResponseEntity.status(401).body("User not found in Keycloak");
        }// username coerente?
        if (!userService.userExistsByUsername(req.getUsername())) {
            Utility.error("Username not found: "+req.getUsername());
            return ResponseEntity.status(401).body("Username not found");
        }
        Utility.warn("User exists");
        return ResponseEntity.ok("User exists in both systems");
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        Utility.warn("Register: "+req.getUsername());
        if (req.getEmail() == null || req.getUsername() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body("Email, username, and password are required");
        }

        try {
            userService.registerUser(req);
            return ResponseEntity.ok("User registered successfully");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Registration failed: " + e.getMessage());
        }
    }
}