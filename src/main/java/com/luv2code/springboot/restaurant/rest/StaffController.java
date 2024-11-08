package com.luv2code.springboot.restaurant.rest;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateStaffRequest;
import com.luv2code.springboot.restaurant.dto.UpdateStaffRequest;
import com.luv2code.springboot.restaurant.service.StaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping("/staffs")
    public ResponseEntity<BaseResponse> createStaff(@RequestBody CreateStaffRequest createStaffRequest) {
        // Retrieve the current authenticated user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String createdByUsername = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            createdByUsername = userDetails.getUsername(); // This is the logged-in user's username
        }

        // Call the service to create staff
        BaseResponse response = staffService.createStaff(createStaffRequest);

        // Return appropriate response
        if ("000".equals(response.getErrorCode())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public BaseResponse updateStaff(@PathVariable Long id,
                                    @RequestBody UpdateStaffRequest request,
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
