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
    com.example.vaccineManagement.Repository.DoctorRepository doctorRepository;

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



        int currentDoses = (int) user.getDoseList().stream()
                .filter(d -> d.getVaccine().getVaccineName().equalsIgnoreCase(doseRequestDto.getVaccineName()))
                .count();

        if (currentDoses >= vaccine.getDosesRequired()) {
            throw new RuntimeException("User is already fully vaccinated for " + doseRequestDto.getVaccineName() + "!");
        }

        // VIAL TRACKING LOGIC (Sanitize input with Trim and UpperCase to avoid mismatch)
        String sanitizedVialNo = doseRequestDto.getVialNumber().trim().toUpperCase();
        com.example.vaccineManagement.Entity.Vial vialToUse = vialRepository.findByVialNumber(sanitizedVialNo)
                .orElseThrow(() -> new RuntimeException("Error: Vial not found with tracking number: " + sanitizedVialNo));

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

        // Assign administering doctor
        if (doseRequestDto.getDocId() != null) {
            com.example.vaccineManagement.Entity.Doctor doctor = doctorRepository.findById(doseRequestDto.getDocId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));
            dose.setDoctor(doctor);
        }

        dose.setUser(user);
        dose.setVaccine(vaccine);
        doseRepository.save(dose);

        //update status
        if ((currentDoses + 1) == vaccine.getDosesRequired()) {
            user.setVaccinated(true);
            userRepository.save(user);
        }

        // Format Date and Time separately
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String formattedDate = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String formattedTime = now.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

        // Get Doctor Name for Email
        String adminDoctorName = dose.getDoctor() != null ? dose.getDoctor().getName() : "General Staff";

        // Send Email
        emailService.sendVaccinationEmail(
                user.getAuthUser().getEmail(),
                user.getName(),
                dose.getDoseNumber(),
                vaccine.getBatchNumber(),
                vialToUse.getVialNumber(),
                formattedDate,
                formattedTime,
                adminDoctorName
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
    }
}
