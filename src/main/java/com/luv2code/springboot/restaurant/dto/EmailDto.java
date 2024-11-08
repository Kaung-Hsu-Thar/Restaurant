package com.luv2code.springboot.restaurant.dto;

import lombok.Data;

@Data
public class EmailDto {
    private String recipientEmail;
    private String token;

    public EmailDto(String recipientEmail, String token, String s) {
        this.recipientEmail = recipientEmail;
        this.token = token;
    }
}
