package com.luv2code.springboot.restaurant.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;


@Entity
@Data
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    private String token;
    private String email;
    private Date createdDate;

    public PasswordResetToken() {
        this.createdDate = new Date();
    }


}
