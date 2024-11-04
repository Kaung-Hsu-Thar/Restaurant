package com.luv2code.springboot.restaurant.rest;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateMenuItemRequest;
import com.luv2code.springboot.restaurant.service.MenuItemService;
import com.luv2code.springboot.restaurant.service.MenuItemServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menu-items")
public class MenuItemController {
    @Autowired
    private MenuItemService menuItemService;

    @GetMapping
    public BaseResponse getAllMenuItems() {
        return menuItemService.getAllMenuItems();
    }

    @PostMapping
    public BaseResponse createMenuItem(@RequestBody CreateMenuItemRequest request) {
        return menuItemService.createMenuItem(request);
    }

    @PutMapping("/{id}")
    public BaseResponse updateMenuItem(@PathVariable Long id, @RequestBody CreateMenuItemRequest request) {
        return menuItemService.updateMenuItem(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
    }
}

