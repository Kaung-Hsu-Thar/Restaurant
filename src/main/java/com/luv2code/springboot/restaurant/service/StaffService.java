package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateStaffRequest;

public interface StaffService {
    BaseResponse getAllStaff();

    BaseResponse createStaff(CreateStaffRequest request);

    BaseResponse updateStaff(Long id, CreateStaffRequest request);

    void deleteStaff(Long id);
}
