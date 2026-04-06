package com.example.vaccineManagement.Controllers;

import com.example.vaccineManagement.Dtos.RequestDtos.AppointmentReqDto;
import com.example.vaccineManagement.Services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointment")
@CrossOrigin
public class AppointmentController {

    @Autowired
    AppointmentService appointmentService;

    //BOOK APPOINTMENT AFTER REGISTER USER
    @PostMapping("/book")
    public String bookAppointment(@RequestBody AppointmentReqDto appointmentReqDto) {

        try {
            String result = appointmentService.bookAppointment(appointmentReqDto);
            return result;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // GET MY BOOKINGS
    @GetMapping("/my-bookings")
    public org.springframework.http.ResponseEntity<?> getMyBookings() {
        try {
            java.util.List<java.util.Map<String, Object>> bookings = appointmentService.getMyBookings();
            return org.springframework.http.ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

}
