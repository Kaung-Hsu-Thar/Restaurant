package com.luv2code.springboot.restaurant.rest;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateStaffRequest;
import com.luv2code.springboot.restaurant.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("staffs")
@RequiredArgsConstructor
@Slf4j
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public BaseResponse getAllStaff(@AuthenticationPrincipal UserDetails userDetails) {
        return staffService.getAllStaff();
    }

    @PostMapping
    public BaseResponse createStaff(@Valid @RequestBody CreateStaffRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        // Log the creation attempt
        log.info("User {} is attempting to create staff: {} with role: {}", userDetails.getUsername(), request.getName(), request.getRole());
        return staffService.createStaff(request);
    }

    @PutMapping("/{id}")
    public BaseResponse updateStaff(@PathVariable Long id,
                                    @RequestBody CreateStaffRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        return staffService.updateStaff(id, request);
    }

    @DeleteMapping("/{id}")
    public BaseResponse deleteStaff(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        staffService.deleteStaff(id);
        return new BaseResponse("000", "Success", null);
    }
}
