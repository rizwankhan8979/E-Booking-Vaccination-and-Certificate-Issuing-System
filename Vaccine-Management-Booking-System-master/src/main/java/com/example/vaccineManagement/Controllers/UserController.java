package com.example.vaccineManagement.Controllers;

import com.example.vaccineManagement.Entity.User;
import com.example.vaccineManagement.Services.UserService;
import com.example.vaccineManagement.Services.AuthService;
import com.example.vaccineManagement.Entity.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    // AFTER REGISTRATION COMPLETE PROFILE
    @PostMapping("/add")
    public org.springframework.http.ResponseEntity<?> addUser(
            @RequestBody User user,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        try {
            User savedUser = userService.addUser(user, principal.getUsername());
            return org.springframework.http.ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    // REQUEST PROFILE UPDATE (Sends OTP)
    @PutMapping("/update-request")
    public org.springframework.http.ResponseEntity<?> requestProfileUpdate(
            @RequestBody User updatedUser,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        try {
            String result = userService.requestProfileUpdate(principal.getUsername(), updatedUser);
            return org.springframework.http.ResponseEntity.ok(java.util.Map.of("message", result));
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    // VERIFY PROFILE UPDATE OTP
    @PostMapping("/verify-update")
    public org.springframework.http.ResponseEntity<?> verifyProfileUpdate(
            @RequestBody java.util.Map<String, Integer> request,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        try {
            int otp = request.get("otp");
            String result = userService.verifyProfileUpdate(principal.getUsername(), otp);
            return org.springframework.http.ResponseEntity.ok(java.util.Map.of("message", result));
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    // GET MY PROFILE
    @GetMapping("/profile")
    public org.springframework.http.ResponseEntity<?> getMyProfile(
            @AuthenticationPrincipal UserDetails principal) {
        User user = userService.getMyProfile(principal.getUsername());
        AuthUser authUser = authService.findByEmail(principal.getUsername());

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("userId", user.getUserId());
        response.put("name", user.getName());
        response.put("age", user.getAge());
        response.put("gender", user.getGender());
        response.put("mobileNo", user.getMobileNo());
        response.put("isVaccinated", user.isVaccinated());
        response.put("role", authUser.getRole().name());

        return org.springframework.http.ResponseEntity.ok(response);
    }

    // GET VACCINATION DATE
    @GetMapping("/getVaccinationDate")
    public List<Date> getVaccinationDate(@AuthenticationPrincipal UserDetails principal) {
        // User chah kar bhi kisi aur ki details nahi dekh sakta
        // kyunki email token se aa rahi hai
        String email = principal.getUsername();
        return userService.getVaccDate(email);

    }


    // GET ALL USERS
    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    public java.util.List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // GET ALL USERS WITH FULL DOSE DETAILS (ADMIN)
    @GetMapping("/getAllWithDoses")
    @PreAuthorize("hasRole('ADMIN')")
    public org.springframework.http.ResponseEntity<?> getAllUsersWithDoses() {
        try {
            return org.springframework.http.ResponseEntity.ok(userService.getAllUsersWithDoses());
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    // GET MY FULL DOSE HISTORY (LOGGED-IN USER)
    @GetMapping("/myDoses")
    public org.springframework.http.ResponseEntity<?> getMyDoses(
            @AuthenticationPrincipal UserDetails principal) {
        try {
            return org.springframework.http.ResponseEntity.ok(userService.getMyDoses(principal.getUsername()));
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }
}
