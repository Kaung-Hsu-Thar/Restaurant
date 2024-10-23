package com.luv2code.springboot.restaurant.repo;

import com.luv2code.springboot.restaurant.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category, Long> {

    boolean existsByName(String name);
}
