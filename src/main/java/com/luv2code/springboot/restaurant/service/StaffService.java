package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.CreateStaffRequest;
import com.luv2code.springboot.restaurant.entity.Staff;
import com.luv2code.springboot.restaurant.repo.StaffRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class StaffService {

    @Autowired
    private StaffRepo staffRepo;

    public BaseResponse getAllStaff(){
        return new BaseResponse("000", "success", staffRepo.findAll());
    }

    public BaseResponse createStaff(CreateStaffRequest request){
        if(staffRepo.existsByEmail(request.getEmail())){
            return new BaseResponse("001", "The owner of this eamil is already existed", null);
        }
//        if(staffRepo.existsbyPhoneNo() == request.getPhoneNo()){
//            return new BaseResponse("002", "The owner of this number is already existed", null);
//        }

        Staff staff = new Staff();
        staff.setName(request.getName());
        staff.setEmail(request.getEmail());
        staff.setPhoneNo(request.getPhoneNo());
        staff.setPosition(request.getPosition());
        return new BaseResponse("000", "success", staffRepo.save(staff));
    }


    public BaseResponse updateStaff(Long id, CreateStaffRequest request){
        Optional<Staff> existingStaffOpt = staffRepo.findById(id);
        if(existingStaffOpt.isEmpty()){
            return new BaseResponse("001", "The Staff is not found", null);
        }
        Staff existingStaff = existingStaffOpt.get();
        if(staffRepo.existsByEmail(request.getEmail())){
            return new BaseResponse("001", "The owner of this eamil is already existed", null);
        }
        existingStaff.setName(request.getName());
        existingStaff.setEmail(request.getEmail());
        existingStaff.setPhoneNo(request.getPhoneNo());
        existingStaff.setPosition(request.getPosition());
        return new BaseResponse("000", "success", staffRepo.save(existingStaff));
    }

    public void deleteStaff(Long id){
        staffRepo.deleteById(id);
    }
}
