package com.myapp.backend.service;

import com.myapp.backend.dto.RegisterRequest;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.*;

import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.Collections;

@Service
public class KeycloakService {

    private final Keycloak keycloak;

    public KeycloakService() {

        this.keycloak = KeycloakBuilder.builder()
                .serverUrl("http://localhost:8080")
                .realm("master")
                .clientId("admin-cli")
                .username("admin")
                .password("admin")
                .build();
    }

    public String createUser(RegisterRequest req) {

        UserRepresentation user = new UserRepresentation();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEnabled(true);

        Response response = keycloak
                .realm("myapp")
                .users()
                .create(user);

        String userId = response.getLocation().getPath()
                .replaceAll(".*/([^/]+)$", "$1");

        // password
        CredentialRepresentation password = new CredentialRepresentation();
        password.setTemporary(false);
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue(req.getPassword());

        keycloak.realm("myapp")
                .users()
                .get(userId)
                .resetPassword(password);

        return userId;
    }
}