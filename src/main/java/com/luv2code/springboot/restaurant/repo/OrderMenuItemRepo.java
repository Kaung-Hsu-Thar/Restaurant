package com.luv2code.springboot.restaurant.repo;

import com.luv2code.springboot.restaurant.entity.Order;
import com.luv2code.springboot.restaurant.entity.OrderMenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderMenuItemRepo extends JpaRepository<OrderMenuItem, Long> {

    void deleteByOrderId(Long orderId);

    List<OrderMenuItem> findByOrderId(Long id);
}
