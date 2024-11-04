package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateStaffRequest;
import com.luv2code.springboot.restaurant.entity.Role;
import com.luv2code.springboot.restaurant.entity.Staff;

public interface StaffService {
    BaseResponse getAllStaff();

    BaseResponse createStaff(CreateStaffRequest request);

    BaseResponse updateStaff(Long id, CreateStaffRequest request);

    void deleteStaff(Long id);

    Staff getStaffByEmail(String email);

    Staff getStaffByUsername(String username);
}
