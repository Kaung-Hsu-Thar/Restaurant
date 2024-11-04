package com.luv2code.springboot.restaurant.dto;

import lombok.Data;

import java.util.List;

@Data
public class StaffResponse {
    private Long id;
    private String name;
    private String email;
    private String username;
    private String phoneNo;
    private String position;
    private List<String> roles; // List of role names

    // Constructor can be added for convenience
    public StaffResponse(Long id, String name, String email,String username, String phoneNo, String position, List<String> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.phoneNo = phoneNo;
        this.position = position;
        this.roles = roles;
    }
}
