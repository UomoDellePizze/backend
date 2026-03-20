package com.myapp.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.myapp.backend.service.KeycloakService;
import com.myapp.backend.service.UserService;
import com.myapp.backend.dto.RegisterRequest;
import com.myapp.backend.dto.LoginRequest;
import com.myapp.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    private final UserService userService;
    private final KeycloakService keycloakService;
    public RegisterController(UserService userService, KeycloakService keycloakService) {
        this.userService = userService;
        this.keycloakService = keycloakService;
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        System.out.println(req);
        // 1️⃣ esiste nel DB locale?
        boolean userExists = userService.userExists(req.getId());
        if (!userService.userExists(req.getId())) {
            return ResponseEntity.status(404).body("User not found in DB");
        }

        // 2️⃣ username coerente?
        if (!userService.userExistsByUsername(req.getUsername())) {
            return ResponseEntity.status(401).body("Username mismatch");
        }

        // 3️⃣ esiste in Keycloak?
        if (!keycloakService.userExists(req.getId())) {
            return ResponseEntity.status(401).body("User not found in Keycloak");
        }

        return ResponseEntity.ok("User exists in both systems");
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        if (req.getEmail() == null || req.getUsername() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body("Email, username, and password are required");
        }

        try {
            userService.registerUser(req);
            return ResponseEntity.ok("User registered successfully");

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Registration failed: " + e.getMessage());
        }
    }
}