package com.example.vaccineManagement.Controllers;

import com.example.vaccineManagement.Entity.Vaccine;
import com.example.vaccineManagement.Services.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/vaccine")
@CrossOrigin
public class VaccineController {

    @Autowired
    private VaccineService vaccineService;

    //ADD VACCINE
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addVaccine(@RequestBody Vaccine vaccine) {
        try {
            String result = vaccineService.addVaccine(vaccine);
            return new ResponseEntity<>(Map.of("message", result), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    // Associate existing vaccine with doctor
    @PostMapping("/associate/{vaccineId}/doctor/{doctorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> associateVaccine(@PathVariable int vaccineId,
            @PathVariable int doctorId) {
        try {
            String result = vaccineService.associateVaccineWithDoctor(vaccineId, doctorId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    //Get all vaccines
    @GetMapping("/getAll")
    public ResponseEntity<List<Vaccine>> getAllVaccines() {
        List<Vaccine> list = vaccineService.getAllVaccines();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    //Get vaccine by ID
    @GetMapping("/get/{id}")
    public ResponseEntity<Vaccine> getVaccineById(@PathVariable int id) {
        try {
            Vaccine vaccine = vaccineService.getVaccineById(id);
            return new ResponseEntity<>(vaccine, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //Get vaccines of a doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Vaccine>> getVaccinesByDoctor(@PathVariable int doctorId) {
        List<Vaccine> list = vaccineService.getVaccinesByDoctorId(doctorId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // Update Vaccine
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateVaccine(@PathVariable int id, @RequestBody Vaccine vaccine) {
        try {
            String result = vaccineService.updateVaccine(id, vaccine);
            return new ResponseEntity<>(Map.of("message", result), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
