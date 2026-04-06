package com.example.vaccineManagement.Services;

import com.example.vaccineManagement.Entity.AuthUser;
import com.example.vaccineManagement.Entity.Dose;
import com.example.vaccineManagement.Entity.User;
import com.example.vaccineManagement.Repository.AuthUserRepository;
import com.example.vaccineManagement.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private EmailService emailService;

    private final Map<String, Integer> profileOtpStore = new ConcurrentHashMap<>();
    private final Map<String, User> pendingProfileUpdates = new ConcurrentHashMap<>();

    public User addUser(User user, String loggedInEmail) {
        AuthUser authUser = authUserRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));
        
        Optional<User> existingUserOpt = userRepository.findByAuthUser(authUser);
        if (existingUserOpt.isPresent()) {
            throw new RuntimeException("Profile already Registered!");
        } 

        // Validation for Mobile Number
        if (user.getMobileNo() == null || !user.getMobileNo().matches("^\\d{10}$")) {
            throw new RuntimeException("Mobile number must be exactly 10 digits and contain only numbers.");
        }

        user.setAuthUser(authUser);
        return userRepository.save(user);
    }

    // 1. Request Profile Update (Sent OTP)
    public String requestProfileUpdate(String email, User updatedUser) {
        AuthUser authUser = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));
        
        userRepository.findByAuthUser(authUser)
                .orElseThrow(() -> new RuntimeException("You don't have a profile yet! Please complete it first."));

        if (updatedUser.getMobileNo() == null || !updatedUser.getMobileNo().matches("^\\d{10}$")) {
            throw new RuntimeException("Mobile number must be exactly 10 digits and contain only numbers.");
        }

        int otp = new Random().nextInt(900000) + 100000;
        profileOtpStore.put(email, otp);
        pendingProfileUpdates.put(email, updatedUser);

        emailService.sendOtpEmail(email, otp);
        return "OTP sent to your email to verify profile update.";
    }

    // 2. Verify OTP and Save Update
    public String verifyProfileUpdate(String email, int otp) {
        Integer storedOtp = profileOtpStore.get(email);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        User pendingUpdate = pendingProfileUpdates.get(email);
        if (pendingUpdate == null) {
            throw new RuntimeException("No pending profile update found");
        }

        AuthUser authUser = authUserRepository.findByEmail(email).get();
        User existingUser = userRepository.findByAuthUser(authUser).get();

        existingUser.setName(pendingUpdate.getName());
        existingUser.setAge(pendingUpdate.getAge());
        existingUser.setGender(pendingUpdate.getGender());
        existingUser.setMobileNo(pendingUpdate.getMobileNo());

        userRepository.save(existingUser);

        profileOtpStore.remove(email);
        pendingProfileUpdates.remove(email);

        return "Profile updated successfully!";
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

    // GET ALL USERS WITH THEIR DOSE DETAILS (ADMIN)
    public List<java.util.Map<String, Object>> getAllUsersWithDoses() {
        List<User> users = userRepository.findAll();
        List<java.util.Map<String, Object>> result = new ArrayList<>();
        for (User user : users) {
            java.util.Map<String, Object> userMap = new java.util.LinkedHashMap<>();
            userMap.put("userId", user.getUserId());
            userMap.put("name", user.getName());
            userMap.put("age", user.getAge());
            userMap.put("gender", user.getGender());
            userMap.put("mobileNo", user.getMobileNo());
            userMap.put("isVaccinated", user.isVaccinated());
            // Include email from authUser
            if (user.getAuthUser() != null) {
                userMap.put("email", user.getAuthUser().getEmail());
            }
            // Build dose list
            List<java.util.Map<String, Object>> doses = new ArrayList<>();
            for (com.example.vaccineManagement.Entity.Dose dose : user.getDoseList()) {
                java.util.Map<String, Object> doseMap = new java.util.LinkedHashMap<>();
                doseMap.put("doseId", dose.getDoseId());
                doseMap.put("doseNumber", dose.getDoseNumber());
                doseMap.put("vaccineName", dose.getVaccine() != null ? dose.getVaccine().getVaccineName() : "N/A");
                doseMap.put("batchNumber", dose.getVaccine() != null ? dose.getVaccine().getBatchNumber() : "N/A");
                doseMap.put("dosesRequired", dose.getVaccine() != null ? dose.getVaccine().getDosesRequired() : 0);
                doseMap.put("vialSerialNumber", dose.getVialSerialNumber());
                doseMap.put("vaccinationDate", dose.getVaccinationDate());
                doseMap.put("doctorName", dose.getDoctor() != null ? dose.getDoctor().getName() : "General Staff");
                doses.add(doseMap);
            }
            userMap.put("doses", doses);
            result.add(userMap);
        }
        return result;
    }

    // GET MY FULL DOSE HISTORY (USER)
    public List<java.util.Map<String, Object>> getMyDoses(String email) {
        AuthUser authUser = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));
        User user = userRepository.findByAuthUser(authUser)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        List<java.util.Map<String, Object>> doses = new ArrayList<>();
        for (com.example.vaccineManagement.Entity.Dose dose : user.getDoseList()) {
            java.util.Map<String, Object> doseMap = new java.util.LinkedHashMap<>();
            doseMap.put("doseId", dose.getDoseId());
            doseMap.put("doseNumber", dose.getDoseNumber());
            doseMap.put("vaccineName", dose.getVaccine() != null ? dose.getVaccine().getVaccineName() : "N/A");
            doseMap.put("batchNumber", dose.getVaccine() != null ? dose.getVaccine().getBatchNumber() : "N/A");
            doseMap.put("dosesRequired", dose.getVaccine() != null ? dose.getVaccine().getDosesRequired() : 0);
            doseMap.put("vialSerialNumber", dose.getVialSerialNumber());
            doseMap.put("vaccinationDate", dose.getVaccinationDate());
            doses.add(doseMap);
        }
        return doses;
    }
}
