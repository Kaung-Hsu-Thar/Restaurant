package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateCategoryRequest;
import com.luv2code.springboot.restaurant.entity.Category;
import com.luv2code.springboot.restaurant.repo.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepo categoryRepo;

    public BaseResponse getAllCategories() {
        return new BaseResponse("000", "Success", categoryRepo.findAll());
    }

    public BaseResponse createCategory(CreateCategoryRequest request) {
        if (categoryRepo.existsByName(request.getName())) {
            return new BaseResponse("001", "Category name existed", null);
        }

        Category category = new Category();
        category.setName(request.getName());
        return new BaseResponse("000", "Success", categoryRepo.save(category));
    }

    public BaseResponse updateCategory(Long id, CreateCategoryRequest request) {
        Category existing = categoryRepo.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        if (categoryRepo.existsByName(request.getName())) {
            return new BaseResponse("001", "Category name existed", null);
        }
        existing.setName(request.getName());
        return new BaseResponse("000", "Success", categoryRepo.save(existing));
    }

    public void deleteCategory(Long id) {
        categoryRepo.deleteById(id);
    }
}
