package com.luv2code.springboot.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.sql.exec.spi.StandardEntityInstanceResolver;

import java.util.*;

@Entity
@Data
@Table(name="food_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double totalPrice;

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public enum PaymentStatus {
        WAITING_TO_PAY, CANCELED, PAID
    }


}
