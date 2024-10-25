package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateOrderRequest;
import com.luv2code.springboot.restaurant.entity.Order;

import java.util.List;

public interface OrderService {
    BaseResponse getAllOrders();

    BaseResponse createOrder(List<CreateOrderRequest> orderItems);

    BaseResponse updateOrder(Long orderId, List<CreateOrderRequest> updatedOrderItems, Order.PaymentStatus newStatus);

    BaseResponse updateOrderStatus(Long orderId, Order.PaymentStatus newStatus);

    void deleteOrder(Long id);
}
