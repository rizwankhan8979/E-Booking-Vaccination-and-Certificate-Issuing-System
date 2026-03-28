package com.example.vaccineManagement.Services;


import com.example.vaccineManagement.Dtos.AuthDtos.LoginRequestDto;
import com.example.vaccineManagement.Dtos.AuthDtos.RegisterRequestDto;
import com.example.vaccineManagement.Dtos.AuthDtos.UpdateEmailRequestDto;
import com.example.vaccineManagement.Entity.AuthUser;
import com.example.vaccineManagement.Enums.Role;
import com.example.vaccineManagement.Repository.AuthUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //OTP Store
    private final Map<String, Integer> otpStore = new ConcurrentHashMap<>();
    private final Map<String, AuthUser> tempUserStore = new ConcurrentHashMap<>();

    public String register(RegisterRequestDto dto) {
        if (authUserRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered and verified!");
        }

        // create object but not save
        AuthUser authUser = new AuthUser();
        authUser.setEmail(dto.getEmail());
        authUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        authUser.setRole(Role.USER);
        authUser.setEmailVerified(true);

        //Generate Otp
        int otp = new Random().nextInt(900000) + 100000;
        otpStore.put(dto.getEmail(), otp);
        tempUserStore.put(dto.getEmail(), authUser);
        emailService.sendOtpEmail(dto.getEmail(), otp);
        return "OTP sent to email. Data is in temporary storage waiting for verification.";
    }


    public String verifyEmail(String email, int otp) {
        Integer storedOtp = otpStore.get(email);
        AuthUser tempUser = tempUserStore.get(email);
<<<<<<< HEAD

=======
>>>>>>> b4f768d (Updated backend After Create Apis)
        System.out.println("DEBUGGING OTP VERIFICATION ");
        System.out.println("Email: " + email);
        System.out.println("Stored OTP (Server ke paas): " + storedOtp);
        System.out.println("Incoming OTP (Postman se aaya): " + otp);

        //User Data Check Print
        if (tempUser != null) {
            System.out.println("👤 User Data Found in Temp Store:");
            System.out.println("   -> Email: " + tempUser.getEmail());
            System.out.println("   -> Role: " + tempUser.getRole());
            System.out.println("   -> Password (Hash): " + tempUser.getPassword()); // Encrypted password
            System.out.println("   -> Is Verified?: " + tempUser.isEmailVerified()); // Should be true
        } else {
            System.out.println("User Data is NULL (Shayad Register nahi kiya ya Server restart hua)");
        }


        if (storedOtp == null) {
            throw new RuntimeException("OTP expired or not found. Please register again.");
        }

        // 2. OTP Match karo
        if (!storedOtp.equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (tempUser == null) {
            throw new RuntimeException("Session expired, please register again");
        }


        if (tempUser == null) {
            throw new RuntimeException("Session expired, please register again");
        }
<<<<<<< HEAD
        
=======
>>>>>>> b4f768d (Updated backend After Create Apis)
        authUserRepository.save(tempUser);
        otpStore.remove(email);
        tempUserStore.remove(email);

        return "Email verified and User Registered Successfully!";
    }

    //LOGIN
    public String login(LoginRequestDto dto) {
        AuthUser authUser = authUserRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), authUser.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        if (!authUser.isEmailVerified()) {
            throw new RuntimeException("Please verify email first");
        }

        return "Login successful";
    }
<<<<<<< HEAD
=======

>>>>>>> b4f768d (Updated backend After Create Apis)
    public AuthUser findByEmail(String email) {
        return authUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));
    }

    private final Map<String, String> pendingEmailUpdates = new ConcurrentHashMap<>();

    public String updateEmail(String loggedInEmail, UpdateEmailRequestDto dto) {
        String newEmail = dto.getNewEmail();

        if (authUserRepository.findByEmail(newEmail).isPresent()) {
            throw new RuntimeException("This new email is already registered!");
        }
<<<<<<< HEAD
        int otp = new Random().nextInt(900000) + 100000;
        otpStore.put(newEmail, otp); 
        pendingEmailUpdates.put(loggedInEmail, newEmail);
=======

        //OTP Generate karo
        int otp = new Random().nextInt(900000) + 100000;

        // 3. Store in maps
        otpStore.put(newEmail, otp);
        pendingEmailUpdates.put(loggedInEmail, newEmail);

        // 4. Send OTP to NEW Email
>>>>>>> b4f768d (Updated backend After Create Apis)
        emailService.sendOtpEmail(newEmail, otp);

        return "Verification OTP sent to your new email: " + newEmail;
    }

    public String verifyUpdateEmail(String currentEmail, String newEmail, int otp) {
        Integer storedOtp = otpStore.get(newEmail);

        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new RuntimeException("Invalid or Expired OTP");
        }
<<<<<<< HEAD
        
=======

>>>>>>> b4f768d (Updated backend After Create Apis)
        AuthUser user = authUserRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(newEmail);
        authUserRepository.save(user);
        otpStore.remove(newEmail);
        pendingEmailUpdates.remove(currentEmail);

        return "Email updated successfully to " + newEmail;
    }
}
