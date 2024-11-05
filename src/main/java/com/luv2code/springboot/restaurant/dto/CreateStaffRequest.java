package com.luv2code.springboot.restaurant.dto;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data

public class CreateStaffRequest {

    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private String phoneNo;

    private String position;

    private String password;

    private String role;



}
