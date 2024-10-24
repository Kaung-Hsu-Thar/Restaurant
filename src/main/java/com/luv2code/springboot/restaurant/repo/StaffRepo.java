package com.luv2code.springboot.restaurant.repo;

import com.luv2code.springboot.restaurant.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepo extends JpaRepository<Staff, Long> {



    boolean existsByEmail(String email);
}
