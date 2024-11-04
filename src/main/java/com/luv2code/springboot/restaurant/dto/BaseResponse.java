package com.luv2code.springboot.restaurant.dto;

import lombok.Data;

@Data
public class BaseResponse {
    private String errorCode;
    private String message;
    private Object result;

    public BaseResponse(String errorCode, String message, Object result) {
        this.errorCode = errorCode;
        this.message = message;
        this.result = result;
    }
}
