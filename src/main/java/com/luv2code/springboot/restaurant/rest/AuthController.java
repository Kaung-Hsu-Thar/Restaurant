package com.luv2code.springboot.restaurant.rest;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateAuthRequest;
import com.luv2code.springboot.restaurant.entity.Staff;
import com.luv2code.springboot.restaurant.service.AuthService;
import com.luv2code.springboot.restaurant.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final StaffService staffService;
    private final AuthService authService;

    // Endpoint to fetch user details after authentication
    @GetMapping("/user")
    public BaseResponse getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        Staff staff = staffService.getStaffByEmail(userDetails.getUsername());

        if (staff != null) {
            return new BaseResponse("000", "User details fetched", staff);
        }

        return new BaseResponse("404", "User not found", null);
    }

    @PostMapping("/login")
    public BaseResponse authenticateUser(@RequestBody CreateAuthRequest createAuthRequest){

        return authService.authenticateUser(createAuthRequest);
    }
}
