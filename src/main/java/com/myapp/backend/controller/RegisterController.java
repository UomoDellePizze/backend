package com.myapp.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.myapp.backend.service.KeycloakService;
import com.myapp.backend.service.UserService;
import com.myapp.backend.dto.RegisterRequest;
import com.myapp.backend.dto.LoginRequest;
import com.myapp.backend.debug.Utility;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    private final UserService userService;
    private final KeycloakService keycloakService;

    public RegisterController(UserService userService, KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
        this.userService = userService;
    }

    /**
     * Chiamato automaticamente da Angular dopo il login Keycloak.
     * Riceve sub (keycloakId) e username dal JWT già parsato lato client.
     * Verifica solo l'esistenza nel DB locale, non fa query a Keycloak.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Utility.debug("Login request received: " + req.getUsername());
        Utility.debug("Auto-login check per sub: " + req.getId());
        /*
        if (req.getId() == null || req.getId().isBlank()) {
            return ResponseEntity.badRequest().body("Keycloak ID mancante");
        }
        */
        if (!userService.userExists(req.getId())) {
            Utility.error("Utente non trovato nel DB: " + req.getId());
            return ResponseEntity.status(401).body("Utente non trovato nel DB");
        }

        Utility.info("Login verificato per: " + req.getUsername());
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        Utility.warn("Register: " + req.getUsername());

        if (req.getEmail() == null || req.getUsername() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body("Email, username e password sono obbligatori");
        }

        try {
            userService.registerUser(req);
            return ResponseEntity.ok("Utente registrato con successo");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Registrazione fallita: " + e.getMessage());
        }
    }
}
