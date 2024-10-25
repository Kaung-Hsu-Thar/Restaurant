package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateMenuItemRequest;

public interface MenuItemService {

     BaseResponse getAllMenuItems();

     BaseResponse createMenuItem(CreateMenuItemRequest request);

    BaseResponse updateMenuItem(Long id, CreateMenuItemRequest request);

    void deleteMenuItem(Long id);
}
