package com.luv2code.springboot.restaurant.rest;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateAuthRequest;
import com.luv2code.springboot.restaurant.dto.StaffResponse;
import com.luv2code.springboot.restaurant.entity.Staff;
import com.luv2code.springboot.restaurant.service.AuthService;
import com.luv2code.springboot.restaurant.service.StaffService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public BaseResponse authenticateUser(@RequestBody CreateAuthRequest createAuthRequest) {
        // Call AuthService to authenticate and retrieve the response
        BaseResponse response = authService.authenticateUser(createAuthRequest);

        // Check if authentication was successful
        if ("000".equals(response.getErrorCode())) {
            // Get the token and user details from the response
            Object result = response.getResult();
            if (result instanceof Map) {
                Map<String, Object> responseData = (Map<String, Object>) result;
                String token = (String) responseData.get("token"); // Adjust based on the structure
                StaffResponse staffResponse = (StaffResponse) responseData.get("user");

                // Return the response with the token and user details
                return new BaseResponse("000", "Authentication successful", new AuthResponse(token, staffResponse));
            }
        }

        return response; // Return the original error response if authentication failed
    }

    // DTO to structure the login response
    @Data
    public static class AuthResponse {
        private String token;
        private StaffResponse user;

        public AuthResponse(String token, StaffResponse user) {
            this.token = token;
            this.user = user;
        }
    }
}


/*
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

 */
