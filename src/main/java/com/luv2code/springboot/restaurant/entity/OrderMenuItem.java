package com.luv2code.springboot.restaurant.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="order_item")
public class OrderMenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long menuItemId;
    private int quantity;


}
