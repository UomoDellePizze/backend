package com.myapp.backend.controller;

import com.myapp.backend.dto.RegisterRequest;
import com.myapp.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest request) {
        userService.registerUser(request);
    }
}