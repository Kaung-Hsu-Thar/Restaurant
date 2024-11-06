package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateStaffRequest;
import com.luv2code.springboot.restaurant.dto.StaffResponse;
import com.luv2code.springboot.restaurant.dto.UpdateStaffRequest;
import com.luv2code.springboot.restaurant.entity.PasswordResetToken;
import com.luv2code.springboot.restaurant.entity.Role;
import com.luv2code.springboot.restaurant.entity.Staff;
import com.luv2code.springboot.restaurant.repo.PasswordResetTokenRepo;
import com.luv2code.springboot.restaurant.repo.RoleRepo;
import com.luv2code.springboot.restaurant.repo.StaffRepo;
import com.luv2code.springboot.restaurant.repo.StaffRoleRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StaffServiceImpl implements StaffService {

    private final StaffRepo staffRepo;
    private final RoleRepo roleRepo;
    private final StaffRoleRepo staffRoleRepo;
    private final KeycloakService keycloakService;
    private final PasswordResetTokenRepo passwordResetTokenRepo;

    @Autowired
    private EmailService emailService;

    

    public BaseResponse getAllStaff() {
        return new BaseResponse("000", "Success", staffRepo.findAll());
    }

    public BaseResponse createStaff(CreateStaffRequest request) {
        // Validate name
        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            return new BaseResponse("002", "Name cannot be empty", null);
        }

        // Validate email
        if (request.getEmail() == null || !request.getEmail().contains("@") || !request.getEmail().contains(".com")) {
            return new BaseResponse("002", "Invalid email.", null);
        }

        if (staffRepo.existsByEmail(request.getEmail())) {
            return new BaseResponse("003", "This email already exists", null);
        }

        // Validate phone number
        String phoneNoStr = String.valueOf(request.getPhoneNo());
        if (!phoneNoStr.startsWith("09") || phoneNoStr.length() != 11) {
            return new BaseResponse("004", "Invalid phone number. It should start with '09' and be 11 digits long", null);
        }

        if (staffRepo.existsByPhoneNo(request.getPhoneNo())) {
            return new BaseResponse("005", "This phone number already exists", null);
        }

        // Validate role
        Optional<Role> roleOpt = roleRepo.findByName(request.getRole());
        if (roleOpt.isEmpty()) {
            return new BaseResponse("008", "Role not found", null);
        }

        // Create the user in Keycloak without a password
        String keycloakUserId = keycloakService.createPasswordlessUser(
                request.getEmail(), request.getUsername(), request.getFirstName(), request.getLastName()
        );

        if (keycloakUserId == null) {
            return new BaseResponse("005", "Failed to create user in Keycloak", null);
        }

        // Assign role to the user in Keycloak
        boolean roleAssigned = keycloakService.assignRoleToUser(keycloakUserId, request.getRole());
        if (!roleAssigned) {
            return new BaseResponse("010", "Failed to assign role in Keycloak", null);
        }

        // Create staff without password (temporarily set it as null)
        Staff staff = new Staff();
        staff.setFirstName(request.getFirstName());
        staff.setLastName(request.getLastName());
        staff.setEmail(request.getEmail());
        staff.setUsername(request.getUsername());
        staff.setPhoneNo(request.getPhoneNo());
        staff.setPosition(request.getPosition());
        staff.setPassword(null); // No password initially
        staff.getRoles().add(roleOpt.get());

        // Set Keycloak user ID
        staff.setAccountId(keycloakUserId);  // Assuming an accountId field in Staff

        // Save the staff (without password) to the database
        staff = staffRepo.save(staff);

        // Generate and send password reset link to the staff email
        String token = generatePasswordResetToken(staff.getEmail());
        emailService.sendPasswordResetEmail(staff.getEmail(), token);

        return new BaseResponse("000", "Staff created. Please check your email to set your password.", staff, token);
    }


    // Generate a password reset token
    private String generatePasswordResetToken(String email) {
        // Create a new token and save it in the PasswordResetToken table
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(token);
        passwordResetTokenRepo.save(resetToken);
        return token;
    }

    // Reset password after email verification
    public BaseResponse resetPassword(String email, String token, String password) {
        // Validate the token
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepo.findByEmailAndToken(email, token);
        if (tokenOpt.isEmpty()) {
            return new BaseResponse("001", "Invalid or expired token", null);
        }

        // Find the staff by email
        Staff staff = staffRepo.findByEmail(email);
        if (staff == null) {
            return new BaseResponse("002", "Staff not found", null);
        }

        // Set the new password (encode it before saving to DB)
        staff.setPassword(new BCryptPasswordEncoder().encode(password));
        staffRepo.save(staff);

        // Call resetUserPassword to update the password in Keycloak
        boolean passwordResetInKeycloak = keycloakService.resetUserPassword(staff.getAccountId(), password);
        if (!passwordResetInKeycloak) {
            return new BaseResponse("003", "Failed to reset password in Keycloak", null);
        }

        // Delete the token after use
        passwordResetTokenRepo.delete(tokenOpt.get());

        return new BaseResponse("000", "Password has been successfully set", staff);
    }



    public BaseResponse updateStaff(Long id, UpdateStaffRequest request) {
        // Find existing staff by id
        Optional<Staff> existingStaffOpt = staffRepo.findById(id);
        if (existingStaffOpt.isEmpty()) {
            return new BaseResponse("001", "The staff is not found", null);
        }

        // Validate name
        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            return new BaseResponse("002", "Name cannot be empty", null);
        }

        // Validate email
        if (request.getEmail() == null || !request.getEmail().contains("@") || !request.getEmail().contains(".com")) {
            return new BaseResponse("003", "Invalid email.", null);
        }

        // Check if the email already exists
        if (staffRepo.existsByEmail(request.getEmail()) && !existingStaffOpt.get().getEmail().equals(request.getEmail())) {
            return new BaseResponse("004", "This email already exists", null);
        }

        // Validate phone number
        String phoneNoStr = String.valueOf(request.getPhoneNo());
        if (!phoneNoStr.startsWith("09") || phoneNoStr.length() != 11) {
            return new BaseResponse("005", "Invalid phone number. It should start with '09' and be 11 digits long", null);
        }

        if (staffRepo.existsByPhoneNo(request.getPhoneNo()) && !existingStaffOpt.get().getPhoneNo().equals(request.getPhoneNo())) {
            return new BaseResponse("006", "This phone number already exists", null);
        }

        // Validate role
        Optional<Role> roleOpt = roleRepo.findByName(request.getRole());
        if (roleOpt.isEmpty()) {
            return new BaseResponse("007", "Role not found", null);
        }

        // Update existing staff
        Staff existingStaff = existingStaffOpt.get();

        // Check if the username (email) or phone number has changed
        boolean isEmailChanged = !existingStaff.getEmail().equals(request.getEmail());
        boolean isUsernameChanged = !existingStaff.getUsername().equals(request.getUsername());
        boolean isPasswordChanged = request.getPassword() != null && !request.getPassword().isEmpty();

        // Update staff details
        existingStaff.setFirstName(request.getFirstName());
        existingStaff.setLastName(request.getLastName());
        existingStaff.setEmail(request.getEmail());
        existingStaff.setUsername(request.getUsername());
        existingStaff.setPhoneNo(request.getPhoneNo());
        existingStaff.setPosition(request.getPosition());

        // Update the roles
        existingStaff.getRoles().clear(); // Clear existing roles if necessary
        existingStaff.getRoles().add(roleOpt.get()); // Add new role

        // If email or username has changed, update in Keycloak
        if (isEmailChanged || isUsernameChanged) {
            String keycloakUserId = existingStaff.getAccountId(); // Get the Keycloak ID
            boolean keycloakUpdateSuccess = keycloakService.updateUserDetails(keycloakUserId, request.getEmail(), request.getUsername());

            if (!keycloakUpdateSuccess) {
                return new BaseResponse("008", "Failed to update Keycloak account", null);
            }
        }

        // If password has changed, update it in Keycloak
        if (isPasswordChanged) {
            String keycloakUserId = existingStaff.getAccountId(); // Get the Keycloak ID
            boolean keycloakPasswordUpdateSuccess = keycloakService.updateUserPassword(keycloakUserId, request.getPassword());

            if (!keycloakPasswordUpdateSuccess) {
                return new BaseResponse("009", "Failed to update Keycloak password", null);
            }
            // Update the password in your application database
            existingStaff.setPassword(new BCryptPasswordEncoder().encode(request.getPassword())); // Ensure password is encrypted
        }

        // Save updated staff to the database
        return new BaseResponse("000", "Success", staffRepo.save(existingStaff));
    }


    @Override
    @Transactional
    public void deleteStaff(Long staffId) {
        // First delete any roles associated with the staff member
        staffRoleRepo.deleteByStaffId(staffId); // Deletes roles linked to the staff ID

        // Now delete the staff member
        staffRepo.deleteById(staffId); // Deletes the staff member itself
    }


    @Override
    public Staff getStaffByEmail(String email) {
        return staffRepo.findByEmail(email);
    }

    @Override
    public Staff getStaffByUsername(String username) {
        return staffRepo.findByUsername(username);
    }
}
