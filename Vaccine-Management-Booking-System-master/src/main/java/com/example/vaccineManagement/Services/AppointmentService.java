package com.example.vaccineManagement.Services;

import com.example.vaccineManagement.Dtos.RequestDtos.AppointmentReqDto;
import com.example.vaccineManagement.Entity.*;
import com.example.vaccineManagement.Exceptions.DoctorNotFound;
import com.example.vaccineManagement.Exceptions.UserNotFound;
import com.example.vaccineManagement.Exceptions.VaccineNotFound;
import com.example.vaccineManagement.Enums.Role;
import com.example.vaccineManagement.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Service
public class AppointmentService {

        @Autowired
        private AppointmentRepository appointmentRepository;
        @Autowired
        private DoctorRepository doctorRepository;
        @Autowired
        private VaccineRepository vaccineRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private AuthUserRepository authUserRepository;
        @Autowired
        private EmailService emailService;

        // BOOK APPOINTMENT (SECURE VERSION)
        @Transactional
        public String bookAppointment(AppointmentReqDto dto)
                        throws DoctorNotFound, VaccineNotFound, UserNotFound {

                // 1. Get requestor info from token
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                AuthUser authUser = authUserRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Login user not found"));

                User user = userRepository.findByAuthUser(authUser)
                        .orElseThrow(() -> new UserNotFound("Please complete your profile before booking"));

                // 2. Fetch Doctor and Vaccine
                Doctor doctor = doctorRepository.findById(dto.getDocId())
                        .orElseThrow(() -> new DoctorNotFound("Doctor not found"));

                Vaccine vaccine = vaccineRepository.findById(dto.getVaccineId())
                        .orElseThrow(() -> new VaccineNotFound("Vaccine not found"));

                // 3. Validation: Doctor must provide this vaccine
                if (vaccine.getDoctor() == null || !vaccine.getDoctor().getDocId().equals(doctor.getDocId())) {
                        throw new RuntimeException("This doctor does not provide the selected vaccine");
                }

                // 4. Determine Target User (Role-Based)
                User targetUser = user; // Default to self
                if (authUser.getRole() == Role.ADMIN && dto.getUserId() != null) {
                    targetUser = userRepository.findById(dto.getUserId())
                            .orElseThrow(() -> new UserNotFound("Target user not found"));
                }

                // 5. Save Appointment
                Appointment appointment = new Appointment();
                appointment.setAppointmentDate(dto.getAppointmentDate());
                appointment.setAppointmentTime(dto.getAppointmentTime());
                appointment.setDoctor(doctor);
                appointment.setVaccine(vaccine);
                appointment.setUser(targetUser);
                appointmentRepository.save(appointment);

                // 6. Confirmation Email (Sent to Patient)
                try {
                        String patientEmail = targetUser.getAuthUser().getEmail();
                        // Format specifically for email
                        String dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(dto.getAppointmentDate());
                        String timeStr = dto.getAppointmentTime().toString();
                        
                        emailService.sendAppointmentEmail(
                            patientEmail, 
                            targetUser.getName(), 
                            doctor.getName(), 
                            vaccine.getVaccineName(), 
                            dateStr, 
                            timeStr,
                            doctor.getVaccinationCenter() != null ? doctor.getVaccinationCenter().getCentreName() : "To be confirmed",
                            doctor.getVaccinationCenter() != null ? doctor.getVaccinationCenter().getAddress() : "Check with center"
                        );
                } catch (Exception e) {
                        System.out.println("Email failed but appointment is saved: " + e.getMessage());
                }

                return "Appointment booked successfully!";
        }

        // GET MY BOOKINGS
        public List<Map<String, Object>> getMyBookings() throws UserNotFound {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                AuthUser authUser = authUserRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Login user not found"));

                User user = userRepository.findByAuthUser(authUser)
                        .orElseThrow(() -> new UserNotFound("Please complete your profile first"));

                List<Appointment> appointments = user.getAppointmentList();
                List<Map<String, Object>> resultList = new ArrayList<>();

                for (Appointment app : appointments) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", app.getId());
                        map.put("userName", user.getName());
                        map.put("doctorName", app.getDoctor() != null ? app.getDoctor().getName() : "Unknown Doctor");
                        map.put("vaccineName", app.getVaccine() != null ? app.getVaccine().getVaccineName() : "Unknown Vaccine");
                        
                        // Add Center Details
                        if (app.getDoctor() != null && app.getDoctor().getVaccinationCenter() != null) {
                            map.put("centerName", app.getDoctor().getVaccinationCenter().getCentreName());
                            map.put("centerAddress", app.getDoctor().getVaccinationCenter().getAddress());
                        } else {
                            map.put("centerName", "N/A");
                            map.put("centerAddress", "N/A");
                        }

                        // Format date to string to prevent serialization issues
                        map.put("date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(app.getAppointmentDate()));
                        map.put("time", app.getAppointmentTime().toString());
                        map.put("status", "Confirmed");
                        resultList.add(map);
                }
                // Reverse list to show latest first
                java.util.Collections.reverse(resultList);
                return resultList;
        }
}
