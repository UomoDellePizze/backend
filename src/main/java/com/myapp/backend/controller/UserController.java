package com.myapp.backend.controller;

import com.myapp.backend.entity.User;
import com.myapp.backend.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =========================
    // UTENTE LOGGATO
    // =========================
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {

        boolean userExists = userService.userExists(jwt.getSubject());
        if (!userExists) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(
            Map.of(
                "sub", jwt.getSubject(),
                "username", getClaimOrDefault(jwt, "preferred_username", ""),
                "email", getClaimOrDefault(jwt, "email", ""),
                "firstName", getClaimOrDefault(jwt, "given_name", ""),
                "lastName", getClaimOrDefault(jwt, "family_name", ""),
                "emailVerified",
                    jwt.getClaimAsBoolean("email_verified") != null
                        ? jwt.getClaimAsBoolean("email_verified")
                        : false
            )
        );
    }

    // =========================
    // WELCOME
    // =========================
    @GetMapping("/welcome")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> welcome(@AuthenticationPrincipal Jwt jwt) {
        String username = getClaimOrDefault(jwt, "preferred_username", "Utente");

        return ResponseEntity.ok(Map.of(
            "message", "Benvenuto, " + username + "!",
            "status", "authenticated"
        ));
    }

    private String getClaimOrDefault(Jwt jwt, String claim, String defaultValue) {
        Object value = jwt.getClaim(claim);
        return value != null ? value.toString() : defaultValue;
    }

    // =========================
    // CRUD USERS
    // =========================

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {

        return userService.updateUser(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {

        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}