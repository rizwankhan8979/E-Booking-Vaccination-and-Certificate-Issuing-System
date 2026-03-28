package com.example.vaccineManagement.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, int otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("rizwankhan60hy@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Your OTP Verification Code");
            message.setText(
                    "Dear User,\n\n"
                            + "Your One-Time Password (OTP) for vaccination booking verification is: "
                            + otp
                            + "\n\nRegards,\n"
                            + "Vaccine Management System"
            );

            mailSender.send(message);
            System.out.println("OTP Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.out.println("Error sending email : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendVaccinationEmail(String toEmail, String userName, int doseNumber, String batchNumber, String vialNumber, String date) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("spring.mail.username"); // or specific email
            mailMessage.setTo(toEmail);
            mailMessage.setSubject("Vaccination Successful - Dose " + doseNumber);

            String text = "Dear " + userName + ",\n\n"
                    + "Congratulations! Your " + (doseNumber == 1 ? "1st" : "2nd") + " dose has been successfully administered.\n\n"
                    + "Here are your vaccination details:\n"
                    + "- Vaccine Batch Number: " + batchNumber + "\n"
                    + "- Vial Barcode: " + vialNumber + "\n"
                    + "- Date & Time: " + date + "\n\n"
                    + "Thank you for getting vaccinated and staying safely protected!\n\n"
                    + "Regards,\nE-Booking Vaccination Certification System";

            mailMessage.setText(text);
            mailSender.send(mailMessage);
            System.out.println("Vaccination Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.out.println("Failed to send email to " + toEmail + ". Reason: " + e.getMessage());
        }
    }
}
