package com.luv2code.springboot.restaurant.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name="category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    public Category(){

    }


}
