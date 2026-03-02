package com.myapp.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

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
    @PreAuthorize("hasRole('USER')")
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
}
