package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateAuthRequest;
import com.luv2code.springboot.restaurant.dto.StaffResponse;
import com.luv2code.springboot.restaurant.entity.Staff;
import com.luv2code.springboot.restaurant.repo.StaffRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final StaffService staffService; // To fetch staff details
    private final KeycloakService keycloakService; // To interact with Keycloak
    private final StaffRepo staffRepo; // Repository for staff entity

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Transactional
    public BaseResponse authenticateUser(CreateAuthRequest createAuthRequest) {
        // Call Keycloak to authenticate user and retrieve a token
        String token = keycloakService.authenticate(createAuthRequest.getUsername(), createAuthRequest.getPassword());

        if (token == null) {
            return new BaseResponse("401", "Authentication failed", null);
        }

        // Fetch user details from your staff database using the email
        Staff staff = staffService.getStaffByUsername(createAuthRequest.getUsername());

        if (staff == null) {
            return new BaseResponse("404", "User not found", null);
        }

        List<String> roleNames = staff.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        // Create the StaffResponse object with the desired fields
        StaffResponse staffResponse = new StaffResponse(
                staff.getId(),
                staff.getEmail(),
                staff.getUsername(),
                staff.getPhoneNo(),
                staff.getPosition(),
                roleNames
        );
        // Create a response map or object that contains both the token and user details
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        responseData.put("user", staffResponse);

        return new BaseResponse("000", "Authentication successful", responseData);
    }
}












