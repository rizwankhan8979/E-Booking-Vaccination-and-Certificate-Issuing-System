package com.example.vaccineManagement.Services;

import com.example.vaccineManagement.Dtos.RequestDtos.AppointmentReqDto;
import com.example.vaccineManagement.Entity.*;
import com.example.vaccineManagement.Exceptions.DoctorNotFound;
import com.example.vaccineManagement.Exceptions.UserNotFound;
import com.example.vaccineManagement.Exceptions.VaccineNotFound;
import com.example.vaccineManagement.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
        private JavaMailSender emailSender;

        // BOOK APPOINTMENT (SECURE VERSION)
        @Transactional
        public String bookAppointment(AppointmentReqDto dto)
                        throws DoctorNotFound, VaccineNotFound, UserNotFound {

                //get email through the Token
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                // 2. Search AuthUser
                AuthUser authUser = authUserRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Login user not found"));

                //  Search User Profile
                User user = userRepository.findByAuthUser(authUser)
                        .orElseThrow(() -> new UserNotFound("Please complete your profile before booking"));


                //Fetch Doctor and Vaccine
                Doctor doctor = doctorRepository.findById(dto.getDocId())
                        .orElseThrow(() -> new DoctorNotFound("Doctor not found"));

                Vaccine vaccine = vaccineRepository.findById(dto.getVaccineId())
                        .orElseThrow(() -> new VaccineNotFound("Vaccine not found"));


                //  Check: Vaccine and Doctor
                if (vaccine.getDoctor() == null || !vaccine.getDoctor().getDocId().equals(doctor.getDocId())) {
                        throw new RuntimeException("This doctor does not provide the selected vaccine");
                }

                // Appointment
                Appointment appointment = new Appointment();
                appointment.setAppointmentDate(dto.getAppointmentDate());
                appointment.setAppointmentTime(dto.getAppointmentTime());
                appointment.setDoctor(doctor);
                appointment.setVaccine(vaccine);
                appointment.setUser(user);
                appointmentRepository.save(appointment);

                // 6. Confirmation Email
                try {
                        SimpleMailMessage mail = new SimpleMailMessage();
                        mail.setTo(email); // Directly using token email
                        mail.setSubject("Appointment Confirmed");
                        mail.setText("Hi " + user.getName() + ",\nYour appointment with Dr. " + doctor.getName() + " is confirmed for " + dto.getAppointmentDate());
                        emailSender.send(mail);
                } catch (Exception e) {
                        System.out.println("Email failed but appointment is saved: " + e.getMessage());
                }

                return "Appointment booked successfully!";
        }
}
