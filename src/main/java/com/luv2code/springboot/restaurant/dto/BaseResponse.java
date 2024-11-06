package com.luv2code.springboot.restaurant.dto;

import lombok.Data;

@Data
public class BaseResponse {
    private String errorCode;
    private String message;
    private Object result;
    private String token;

    // 3-parameter constructor (Existing one)
    public BaseResponse(String code, String message, Object data) {
        this.errorCode = code;
        this.message = message;
        this.result = data;
    }

    // 4-parameter constructor (New one with token)
    public BaseResponse(String code, String message, Object data, String token) {
        this.errorCode = code;
        this.message = message;
        this.result = data;
        this.token = token;
    }
}
