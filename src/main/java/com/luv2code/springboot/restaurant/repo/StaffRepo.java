package com.luv2code.springboot.restaurant.repo;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.entity.Role;
import com.luv2code.springboot.restaurant.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepo extends JpaRepository<Staff, Long> {

    Staff findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneNo(String name);

    boolean existsByPassword(String password);

    Staff findByUsername(String username);
}
