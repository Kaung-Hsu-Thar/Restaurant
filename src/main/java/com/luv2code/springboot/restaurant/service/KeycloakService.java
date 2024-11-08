package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.RolePayload;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class KeycloakService {

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.username}")
    private String adminUsername;

    @Value("${keycloak.password}")
    private String adminPassword;

    @Autowired
    private EmailService emailService;

    private RestTemplate restTemplate = new RestTemplate();

    // Method to get the admin access token
    private String getAdminAccessToken() {
        String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = String.format("client_id=%s&client_secret=%s&grant_type=password&username=%s&password=%s",
                clientId, clientSecret, adminUsername, adminPassword);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);

        return extractAccessToken(response);
    }

    // Helper method to extract access token
    private String extractAccessToken(ResponseEntity<String> response) {
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonObject = new JSONObject(response.getBody());
            return jsonObject.getString("access_token");
        } else {
            log.error("Failed to retrieve access token, status code: {}", response.getStatusCode());
            return null;
        }
    }
    // Method to get a role by its name
    public RolePayload getByRoleId(String roleName) {
        String accessToken = getAdminAccessToken();
        if (accessToken == null) {
            return null;
        }

        String rolesUrl = keycloakUrl + "/admin/realms/" + realm + "/roles";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<RolePayload>> response = restTemplate.exchange(rolesUrl, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<List<RolePayload>>() {});
            return findRoleByName(response.getBody(), roleName);
        } catch (Exception e) {
            log.error("Error retrieving role by name: {}", e.getMessage());
            return null;
        }
    }
    private RolePayload findRoleByName(List<RolePayload> roles, String roleName) {
        if (roles != null) {
            return roles.stream()
                    .filter(role -> role.getName().trim().equalsIgnoreCase(roleName.trim()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public String authenticate(String username, String password) {
        String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = String.format("client_id=%s&grant_type=password&username=%s&password=%s",
                clientId, username, password);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);

        return extractAccessToken(response);
    }

   public boolean assignRoleToUser(String userId, String roleName) {
        String accessToken = getAdminAccessToken();
        if (accessToken == null) {
            return false;
        }

        if (!isUserExists(userId, accessToken)) {
            log.error("User '{}' not found in Keycloak", userId);
            return false;
        }

        RolePayload rolePayload = getByRoleId(roleName);
        if (rolePayload == null) {
            log.error("Role '{}' not found for assignment to user '{}'", roleName, userId);
            return false;
        }

        return assignRole(userId, rolePayload, accessToken);
    }

    private boolean isUserExists(String userId, String accessToken) {
        String userUrl = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        try {
            ResponseEntity<String> userResponse = restTemplate.exchange(userUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            return userResponse.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.error("Failed to retrieve user '{}': {}", userId, e.getMessage());
            return false;
        }
    }
    private boolean assignRole(String userId, RolePayload rolePayload, String accessToken) {
        String roleUrl = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<List<RolePayload>> entity = new HttpEntity<>(List.of(rolePayload), headers);

        try {
            ResponseEntity<String> roleResponse = restTemplate.exchange(roleUrl, HttpMethod.POST, entity, String.class);
            return roleResponse.getStatusCode() == HttpStatus.NO_CONTENT;
        } catch (Exception e) {
            log.error("Failed to assign role: {}", e.getMessage());
            return false;
        }
    }

    // Method to create passwordless user
    public String createUser(String email, String username, String firstName, String lastName, String password) {
        String accessToken = getAdminAccessToken();
        if (accessToken == null) return null;

        String userUrl = keycloakUrl + "/admin/realms/" + realm + "/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject userJson = new JSONObject();
        userJson.put("enabled", true);
        userJson.put("username", username);
        userJson.put("email", email);
        userJson.put("firstName", firstName);
        userJson.put("lastName", lastName);

        // Add password to the Keycloak user creation JSON
        JSONObject credentials = new JSONObject();
        credentials.put("type", "password");
        credentials.put("value", password);
        credentials.put("temporary", false);

        userJson.put("credentials", List.of(credentials));

        HttpEntity<String> entity = new HttpEntity<>(userJson.toString(), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(userUrl, entity, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            return extractUserIdFromLocation(response);
        } else {
            log.error("Failed to create Keycloak user, status code: {}", response.getStatusCode());
            return null;
        }
    }

    // Extract userId from the Location header in the response
    private String extractUserIdFromLocation(ResponseEntity<String> response) {
        String location = response.getHeaders().getLocation().toString();
        return location.substring(location.lastIndexOf("/") + 1);
    }

    public boolean resetUserPassword(String userId, String newPassword) {
        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/reset-password";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAdminAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject credentials = new JSONObject();
        credentials.put("type", "password");
        credentials.put("value", newPassword);  // Set new password
        credentials.put("temporary", false);  // Make it permanent

        HttpEntity<String> entity = new HttpEntity<>(credentials.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

        if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
            return true;
        } else {
            log.error("Failed to reset password for user '{}': {}", userId, response.getBody());
            return false;
        }
    }

    public boolean updateUserDetails(String keycloakUserId, String newEmail, String newUsername) {
        String updateUrl = keycloakUrl + "/admin/realms/" + realm + "/users/" + keycloakUserId;

        // Create a JSON body with the new email and username
        JSONObject body = new JSONObject();
        body.put("email", newEmail);
        body.put("username", newUsername);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAdminAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

        try {
            // Make the PUT request to update the user
            ResponseEntity<String> response = restTemplate.exchange(updateUrl, HttpMethod.PUT, entity, String.class);
            return response.getStatusCode() == HttpStatus.NO_CONTENT;
        } catch (Exception e) {
            log.error("Failed to update user details: {}", e.getMessage());
            return false;
        }
    }

    public boolean updateUserPassword(String keycloakUserId, String newPassword) {
        String updateUrl = keycloakUrl + "/admin/realms/" + realm + "/users/" + keycloakUserId + "/reset-password";

        // Create a JSON body with the new password
        JSONObject body = new JSONObject();
        body.put("type", "password");
        body.put("value", newPassword);
        body.put("temporary", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAdminAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

        try {
            // Make the PUT request to update the user password
            ResponseEntity<String> response = restTemplate.exchange(updateUrl, HttpMethod.PUT, entity, String.class);
            return response.getStatusCode() == HttpStatus.NO_CONTENT;
        } catch (Exception e) {
            log.error("Failed to update user password: {}", e.getMessage());
            return false;
        }
    }

    public Object getClientId() {
        return clientId;
    }

    public Object getClientSecret() {
        return clientSecret;
    }
}

