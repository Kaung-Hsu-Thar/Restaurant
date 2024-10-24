package com.luv2code.springboot.restaurant.rest;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateStaffRequest;
import com.luv2code.springboot.restaurant.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("staffs")
public class StaffController {
    @Autowired
    private StaffService staffService;

    @GetMapping
    public BaseResponse getAllStaff(){
        return staffService.getAllStaff();
    }

    @PostMapping
    public BaseResponse createStaff(@RequestBody CreateStaffRequest request){
        return staffService.createStaff(request);
    }

    @PutMapping("/{id}")
    public BaseResponse updateStaff(@PathVariable Long id, @RequestBody CreateStaffRequest request){
        return staffService.updateStaff(id, request);
    }

    @DeleteMapping("{id}")
    public void deleteStaff(@PathVariable Long id){
        staffService.deleteStaff(id);
    }
}
