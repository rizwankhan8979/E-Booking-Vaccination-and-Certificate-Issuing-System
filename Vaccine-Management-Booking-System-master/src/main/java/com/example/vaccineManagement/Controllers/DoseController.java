package com.example.vaccineManagement.Controllers;


import com.example.vaccineManagement.Services.DoseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dose")
@CrossOrigin
public class DoseController {

    @Autowired
    DoseService doseService;

    @PostMapping("/giveDose")
    public org.springframework.http.ResponseEntity<?> giveDose(@RequestBody com.example.vaccineManagement.Dtos.RequestDtos.DoseRequestDto doseRequestDto) {
        try {
            com.example.vaccineManagement.Dtos.ResponseDtos.DoseResponseDto response = doseService.giveDose(doseRequestDto);
            return new org.springframework.http.ResponseEntity<>(response, org.springframework.http.HttpStatus.OK);
        } catch (Exception e) {
            return new org.springframework.http.ResponseEntity<>(e.getMessage(), org.springframework.http.HttpStatus.BAD_REQUEST);
        }
    }
}
