package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateOrderRequest;
import com.luv2code.springboot.restaurant.dto.OrderResponseDto;
import com.luv2code.springboot.restaurant.dto.MenuItemDto;
import com.luv2code.springboot.restaurant.entity.MenuItem;
import com.luv2code.springboot.restaurant.entity.Order;
import com.luv2code.springboot.restaurant.entity.OrderMenuItem;
import com.luv2code.springboot.restaurant.repo.MenuItemRepo;
import com.luv2code.springboot.restaurant.repo.OrderMenuItemRepo;
import com.luv2code.springboot.restaurant.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private MenuItemRepo menuItemRepo;

    @Autowired
    private OrderMenuItemRepo orderMenuItemRepo;

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

            orderResponseDto.setStatus(
                    order.getStatus() != null ? order.getStatus().name() : "UNKNOWN"
            );

            orderResponseDtos.add(orderResponseDto);
        }

        return new BaseResponse("000", "success", orderResponseDtos);
    }


    @Transactional
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

        order.setStatus(Order.PaymentStatus.WAITING_TO_PAY);

        order = orderRepo.save(order);

        //create order + menu item
        for (CreateOrderRequest orderItem : orderItems) {
            OrderMenuItem orderMenuItem = new OrderMenuItem();
            orderMenuItem.setOrderId(order.getId());
            orderMenuItem.setMenuItemId(orderItem.getMenuItemId());
            orderMenuItem.setQuantity(orderItem.getQuantities());
            orderMenuItemRepo.save(orderMenuItem);
        }

        return new BaseResponse("000", "success", order);

    }

    @Transactional
    public BaseResponse updateOrder(Long orderId, List<CreateOrderRequest> updatedOrderItems, Order.PaymentStatus newStatus) {
        Optional<Order> existingOrderOpt = orderRepo.findById(orderId);
        if (existingOrderOpt.isEmpty()) {
            return new BaseResponse("001", "Order not found", null);
        }

        Order existingOrder = existingOrderOpt.get();

        if (newStatus == Order.PaymentStatus.CANCELED) {
            // Delete the existing order
            orderRepo.deleteById(orderId);
            return new BaseResponse("000", "Order cancelled and deleted successfully", null);
        }

        if (newStatus == Order.PaymentStatus.PAID) {
            // Only update the status to PAID, no need for CreateOrderRequest
            existingOrder.setStatus(Order.PaymentStatus.PAID);
            existingOrder = orderRepo.save(existingOrder);
            return new BaseResponse("000", "Thanks For Your Purchase", existingOrder);
        }

        if (newStatus == Order.PaymentStatus.WAITING_TO_PAY) {
            // Update the order details if status is WAITING_TO_PAY
            if (updatedOrderItems == null || updatedOrderItems.isEmpty()) {
                return new BaseResponse("002", "No order items provided for update", null);
            }

            // Update total price
            double updatedTotalPrice = 0;

            // Delete existing OrderMenuItems
            orderMenuItemRepo.deleteByOrderId(orderId);

            // Create new OrderMenuItems
            for (CreateOrderRequest orderItem : updatedOrderItems) {
                if (!menuItemRepo.existsById(orderItem.getMenuItemId())) {
                    return new BaseResponse("001", "This MenuItem is not existed", null);
                }

                if (orderItem.getQuantities() == 0) {
                    return new BaseResponse("002", "You must add the quantity", null);
                }

                MenuItem menuItem = menuItemRepo.findById(orderItem.getMenuItemId())
                        .orElseThrow(() -> new RuntimeException("MenuItem not found"));

                updatedTotalPrice += menuItem.getPrice() * orderItem.getQuantities();

                OrderMenuItem orderMenuItem = new OrderMenuItem();
                orderMenuItem.setOrderId(orderId);
                orderMenuItem.setMenuItemId(orderItem.getMenuItemId());
                orderMenuItem.setQuantity(orderItem.getQuantities());
                orderMenuItemRepo.save(orderMenuItem);
            }

            existingOrder.setTotalPrice(updatedTotalPrice);
            existingOrder.setStatus(newStatus);
            existingOrder = orderRepo.save(existingOrder);
        }
        return new BaseResponse("000", "success", existingOrder);
    }


    @Transactional
    public BaseResponse updateOrderStatus(Long orderId, Order.PaymentStatus newStatus) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(newStatus);
        orderRepo.save(order);

        return new BaseResponse("000", "Order status updated successfully", order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        orderRepo.deleteById(id);
    }
}

