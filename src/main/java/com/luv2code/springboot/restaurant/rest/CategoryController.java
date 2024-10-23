package com.luv2code.springboot.restaurant.rest;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateCategoryRequest;
import com.luv2code.springboot.restaurant.entity.Category;
import com.luv2code.springboot.restaurant.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public BaseResponse getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping
    public BaseResponse createCategory(@RequestBody CreateCategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @PutMapping("/{id}")
    public BaseResponse updateCategory(@PathVariable Long id, @RequestBody CreateCategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}

