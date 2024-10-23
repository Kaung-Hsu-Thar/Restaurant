package com.luv2code.springboot.restaurant.repo;

import com.luv2code.springboot.restaurant.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepo extends JpaRepository<MenuItem, Long> {

    boolean existsByName(String name);
}
