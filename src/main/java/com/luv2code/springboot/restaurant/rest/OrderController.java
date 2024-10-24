package com.luv2code.springboot.restaurant.rest;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateOrderRequest;
import com.luv2code.springboot.restaurant.entity.Order;
import com.luv2code.springboot.restaurant.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public BaseResponse getAllOrders() {
        return orderService.getAllOrders();
    }


    @PostMapping
    public BaseResponse createOrder(@RequestBody List<CreateOrderRequest> orderItems) {
        return orderService.createOrder(orderItems);
    }
    @PutMapping("/{id}")
    public BaseResponse updateOrder(@PathVariable Long id, @RequestBody List<CreateOrderRequest> orderItems, Order.PaymentStatus newStatus) {
        return orderService.updateOrder(id, orderItems,newStatus);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}

