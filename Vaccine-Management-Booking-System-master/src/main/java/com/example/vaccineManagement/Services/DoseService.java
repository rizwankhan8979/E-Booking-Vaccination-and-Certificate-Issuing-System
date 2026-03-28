package com.example.vaccineManagement.Services;

import com.example.vaccineManagement.Entity.Dose;
import com.example.vaccineManagement.Entity.User;
import com.example.vaccineManagement.Entity.Vaccine;
import com.example.vaccineManagement.Repository.DoseRepository;
import com.example.vaccineManagement.Repository.UserRepository;
import com.example.vaccineManagement.Repository.VaccineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoseService {

    @Autowired
    DoseRepository doseRepository;

    @Autowired
    VaccineRepository vaccineRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    com.example.vaccineManagement.Repository.VialRepository vialRepository;

    @Autowired
    EmailService emailService;

    public com.example.vaccineManagement.Dtos.ResponseDtos.DoseResponseDto giveDose(com.example.vaccineManagement.Dtos.RequestDtos.DoseRequestDto doseRequestDto) {

        User user = userRepository.findById(doseRequestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Vaccine vaccine = vaccineRepository.findById(doseRequestDto.getVaccineId())
                .orElseThrow(() -> new RuntimeException("Vaccine not found"));

        if (!vaccine.getVaccineName().equalsIgnoreCase(doseRequestDto.getVaccineName())) {
            throw new RuntimeException("Error: Vaccine name mismatch! The connected Batch is for " + vaccine.getVaccineName());
        }

<<<<<<< HEAD
        Dose dose = new Dose();
        dose.setDoseId(doseId);
        dose.setUser(user);
        user.setDose(dose);
        userRepository.save(user);

        return "Dose Given to user successfully";
=======
        // ADVANCED TRACKING BY VACCINE NAME
        // Count ONLY doses where the vaccine name matches!
        int currentDoses = (int) user.getDoseList().stream()
                .filter(d -> d.getVaccine().getVaccineName().equalsIgnoreCase(doseRequestDto.getVaccineName()))
                .count();

        if (currentDoses >= vaccine.getDosesRequired()) {
            throw new RuntimeException("User is already fully vaccinated for " + doseRequestDto.getVaccineName() + "!");
        }

        // VIAL TRACKING LOGIC
        // Find the specific vial by barcode
        com.example.vaccineManagement.Entity.Vial vialToUse = vialRepository.findByVialNumber(doseRequestDto.getVialNumber())
                .orElseThrow(() -> new RuntimeException("Error: Vial not found with tracking number: " + doseRequestDto.getVialNumber()));

        if (vialToUse.getRemainingDoses() <= 0 || vialToUse.getStatus() == com.example.vaccineManagement.Enums.VialStatus.EMPTY) {
            throw new RuntimeException("Error: This vial is already empty!");
        }

        if (vialToUse.getVaccine().getId() != doseRequestDto.getVaccineId() || !vaccine.getBatchNumber().equals(doseRequestDto.getBatchNumber())) {
            throw new RuntimeException("Error: This vial does not belong to the requested Vaccine Batch!");
        }

        // Deduct dose
        vialToUse.setRemainingDoses(vialToUse.getRemainingDoses() - 1);
        if (vialToUse.getRemainingDoses() == 0) {
            vialToUse.setStatus(com.example.vaccineManagement.Enums.VialStatus.EMPTY);
        }
        vialRepository.save(vialToUse);

        // create dose entry
        Dose dose = new Dose();

        // Auto-generate Unique Dose ID
        dose.setDoseId("DOSE-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // Set Dose
        dose.setDoseNumber(currentDoses + 1);

        // Assign specific vial number to this dose record
        dose.setVialSerialNumber(vialToUse.getVialNumber());

        dose.setUser(user);
        dose.setVaccine(vaccine);
        doseRepository.save(dose);

        //update status
        if ((currentDoses + 1) == vaccine.getDosesRequired()) {
            user.setVaccinated(true);
            userRepository.save(user);
        }

        // Construct formatting date
        String formattedDate = java.time.LocalDateTime.now().toString();

        // Send Email
        emailService.sendVaccinationEmail(
                user.getAuthUser().getEmail(), 
                user.getName(), 
                dose.getDoseNumber(), 
                vaccine.getBatchNumber(), 
                vialToUse.getVialNumber(), 
                formattedDate
        );

        // Construct detailed JSON response
        return com.example.vaccineManagement.Dtos.ResponseDtos.DoseResponseDto.builder()
                .message("Dose " + dose.getDoseNumber() + " given successfully!")
                .userId(user.getUserId())
                .userName(user.getName())
                .userEmail(user.getAuthUser().getEmail())
                .doseNumber(dose.getDoseNumber())
                .batchNumber(vaccine.getBatchNumber())
                .vialNumber(vialToUse.getVialNumber())
                .vaccinationDate(new java.util.Date())
                .build();
>>>>>>> b4f768d (Updated backend After Create Apis)
    }
}
