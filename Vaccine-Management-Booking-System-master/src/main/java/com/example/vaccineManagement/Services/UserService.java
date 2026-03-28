package com.example.vaccineManagement.Services;

import com.example.vaccineManagement.Entity.AuthUser;
import com.example.vaccineManagement.Entity.Dose;
import com.example.vaccineManagement.Entity.User;
import com.example.vaccineManagement.Repository.AuthUserRepository;
import com.example.vaccineManagement.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthUserRepository authUserRepository;

    public User addUser(User user, String loggedInEmail) {
        
        AuthUser authUser = authUserRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        user.setAuthUser(authUser);
        return userRepository.save(user);
    }

    // GET MY PROFILE
    public User getMyProfile(String email) {

        AuthUser authUser = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        return userRepository.findByAuthUser(authUser)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
    }

    // GET VACCINATION DATE
    public Date getVaccDate(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Dose dose = user.getDose();
        return dose != null ? dose.getVaccinationDate() : null;
    }

    // GET ALL USERS
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
