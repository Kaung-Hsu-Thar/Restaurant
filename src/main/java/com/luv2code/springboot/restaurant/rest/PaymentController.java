package com.luv2code.springboot.restaurant.rest;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreatePaymentRequest;
import com.luv2code.springboot.restaurant.entity.Payment;
import com.luv2code.springboot.restaurant.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public BaseResponse getAllPayments() {
        return paymentService.getAllPayments();
    }

    /*
    @PostMapping
    public BaseResponse createPayment(@RequestParam Long orderId) {
        return paymentService.createPayment(orderId);
    }
     */

    @PutMapping("/{id}")
    public BaseResponse updatePayment(@PathVariable Long id, @RequestBody Payment.PaymentStatus status) {
        return paymentService.updatePaymentStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
    }
}

