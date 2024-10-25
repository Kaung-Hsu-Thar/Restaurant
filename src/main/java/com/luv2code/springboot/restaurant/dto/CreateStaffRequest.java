package com.luv2code.springboot.restaurant.dto;

import lombok.Data;

@Data
public class CreateStaffRequest {

    private String name;

    private String email;

    private String phoneNo;

    private String position;
}
