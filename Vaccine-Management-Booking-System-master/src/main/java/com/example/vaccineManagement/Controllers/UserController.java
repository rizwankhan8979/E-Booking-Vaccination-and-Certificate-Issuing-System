package com.example.vaccineManagement.Controllers;

import com.example.vaccineManagement.Entity.User;
import com.example.vaccineManagement.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    UserService userService;

    // Add PROFILE
    @PostMapping("/add")
    public User addUser(
            @RequestBody User user,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        return userService.addUser(user, principal.getUsername());
    }

    // GET MY PROFILE
    @GetMapping("/profile")
    public User getMyProfile(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        return userService.getMyProfile(principal.getUsername());
    }

    // GET VACCINATION DATE
    @GetMapping("/getVaccinationDate")
    public Date getVaccinationDate(@RequestParam Integer userId) {
        return userService.getVaccDate(userId);
    }

    // GET ALL USERS
    @GetMapping("/getAll")
    public java.util.List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
