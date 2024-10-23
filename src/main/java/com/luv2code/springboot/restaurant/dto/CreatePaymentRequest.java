package com.luv2code.springboot.restaurant.dto;

import com.luv2code.springboot.restaurant.entity.Payment;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class CreatePaymentRequest {
    private Long orderId;

    @Enumerated(EnumType.STRING)
    private Payment.PaymentStatus status;

    public CreatePaymentRequest() {
    }

    public CreatePaymentRequest(Long orderId, Payment.PaymentStatus status) {
        this.orderId = orderId;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Payment.PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(Payment.PaymentStatus status) {
        this.status = status;
    }
}
