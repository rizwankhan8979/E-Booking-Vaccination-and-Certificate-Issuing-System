package com.example.vaccineManagement.Controllers;

import com.example.vaccineManagement.Dtos.RequestDtos.AssociateDocDto;
import com.example.vaccineManagement.Entity.Doctor;
import com.example.vaccineManagement.Services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/doctor")
@CrossOrigin
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    //ADD DOCTOR ONLY ADMIN ROLE
    @PostMapping("/add")
    public String addDoctor(@RequestBody Doctor doctor){

        try {
            String response = doctorService.addDoctor(doctor);
            return response;
        }catch (Exception e){
            return e.getMessage();
        }

    }

    //LINK DOCTOR INTO CENTER
    @PostMapping("/associateWithCenter")
    public ResponseEntity<String> associateDoctor(@RequestBody AssociateDocDto associateDocDto){

        try{
            String result = doctorService.associateDoctor(associateDocDto);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }

<<<<<<< HEAD
    // doctor List 
=======
    // GET ALL DOCTOR LIST
>>>>>>> b4f768d (Updated backend After Create Apis)
    @GetMapping("/getAll")
    public ResponseEntity<List<Doctor>> getAllDoctors(){

        try {
            List<Doctor> doctorList = doctorService.getAllDoctors();
            return new ResponseEntity<>(doctorList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
