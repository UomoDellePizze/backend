package com.myapp.backend.controller;

import com.myapp.backend.entity.User;               
import com.myapp.backend.repository.UserRepository; 

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
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    /**
     * GET /api/me — restituisce info dell'utente loggato dal JWT
     * Angular lo chiama dopo il login per mostrare nome e email sulla Welcome Page
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(Map.of(
            "sub",              jwt.getSubject(),
            "username",         getClaimOrDefault(jwt, "preferred_username", ""),
            "email",            getClaimOrDefault(jwt, "email", ""),
            "firstName",        getClaimOrDefault(jwt, "given_name", ""),
            "lastName",         getClaimOrDefault(jwt, "family_name", ""),
            "emailVerified",    jwt.getClaimAsBoolean("email_verified") != null
                                    ? jwt.getClaimAsBoolean("email_verified")
                                    : false
        ));
    }

    /**
     * GET /api/welcome — endpoint di esempio protetto per la welcome page
     */
    @GetMapping("/welcome")
    //@PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> welcome(@AuthenticationPrincipal Jwt jwt) {
        String username = getClaimOrDefault(jwt, "preferred_username", "Utente");
        return ResponseEntity.ok(Map.of(
            "message", "Benvenuto, " + username + "!",
            "status",  "authenticated"
        ));
    }

    private String getClaimOrDefault(Jwt jwt, String claim, String defaultValue) {
        Object value = jwt.getClaim(claim);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * GET /api/users
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('USER')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * GET /api/users/{id}
     */
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/users
     */
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    /**
     * PUT /api/users/{id}
     */
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {

        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(user.getUsername());
                    existingUser.setEmail(user.getEmail());
                    existingUser.setFirstName(user.getFirstName());
                    existingUser.setLastName(user.getLastName());

                    return ResponseEntity.ok(userRepository.save(existingUser));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
