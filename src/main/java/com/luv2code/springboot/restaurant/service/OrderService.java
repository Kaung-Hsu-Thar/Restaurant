package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateOrderRequest;
import com.luv2code.springboot.restaurant.dto.OrderResponseDto;
import com.luv2code.springboot.restaurant.dto.MenuItemDto;
import com.luv2code.springboot.restaurant.entity.MenuItem;
import com.luv2code.springboot.restaurant.entity.Order;
import com.luv2code.springboot.restaurant.entity.OrderMenuItem;
import com.luv2code.springboot.restaurant.entity.Payment;
import com.luv2code.springboot.restaurant.repo.MenuItemRepo;
import com.luv2code.springboot.restaurant.repo.OrderMenuItemRepo;
import com.luv2code.springboot.restaurant.repo.OrderRepo;
import com.luv2code.springboot.restaurant.repo.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class OrderService {
    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private MenuItemRepo menuItemRepo;

    @Autowired
    private OrderMenuItemRepo orderMenuItemRepo;

    @Autowired
    private PaymentRepo paymentRepo;


    public BaseResponse getAllOrders() {
        List<Order> orders = orderRepo.findAll();
        List<OrderResponseDto> orderResponseDtos = new ArrayList<>();

        for (Order order : orders) {
            List<OrderMenuItem> orderMenuItems = orderMenuItemRepo.findByOrderId(order.getId());
            List<MenuItemDto> menuItemDtos = new ArrayList<>();

            for (OrderMenuItem orderMenuItem : orderMenuItems) {
                MenuItem menuItem = menuItemRepo.findById(orderMenuItem.getMenuItemId())
                        .orElseThrow(() -> new RuntimeException("MenuItem not found"));

                // Create a MenuItemDto with the required details
                MenuItemDto menuItemDto = new MenuItemDto(menuItem.getName(), menuItem.getPrice(), orderMenuItem.getQuantity());
                menuItemDtos.add(menuItemDto);
            }

            // Create an OrderResponseDto
            OrderResponseDto orderResponseDto = new OrderResponseDto();
            orderResponseDto.setOrderId(order.getId());
            orderResponseDto.setOrderDate(order.getOrderDate());
            orderResponseDto.setTotalPrice(order.getTotalPrice());
            orderResponseDto.setMenuItems(menuItemDtos);

            orderResponseDtos.add(orderResponseDto);
        }

        return new BaseResponse("000", "success", orderResponseDtos);
    }


    public BaseResponse createOrder(List<CreateOrderRequest> orderItems) {
        double totalPrice = 0;

        for (CreateOrderRequest orderItem : orderItems) {
            if(!menuItemRepo.existsById(orderItem.getMenuItemId())){
                return new BaseResponse("001", "This MenuItem is not existed", null);
            }

            if(orderItem.getQuantities() == 0){
                return new BaseResponse("002", "You must add the quantity", null);
            }

            MenuItem menuItem = menuItemRepo.findById(orderItem.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("MenuItem not found"));
            totalPrice += menuItem.getPrice() * orderItem.getQuantities();
        }

        //create order
        Order order = new Order();
        order.setTotalPrice(totalPrice);

        LocalDate localDate = LocalDate.now();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        order.setOrderDate(date);

        order = orderRepo.save(order);

        //create order + menu item
        for (CreateOrderRequest orderItem : orderItems) {
            OrderMenuItem orderMenuItem = new OrderMenuItem();
            orderMenuItem.setOrderId(order.getId());
            orderMenuItem.setMenuItemId(orderItem.getMenuItemId());
            orderMenuItem.setQuantity(orderItem.getQuantities());
            orderMenuItemRepo.save(orderMenuItem);
        }
        // Automatically create payment for the order
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setStatus(Payment.PaymentStatus.PENDING); // or any initial status you prefer
        paymentRepo.save(payment);
        return new BaseResponse("000", "success", order);
    }

    @Transactional
    public BaseResponse updateOrder(Long orderId, List<CreateOrderRequest> updatedOrderItems) {

        Optional<Order> existingOrderOpt = orderRepo.findById(orderId);
        if (existingOrderOpt.isEmpty()) {
            return new BaseResponse("001", "Order not found", null); // Return a BaseResponse for not found
        }
        Order existingOrder = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));


        double updatedTotalPrice = 0;


        orderMenuItemRepo.deleteByOrderId(orderId);

        List<MenuItemDto> updatedMenuItems = new ArrayList<>();

        for (CreateOrderRequest orderItem : updatedOrderItems) {
            if(!menuItemRepo.existsById(orderItem.getMenuItemId())){
                return new BaseResponse("001", "This MenuItem is not existed", null);
            }

            if(orderItem.getQuantities() == 0){
                return new BaseResponse("002", "You must add the quantity", null);
            }
            MenuItem menuItem = menuItemRepo.findById(orderItem.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("MenuItem not found"));
            updatedTotalPrice += menuItem.getPrice() * orderItem.getQuantities();

            // Create new order + menu item
            OrderMenuItem orderMenuItem = new OrderMenuItem();
            orderMenuItem.setOrderId(orderId);
            orderMenuItem.setMenuItemId(orderItem.getMenuItemId());
            orderMenuItem.setQuantity(orderItem.getQuantities());
            orderMenuItemRepo.save(orderMenuItem);

            updatedMenuItems.add(new MenuItemDto(menuItem.getName(), menuItem.getPrice(), orderItem.getQuantities()));
        }

        existingOrder.setTotalPrice(updatedTotalPrice);
        existingOrder = orderRepo.save(existingOrder);

        return new BaseResponse("000", "success", existingOrder);
    }



    public void deleteOrder(Long id) {
        orderRepo.deleteById(id);
    }
}

