package com.example.vaccineManagement.Controllers;

import com.example.vaccineManagement.AppSecurity.JwtService;
import com.example.vaccineManagement.Dtos.AuthDtos.*;
import com.example.vaccineManagement.Entity.AuthUser;
import com.example.vaccineManagement.Services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // ================= REGISTER =================
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto dto) {
        try {
            System.out.println("This is the FDto Data "+dto);
            String result = authService.register(dto);
            return new ResponseEntity<>(
                    new AuthResponseDto(result, null),
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AuthResponseDto(e.getMessage(), null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }



//    // REGISTER
//    @PostMapping("/register")
//    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto dto) {
//        try {
//            String result = authService.register(dto);
//            return new ResponseEntity<>(new AuthResponseDto(result), HttpStatus.CREATED);
//        } catch (Exception e) {
//            return new ResponseEntity<>(new AuthResponseDto(e.getMessage()), HttpStatus.BAD_REQUEST);
//        }
//    }



    // ================= VERIFY EMAIL =================
    @PostMapping("/verify-email")
    public ResponseEntity<AuthResponseDto> verifyEmail(@RequestBody VerifyOtpDto dto) {
        try {
            String msg = authService.verifyEmail(dto.getEmail(), dto.getOtp());
            return new ResponseEntity<>(
                    new AuthResponseDto(msg, null),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AuthResponseDto(e.getMessage(), null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }



//    // OTP VERIFY
//    @PostMapping("/verify-email")
//    public ResponseEntity<AuthResponseDto> verifyEmail(@RequestBody VerifyOtpDto dto) {
//        try {
//            String msg = authService.verifyEmail(dto.getEmail(), dto.getOtp());
//            return new ResponseEntity<>(new AuthResponseDto(msg), HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(new AuthResponseDto(e.getMessage()), HttpStatus.BAD_REQUEST);
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        String token = jwtService.generateToken(dto.getEmail());

        return ResponseEntity.ok(
                new AuthResponseDto("Login Successful", token)
        );
    }

    // ================= UPDATE EMAIL =================
    @PutMapping("/update-email")
    public ResponseEntity<AuthResponseDto> updateEmail(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @RequestBody UpdateEmailRequestDto dto) {
        try {
            String result = authService.updateEmail(principal.getUsername(), dto);
            return new ResponseEntity<>(
                    new AuthResponseDto(result, null),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AuthResponseDto(e.getMessage(), null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

//    // update email
//    @PutMapping("/update-email")
//    public ResponseEntity<AuthResponseDto> updateEmail(
//            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
//            @RequestBody UpdateEmailRequestDto dto) {
//        try {
//            String result = authService.updateEmail(principal.getUsername(), dto);
//            return new ResponseEntity<>(new AuthResponseDto(result), HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(new AuthResponseDto(e.getMessage()), HttpStatus.BAD_REQUEST);
//        }
//    }



    // ================= VERIFY UPDATE OTP =================
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
                    new AuthResponseDto(result, null),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AuthResponseDto(e.getMessage(), null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }


//    @PostMapping("/verify-update-otp")
//    public ResponseEntity<AuthResponseDto> verifyUpdateOtp(
//            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
//            @RequestBody VerifyUpdateEmailDto dto) {
//        try {
//            String result = authService.verifyUpdateEmail(principal.getUsername(), dto.getNewEmail(), dto.getOtp());
//            return new ResponseEntity<>(new AuthResponseDto(result), HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(new AuthResponseDto(e.getMessage()), HttpStatus.BAD_REQUEST);
//        }
//    }

    // ================= FIND BY EMAIL =================
    @GetMapping("/by-email/{email}")
    public ResponseEntity<?> findByEmail(@PathVariable String email) {
        try {
            AuthUser authUser = authService.findByEmail(email);
            return new ResponseEntity<>(authUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new AuthResponseDto(e.getMessage(), null),
                    HttpStatus.NOT_FOUND
            );
        }
    }

//    // find by email
//    @GetMapping("/by-email/{email}")
//    public ResponseEntity<?> findByEmail(@PathVariable String email) {
//        try {
//            AuthUser authUser = authService.findByEmail(email);
//            return new ResponseEntity<>(authUser, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(new AuthResponseDto(e.getMessage()), HttpStatus.NOT_FOUND);
//        }
//    }

}
