package com.luv2code.springboot.restaurant.rest;

import com.luv2code.springboot.restaurant.dto.RolePayload;
import com.luv2code.springboot.restaurant.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final KeycloakService keycloakService;

    // Endpoint to get a role by role name, secured for ADMIN users
    @GetMapping("/role")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public RolePayload getRoleByName(@RequestParam String roleName) {
        String token = keycloakService.getAdminAccessToken();
        log.info("Access token retrieved: {}", token);
        RolePayload role = keycloakService.getByRoleId(roleName);
        log.info("Retrieved role: {}", role);
        return role;
    }

    // Endpoint to assign roles to a user, secured for ADMIN users
    @PostMapping("/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public boolean assignRoles(@RequestParam String userId, @RequestParam List<String> roles) {
        boolean allSuccess = true; // Track success for all role assignments
        for (String roleName : roles) {
            boolean success = keycloakService.assignRoleToUser(userId, roleName);
            if (success) {
                log.info("Successfully assigned {} role to user with ID: {}", roleName, userId);
            } else {
                log.warn("Failed to assign {} role to user with ID: {}", roleName, userId);
                allSuccess = false; // Mark as failed if any role assignment fails
            }
        }
        return allSuccess;
    }

}
