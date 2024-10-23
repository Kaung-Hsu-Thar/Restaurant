package com.luv2code.springboot.restaurant.dto;

import lombok.Getter;

import java.util.Set;

@Getter
public class CreateCategoryRequest {
    private String name;

    public CreateCategoryRequest() {
    }

    public void setName(String name) {
        this.name = name;
    }
}

