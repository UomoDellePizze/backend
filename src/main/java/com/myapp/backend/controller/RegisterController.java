package com.myapp.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.myapp.backend.service.UserService;
import com.myapp.backend.dto.RegisterRequest;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            // Validate request
            if (req.getEmail() == null || req.getUsername() == null || req.getPassword() == null) {
                return ResponseEntity.badRequest().body("Email, username, and password are required");
            }

            userService.registerUser(req);

            return ResponseEntity.ok().body("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Registration failed: " + e.getMessage());
        }
    }
}