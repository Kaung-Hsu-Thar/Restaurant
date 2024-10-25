package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateCategoryRequest;

public interface CategoryService {
     BaseResponse getAllCategories();

     BaseResponse createCategory(CreateCategoryRequest request);

     BaseResponse updateCategory(Long id, CreateCategoryRequest request);

     void deleteCategory(Long id);
}
