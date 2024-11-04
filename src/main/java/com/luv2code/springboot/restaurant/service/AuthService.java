package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateAuthRequest;
import com.luv2code.springboot.restaurant.dto.StaffResponse;
import com.luv2code.springboot.restaurant.entity.Staff;
import com.luv2code.springboot.restaurant.repo.StaffRepo;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
                staff.getName(),
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

/*
package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.CreateAuthRequest;
import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.RolePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.util.List;

@Service
public class AuthService {

    private final KeycloakService keycloakService;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    private final RestTemplate restTemplate = new RestTemplate();

    public AuthService(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }


    public BaseResponse authenticateUser(CreateAuthRequest authRequest) {
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", keycloakUrl, realm);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = String.format("client_id=%s&client_secret=%s&grant_type=password&username=%s&password=%s&scope=openid profile email",
                keycloakService.getClientId(), keycloakService.getClientSecret(), authRequest.getUsername(), authRequest.getPassword());


        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonObject = new JSONObject(response.getBody());
                logger.debug("Keycloak response: {}", jsonObject.toString());

                String accessToken = jsonObject.getString("access_token"); // Ensure this is defined
                String userId = jsonObject.getString("sub");

                logger.info("User {} authenticated successfully.", authRequest.getUsername());

                List<RolePayload> roles = keycloakService.getRealmRoles(userId, accessToken);

                // Check if the user has an ADMIN or USER role
                boolean isAdmin = roles.stream().anyMatch(role -> role.getName().equals("ADMIN"));
                boolean isUser = roles.stream().anyMatch(role -> role.getName().equals("USER"));

                if (isAdmin) {
                    logger.info("User {} has ADMIN role.", authRequest.getUsername());
                    return new BaseResponse("000", "Admin login successful", roles);
                } else if (isUser) {
                    logger.info("User {} has USER role.", authRequest.getUsername());
                    return new BaseResponse("001", "User login successful", roles);
                } else {
                    logger.warn("User {} does not have the required role.", authRequest.getUsername());
                    return new BaseResponse("403", "Access denied", null);
                }
            } else {
                logger.error("Authentication failed with status code: {}", response.getStatusCode());
                return new BaseResponse("401", "Authentication failed", null);
            }
        } catch (Exception e) {
            logger.error("Exception occurred during authentication: ", e);
            return new BaseResponse("500", "Authentication service error", null);
        }
    }
}

 */














