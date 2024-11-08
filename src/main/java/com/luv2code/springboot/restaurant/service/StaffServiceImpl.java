package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.*;
import com.luv2code.springboot.restaurant.entity.Role;
import com.luv2code.springboot.restaurant.entity.Staff;
import com.luv2code.springboot.restaurant.repo.RoleRepo;
import com.luv2code.springboot.restaurant.repo.StaffRepo;
import com.luv2code.springboot.restaurant.repo.StaffRoleRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
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

        // Check if email already exists
        if (staffRepo.existsByEmail(request.getEmail())) {
            return new BaseResponse("003", "This email already exists", null);
        }

        // Validate phone number
        String phoneNoStr = String.valueOf(request.getPhoneNo());
        if (!phoneNoStr.startsWith("09") || phoneNoStr.length() != 11) {
            return new BaseResponse("004", "Invalid phone number. It should start with '09' and be 11 digits long", null);
        }

        // Check if phone number already exists
        if (staffRepo.existsByPhoneNo(request.getPhoneNo())) {
            return new BaseResponse("005", "This phone number already exists", null);
        }

        // Validate role
        Optional<Role> roleOpt = roleRepo.findByName(request.getRole());
        if (roleOpt.isEmpty()) {
            return new BaseResponse("008", "Role not found", null);
        }

        // Create the user in Keycloak
        String password = RandomStringUtils.randomAlphanumeric(12);
        String keycloakUserId = keycloakService.createUser(
                request.getEmail(), request.getUsername(), request.getFirstName(), request.getLastName(), password
        );

        // Check Keycloak user creation success
        if (keycloakUserId == null) {
            return new BaseResponse("005", "Failed to create user in Keycloak", null);
        }

        // Assign role to the user in Keycloak
        boolean roleAssigned = keycloakService.assignRoleToUser(keycloakUserId, request.getRole());
        if (!roleAssigned) {
            return new BaseResponse("010", "Failed to assign role in Keycloak", null);
        }

        // Create and save the staff entity
        Staff staff = new Staff();
        staff.setFirstName(request.getFirstName());
        staff.setLastName(request.getLastName());
        staff.setEmail(request.getEmail());
        staff.setUsername(request.getUsername());
        staff.setPhoneNo(request.getPhoneNo());
        staff.setPosition(request.getPosition());
        staff.setPassword(password);
        staff.getRoles().add(roleOpt.get());
        staff.setAccountId(keycloakUserId);

        // Save the staff to the database
        staff = staffRepo.save(staff);

        // Send email with the password
        EmailDto emailDto = new EmailDto(staff.getEmail(), "Welcome to Our Service", "Your temporary password is: " + password);
        emailService.sendPasswordCreateEmail(emailDto, password);

        // Return success response
        return new BaseResponse("000", "Staff created successfully. Please check your email for your password.", staff);
    }

    public BaseResponse updatePassword(EmailDto emailDto, String password) {
        // Find the staff by email
        Staff staff = staffRepo.findByEmail(emailDto.getRecipientEmail());
        if (staff == null) {
            return new BaseResponse("002", "Staff not found", null);
        }

        // Encode the password and save it in the database
        staff.setPassword(new BCryptPasswordEncoder().encode(password));
        staffRepo.save(staff);

        // Update the password in Keycloak
        boolean passwordResetInKeycloak = keycloakService.resetUserPassword(staff.getAccountId(), password);
        if (!passwordResetInKeycloak) {
            return new BaseResponse("003", "Failed to reset password in Keycloak", null);
        }

        return new BaseResponse("000", "Password has been successfully updated", staff);
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

        // Update staff details
        existingStaff.setFirstName(request.getFirstName());
        existingStaff.setLastName(request.getLastName());
        existingStaff.setEmail(request.getEmail());
        existingStaff.setUsername(request.getUsername());
        existingStaff.setPhoneNo(request.getPhoneNo());
        existingStaff.setPosition(request.getPosition());

        // Update the roles
        existingStaff.getRoles().clear();
        existingStaff.getRoles().add(roleOpt.get());

        // Save updated staff to the database
        return new BaseResponse("000", "Success", staffRepo.save(existingStaff));
    }


    @Override
    @Transactional
    public void deleteStaff(Long staffId) {
        // First delete any roles associated with the staff member
        staffRoleRepo.deleteByStaffId(staffId);

        // Now delete the staff member
        staffRepo.deleteById(staffId);
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
