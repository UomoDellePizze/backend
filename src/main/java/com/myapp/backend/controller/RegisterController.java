package com.myapp.backend.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.myapp.backend.user.User;
import com.myapp.backend.user.UserRepository;
import com.myapp.backend.keycloak.KeycloakService;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    @Autowired
    private KeycloakService keycloakService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            // Validate request
            if (req.getEmail() == null || req.getUsername() == null || req.getPassword() == null) {
                return ResponseEntity.badRequest().body("Email, username, and password are required");
            }

            // Create user in Keycloak
            String keycloakId = keycloakService.createUser(req);

            // Create user in database
            User user = new User();
            user.setKeycloakId(keycloakId);
            user.setEmail(req.getEmail());
            user.setUsername(req.getUsername());

            userRepository.save(user);

            return ResponseEntity.ok().body("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Registration failed: " + e.getMessage());
        }
    }
}