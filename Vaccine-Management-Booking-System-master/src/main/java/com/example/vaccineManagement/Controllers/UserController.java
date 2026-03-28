package com.example.vaccineManagement.Controllers;

import com.example.vaccineManagement.Entity.User;
import com.example.vaccineManagement.Services.UserService;
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

<<<<<<< HEAD
    // Add PROFILE
=======
    // AFTER REGISTRATION COMPLETE PROFILE
>>>>>>> b4f768d (Updated backend After Create Apis)
    @PostMapping("/add")
    public User addUser(
            @RequestBody User user,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        return userService.addUser(user, principal.getUsername());
    }

    // GET MY PROFILE
    @GetMapping("/profile")
    public User getMyProfile(
            @AuthenticationPrincipal UserDetails principal) {
        return userService.getMyProfile(principal.getUsername());
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
}
