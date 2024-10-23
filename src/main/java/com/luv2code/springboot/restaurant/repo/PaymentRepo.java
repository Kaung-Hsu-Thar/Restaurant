package com.luv2code.springboot.restaurant.repo;

import com.luv2code.springboot.restaurant.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepo extends JpaRepository<Payment, Long> {

}
