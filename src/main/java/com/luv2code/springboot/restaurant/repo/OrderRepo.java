package com.luv2code.springboot.restaurant.repo;

import com.luv2code.springboot.restaurant.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {

}
