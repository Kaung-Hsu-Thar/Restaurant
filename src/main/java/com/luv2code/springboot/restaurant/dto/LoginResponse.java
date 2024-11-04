package com.luv2code.springboot.restaurant.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {
    private String accessToken;
    private List<String> roles; // User roles from Keycloak

    public LoginResponse(String accessToken, List<String> roles) {
        this.accessToken = accessToken;
        this.roles = roles;
    }
}
