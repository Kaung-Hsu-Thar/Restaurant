package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreatePaymentRequest;
import com.luv2code.springboot.restaurant.entity.Payment;
import com.luv2code.springboot.restaurant.repo.OrderRepo;
import com.luv2code.springboot.restaurant.repo.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private OrderRepo orderRepo;

    public BaseResponse getAllPayments() {
        return new BaseResponse("000", "success",  paymentRepo.findAll());
    }
/*
    public BaseResponse createPayment(Long orderId) {
        orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));


        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setStatus(Payment.PaymentStatus.PENDING);

        return new BaseResponse("000", "success", paymentRepo.save(payment));
    }

*/
    public BaseResponse updatePaymentStatus(Long paymentId, Payment.PaymentStatus status) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));


        payment.setStatus(status);

        return new BaseResponse("000", "success", paymentRepo.save(payment));
    }

    public void deletePayment(Long id) {
        paymentRepo.deleteById(id);
    }
}

