package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateStaffRequest;
import com.luv2code.springboot.restaurant.entity.Staff;
import com.luv2code.springboot.restaurant.repo.StaffRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffRepo staffRepo;

    public BaseResponse getAllStaff(){
        return new BaseResponse("000", "success", staffRepo.findAll());
    }

    public BaseResponse createStaff(CreateStaffRequest request) {
        // Validate name
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return new BaseResponse("002", "Name cannot be empty", null);
        }

        // Validate email
        if (request.getEmail() == null || !request.getEmail().contains("@") || !request.getEmail().contains(".com")) {
            return new BaseResponse("003", "Invalid email.", null);
        }


        // Validate phone number
        String phoneNoStr = String.valueOf(request.getPhoneNo());
        if (!phoneNoStr.startsWith("09") || phoneNoStr.length() != 11) {
            return new BaseResponse("004", "Invalid phone number. It should start with '09' and be 11 digits long", null);
        }

        // Check if email already exists
        if (staffRepo.existsByEmail(request.getEmail())) {
            return new BaseResponse("001", "The owner of this email already exists", null);
        }

        // Create new staff
        Staff staff = new Staff();
        staff.setName(request.getName());
        staff.setEmail(request.getEmail());
        staff.setPhoneNo(request.getPhoneNo());
        staff.setPosition(request.getPosition());
        return new BaseResponse("000", "Success", staffRepo.save(staff));
    }

    public BaseResponse updateStaff(Long id, CreateStaffRequest request) {
        // Find existing staff by id
        Optional<Staff> existingStaffOpt = staffRepo.findById(id);
        if (existingStaffOpt.isEmpty()) {
            return new BaseResponse("001", "The Staff is not found", null);
        }

        // Validate name
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return new BaseResponse("002", "Name cannot be empty", null);
        }

        // Validate email
        if (request.getEmail() == null || !request.getEmail().contains("@") || !request.getEmail().contains(".com")) {
            return new BaseResponse("003", "Invalid email.", null);
        }


        // Validate phone number
        String phoneNoStr = String.valueOf(request.getPhoneNo());
        if (!phoneNoStr.startsWith("09") || phoneNoStr.length() != 11) {
            return new BaseResponse("004", "Invalid phone number. It should start with '09' and be 11 digits long", null);
        }

        // Update existing staff
        Staff existingStaff = existingStaffOpt.get();
        existingStaff.setName(request.getName());
        existingStaff.setEmail(request.getEmail());
        existingStaff.setPhoneNo(request.getPhoneNo());
        existingStaff.setPosition(request.getPosition());
        return new BaseResponse("000", "Success", staffRepo.save(existingStaff));
    }



    public void deleteStaff(Long id){
        staffRepo.deleteById(id);
    }
}
