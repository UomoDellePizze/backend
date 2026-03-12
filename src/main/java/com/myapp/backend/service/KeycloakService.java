package com.myapp.backend.service;

import com.myapp.backend.dto.RegisterRequest;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;

@Service
public class KeycloakService {

    private final Keycloak keycloak;
    private final String realm;

    public KeycloakService(
        // Importa i valori da application.yml
            @Value("${keycloak.auth-server-url}") String serverUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.client-id}") String clientId,
            @Value("${keycloak.admin-username}") String adminUsername,
            @Value("${keycloak.admin-password}") String adminPassword
    ) {
        this.realm = realm;
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")           // sempre master per admin-cli
                .clientId(clientId)
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }

    /**
     * Crea un nuovo utente in Keycloak e imposta la password.
     * @param req dati di registrazione
     * @return id dell'utente creato
     */
    public String createUser(RegisterRequest req) {

        // costruisci la rappresentazione utente
        UserRepresentation user = new UserRepresentation();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEnabled(true);

        // crea utente
        Response response = keycloak.realm(realm).users().create(user);

        // estrai l'id dall'header Location
        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        // imposta password
        keycloak.realm(realm)
                .users()
                .get(userId)
                .resetPassword(createPasswordCredential(req.getPassword()));

        return userId;
    }

    private CredentialRepresentation createPasswordCredential(String password) {
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(false);
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        return cred;
    }
}