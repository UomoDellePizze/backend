package com.myapp.backend.dto;

public class LoginRequest {
    private String id;
    private String username;

    // getters and setters
    public String getId(){return id;}
    public void setId(String id){this.id=id;}
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}