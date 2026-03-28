package com.example.vaccineManagement.Services;

import com.example.vaccineManagement.Entity.Doctor;
import com.example.vaccineManagement.Entity.Vaccine;
import com.example.vaccineManagement.Exceptions.DoctorNotFound;
import com.example.vaccineManagement.Exceptions.VaccineNotFound;
import com.example.vaccineManagement.Repository.DoctorRepository;
import com.example.vaccineManagement.Repository.VaccineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VaccineService {

    @Autowired
    private VaccineRepository vaccineRepository;

    @Autowired
    private DoctorRepository doctorRepository;
<<<<<<< HEAD
    
=======

>>>>>>> b4f768d (Updated backend After Create Apis)
    public String addVaccine(Vaccine vaccine) {
        if (vaccine.getBatchNumber() == null || vaccine.getBatchNumber().trim().isEmpty()) {
            throw new RuntimeException("Error: Batch Number must be provided manually!");
        }

        //Duplicate Check
        if (vaccineRepository.existsByBatchNumber(vaccine.getBatchNumber())) {
            throw new RuntimeException("Error: Batch Number '" + vaccine.getBatchNumber() + "' already exists in the system!");
        }

        //Capacity and Vial Limit Check
        if (vaccine.getTotalBatchCapacity() <= 0 || vaccine.getDosesPerVial() <= 0) {
            throw new RuntimeException("Error: Total Capacity and Doses Per Vial must be greater than zero!");
        }

        if (vaccine.getTotalBatchCapacity() % vaccine.getDosesPerVial() != 0) {
            throw new RuntimeException("Error: Total Capacity (" + vaccine.getTotalBatchCapacity() +
                    ") is invalid based on vials. It must be divisible by " + vaccine.getDosesPerVial() + "!");
        }

        //Date Validation
        if (vaccine.getEntryDate() != null && vaccine.getExpiryDate() != null) {
            if (vaccine.getExpiryDate().before(vaccine.getEntryDate())) {
                throw new RuntimeException("Error: Expiry Date must be after the Entry (Manufacturing) Date!");
            }
        } else {
            throw new RuntimeException("Error: Both Entry Date and Expiry Date must be provided manually!");
        }

        vaccineRepository.save(vaccine);

        return "Vaccine added successfully! Batch: " + vaccine.getBatchNumber() +
                ", Total Certificates allowed: " + vaccine.getTotalBatchCapacity();
    }

    public String associateVaccineWithDoctor(int vaccineId, int doctorId)
            throws VaccineNotFound, DoctorNotFound {

        Optional<Vaccine> vaccineOpt = vaccineRepository.findById(vaccineId);
        if (!vaccineOpt.isPresent()) {
            throw new VaccineNotFound("Vaccine not found with id: " + vaccineId);
        }

        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (!doctorOpt.isPresent()) {
            throw new DoctorNotFound("Doctor not found with id: " + doctorId);
        }

        Vaccine vaccine = vaccineOpt.get();
        Doctor doctor = doctorOpt.get();

        vaccine.setDoctor(doctor);                 
        doctor.getVaccineList().add(vaccine);        
        vaccineRepository.save(vaccine);             
                
        return "Vaccine '" + vaccine.getVaccineName() + "' linked with Doctor: " + doctor.getName();
    }

    // Get all vaccines
    public List<Vaccine> getAllVaccines() {
        return vaccineRepository.findAll();
    }

    //Get vaccine by ID
    public Vaccine getVaccineById(int id) throws VaccineNotFound {
        return vaccineRepository.findById(id)
                .orElseThrow(()
                        -> new VaccineNotFound("Vaccine not found with id: " + id));
    }

    public List<Vaccine> getVaccinesByDoctorId(int doctorId) {
        return vaccineRepository.findByDoctor_DocId(doctorId);
    }

    // Update Vaccine
    public String updateVaccine(int id, Vaccine updatedVaccine) throws VaccineNotFound {
        Vaccine existingVaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new VaccineNotFound("Vaccine not found with id: " + id));

        // 1. Batch Number Check
        if (updatedVaccine.getBatchNumber() != null && !updatedVaccine.getBatchNumber().trim().isEmpty()) {
            if (!existingVaccine.getBatchNumber().equals(updatedVaccine.getBatchNumber()) &&
                vaccineRepository.existsByBatchNumber(updatedVaccine.getBatchNumber())) {
                throw new RuntimeException("Error: Batch Number '" + updatedVaccine.getBatchNumber() + "' already exists in the system!");
            }
            existingVaccine.setBatchNumber(updatedVaccine.getBatchNumber());
        }

        // 2. Update basic fields
        if (updatedVaccine.getVaccineName() != null) existingVaccine.setVaccineName(updatedVaccine.getVaccineName());
        if (updatedVaccine.getManufacturer() != null) existingVaccine.setManufacturer(updatedVaccine.getManufacturer());
        if (updatedVaccine.getDosesRequired() > 0) existingVaccine.setDosesRequired(updatedVaccine.getDosesRequired());
        if (updatedVaccine.getAgeRange() != null) existingVaccine.setAgeRange(updatedVaccine.getAgeRange());
        if (updatedVaccine.getStatus() != null) existingVaccine.setStatus(updatedVaccine.getStatus());

        // 3. Update capacities
        if (updatedVaccine.getTotalBatchCapacity() > 0) existingVaccine.setTotalBatchCapacity(updatedVaccine.getTotalBatchCapacity());
        if (updatedVaccine.getDosesPerVial() > 0) existingVaccine.setDosesPerVial(updatedVaccine.getDosesPerVial());

        // Capacity and Vial Limit Check
        if (existingVaccine.getTotalBatchCapacity() <= 0 || existingVaccine.getDosesPerVial() <= 0) {
            throw new RuntimeException("Error: Total Capacity and Doses Per Vial must be greater than zero!");
        }

        // Batch vs Vial Logic Check
        if (existingVaccine.getTotalBatchCapacity() % existingVaccine.getDosesPerVial() != 0) {
            throw new RuntimeException("Error: Total Capacity (" + existingVaccine.getTotalBatchCapacity() +
                    ") is invalid based on vials. It must be divisible by " + existingVaccine.getDosesPerVial() + "!");
        }

        // 4. Update Dates
        if (updatedVaccine.getEntryDate() != null) existingVaccine.setEntryDate(updatedVaccine.getEntryDate());
        if (updatedVaccine.getExpiryDate() != null) existingVaccine.setExpiryDate(updatedVaccine.getExpiryDate());

        // Date Validation
        if (existingVaccine.getEntryDate() != null && existingVaccine.getExpiryDate() != null) {
            if (existingVaccine.getExpiryDate().before(existingVaccine.getEntryDate())) {
                throw new RuntimeException("Error: Expiry Date must be after the Entry (Manufacturing) Date!");
            }
        }

        vaccineRepository.save(existingVaccine);
        return "Vaccine updated successfully! Batch: " + existingVaccine.getBatchNumber();
    }
}
