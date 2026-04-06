package com.example.vaccineManagement.Controllers;

import com.example.vaccineManagement.Exceptions.VaccinationAddressNotFound;
import com.example.vaccineManagement.Entity.VaccinationCenter;
import com.example.vaccineManagement.Services.VaccinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/vaccinationCenter")
@CrossOrigin
public class Vaccination_Centers_Controller {

    @Autowired
    public VaccinationService vaccinationService;

    //ADD VACCINE CENTER
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addCenter(@RequestBody VaccinationCenter vaccinationCenter) {

        try {
            String result = vaccinationService.addVaccinationCenter(vaccinationCenter);
            return new ResponseEntity<>(Map.of("message", result), HttpStatus.OK);
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : e.toString();
            return new ResponseEntity<>(Map.of("message", msg), HttpStatus.BAD_REQUEST);
        }

    }

    //GET ALL VACCINE CENTER
    @GetMapping("/getAll")
    public ResponseEntity<List<VaccinationCenter>> getAllCenters() {
        try {
            List<VaccinationCenter> centerList = vaccinationService.getAllVaccinationCenters();
            return new ResponseEntity<>(centerList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
