package com.luv2code.springboot.restaurant.dto;

import lombok.Data;

import java.util.List;

@Data
public class RolePayload {
    private String id;
    private String name;
    private String description;
    private boolean composite;
    private boolean clientRole;
    private String containerId;


}
