package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateMenuItemRequest;
import com.luv2code.springboot.restaurant.entity.MenuItem;
import com.luv2code.springboot.restaurant.repo.CategoryRepo;
import com.luv2code.springboot.restaurant.repo.MenuItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuItemService {
    @Autowired
    private MenuItemRepo menuItemRepo;

    @Autowired
    private CategoryRepo categoryRepo;


    public BaseResponse getAllMenuItems() {
        return new BaseResponse("000", "success", menuItemRepo.findAll());
    }

    public BaseResponse createMenuItem(CreateMenuItemRequest request) {
        if(menuItemRepo.existsByName(request.getName())){
            return new BaseResponse("001", "This MenuItem is already existed", null);
        }

        if (request.getPrice() == 0) {
            return new BaseResponse("002", "Price cannot be zero", null);
        }

        if (!categoryRepo.existsById(request.getCategoryId())) {
            return new BaseResponse("003", "Category ID does not exist", null);
        }

        MenuItem menuItem = new MenuItem();
        menuItem.setName(request.getName());
        menuItem.setPrice((request.getPrice()));
        menuItem.setCategoryId(request.getCategoryId());
        return new BaseResponse("000", "success", menuItemRepo.save(menuItem));
    }

    public BaseResponse updateMenuItem(Long id, CreateMenuItemRequest request) {
        MenuItem existing = menuItemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu Item not found"));
        if(menuItemRepo.existsByName(request.getName())){
            return new BaseResponse("001", "This MenuItem is already existed",null);
        }

        if (request.getPrice() == 0) {
            return new BaseResponse("002", "Price cannot be zero", null);
        }

        if (!categoryRepo.existsById(request.getCategoryId())) {
            return new BaseResponse("003", "Category ID does not exist", null);
        }

        existing.setName(request.getName());
        existing.setPrice(request.getPrice());
        existing.setCategoryId(request.getCategoryId());
        return new BaseResponse("000", "success" ,menuItemRepo.save(existing));
    }

    public void deleteMenuItem(Long id) {
        menuItemRepo.deleteById(id);
    }
}

