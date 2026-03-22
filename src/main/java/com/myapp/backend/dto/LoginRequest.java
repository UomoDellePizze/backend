package com.myapp.backend.dto;

public class LoginRequest {
    private String id;
    private String username;
    private String email;
    // getters and setters
    public String getEmail(){return email;}

    public void setEmail(String email){this.email = email;}

    public String getId(){return id;}
    
    public void setId(String id){this.id=id;}
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}