package com.luv2code.springboot.restaurant.dto;

public class CreateMenuItemRequest {
    private String name;
    private Double price;
    private Long categoryId;

    public CreateMenuItemRequest() {
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
