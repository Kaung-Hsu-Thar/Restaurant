package com.luv2code.springboot.restaurant.repo;

import com.luv2code.springboot.restaurant.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    Role save(Role role);

    boolean existsByName(String admin);
}
