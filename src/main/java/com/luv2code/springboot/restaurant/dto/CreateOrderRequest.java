package com.luv2code.springboot.restaurant.dto;

public class CreateOrderRequest {
    private Long menuItemId;
    private int quantities;

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public int getQuantities() {
        return quantities;
    }

    public void setQuantities(int quantities) {
        this.quantities = quantities;
    }
}

