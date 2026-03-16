package com.myapp.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.myapp.backend.service.UserService;
import com.myapp.backend.dto.RegisterRequest;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
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