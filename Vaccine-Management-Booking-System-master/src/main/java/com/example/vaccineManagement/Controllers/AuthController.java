package com.example.vaccineManagement.Controllers;

import com.example.vaccineManagement.AppSecurity.JwtService;
import com.example.vaccineManagement.Dtos.AuthDtos.*;
import com.example.vaccineManagement.Entity.AuthUser;
import com.example.vaccineManagement.Services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private  AuthenticationManager authenticationManager;
    @Autowired
    private  JwtService jwtService;

    //REGISTER
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto dto) {
        try {
            System.out.println("This is the FDto Data "+dto);
            String result = authService.register(dto);
            return new ResponseEntity<>(
                    new AuthResponseDto(result, null, null),
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AuthResponseDto(e.getMessage(), null, null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }


    //VERIFY EMAIL
    @PostMapping("/verify-email")
    public ResponseEntity<AuthResponseDto> verifyEmail(@RequestBody VerifyOtpDto dto) {
        try {
            String msg = authService.verifyEmail(dto.getEmail(), dto.getOtp());
            return new ResponseEntity<>(
                    new AuthResponseDto(msg, null, null),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AuthResponseDto(e.getMessage(), null, null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    //USER LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        //Take user from Database
        AuthUser authUser = authService.findByEmail(dto.getEmail());

        //token generate with roles
        String token = jwtService.generateToken(
                authUser.getEmail(),
                authUser.getRole().name()
        );

        return ResponseEntity.ok(
                new AuthResponseDto("Login Successful", token, authUser.getRole().name())
        );
    }

    //UPDATE EMAIL
    @PutMapping("/update-email")
    public ResponseEntity<AuthResponseDto> updateEmail(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @RequestBody UpdateEmailRequestDto dto) {
        try {
            String result = authService.updateEmail(principal.getUsername(), dto);
            return new ResponseEntity<>(
                    new AuthResponseDto(result, null, null),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AuthResponseDto(e.getMessage(), null, null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    //VERIFY UPDATE OTP FOR UPDATE EMAIL
    @PostMapping("/verify-update-otp")
    public ResponseEntity<AuthResponseDto> verifyUpdateOtp(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @RequestBody VerifyUpdateEmailDto dto) {
        try {
            String result = authService.verifyUpdateEmail(
                    principal.getUsername(),
                    dto.getNewEmail(),
                    dto.getOtp()
            );
            return new ResponseEntity<>(
                    new AuthResponseDto(result, null, null),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AuthResponseDto(e.getMessage(), null, null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    //FIND BY EMAIL
    @GetMapping("/by-email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findByEmail(@PathVariable String email) {
        try {
            AuthUser authUser = authService.findByEmail(email);
            return new ResponseEntity<>(authUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AuthResponseDto(e.getMessage(), null, null),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    // FORGOT PASSWORD REQUEST
    @PostMapping("/forgot-password-request")
    public ResponseEntity<AuthResponseDto> forgotPasswordRequest(@RequestBody java.util.Map<String, String> request) {
        try {
            String email = request.get("email");
            String result = authService.forgotPasswordRequest(email);
            return new ResponseEntity<>(
                    new AuthResponseDto(result, null, null),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AuthResponseDto(e.getMessage(), null, null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    // RESET PASSWORD
    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponseDto> resetPassword(@RequestBody java.util.Map<String, Object> request) {
        try {
            String email = (String) request.get("email");
            int otp = Integer.parseInt(request.get("otp").toString());
            String newPassword = (String) request.get("newPassword");
            
            String result = authService.resetPassword(email, otp, newPassword);
            return new ResponseEntity<>(
                    new AuthResponseDto(result, null, null),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AuthResponseDto(e.getMessage(), null, null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

}
