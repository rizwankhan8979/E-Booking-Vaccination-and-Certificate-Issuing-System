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
<<<<<<< HEAD
        
        AuthUser authUser = authUserRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

=======
        AuthUser authUser = authUserRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));
>>>>>>> b4f768d (Updated backend After Create Apis)
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

    public List<Date> getVaccDate(String email) {
        AuthUser authUser = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Auth user not found with email: " + email));
        User user = userRepository.findByAuthUser(authUser)
                .orElseThrow(() -> new RuntimeException("User profile not found for this user"));
        List<Dose> doseList = user.getDoseList();

        List<Date> dates = new ArrayList<>();
        for(Dose dose : doseList) {
            dates.add(dose.getVaccinationDate());
        }

        return dates;
    }
    // GET ALL USERS
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
