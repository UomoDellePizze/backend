package com.myapp.backend.dto;

public class LoginRequest {

    private String keycloakId;       // keycloakId (sub dal JWT)
    private String username; // preferred_username dal JWT

    public String getId() { return keycloakId; }
    public void setId(String keycloakId) { this.keycloakId = keycloakId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
