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
                            + "E-Booking Vaccination and Certificate Issuing System"
            );

            mailSender.send(message);
            System.out.println("OTP Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.out.println("Error sending email : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendAppointmentEmail(String toEmail, String userName, String doctorName, String vaccineName, String date, String time, String centerName, String centerAddress) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("rizwankhan60hy@gmail.com");
            mailMessage.setTo(toEmail);
            mailMessage.setSubject("Vaccination Appointment Confirmed!");

            String text = "Dear " + userName + ",\n\n"
                    + "Your vaccination appointment has been successfully booked.\n\n"
                    + "Details:\n"
                    + "- Vaccine: " + vaccineName + "\n"
                    + "- Doctor: " + doctorName + "\n"
                    + "- Center: " + centerName + "\n"
                    + "- Address: " + centerAddress + "\n"
                    + "- Date: " + date + "\n"
                    + "- Time: " + time + "\n\n"
                    + "Please arrive 15 minutes before your scheduled time.\n\n"
                    + "Regards,\nE-Booking Vaccination and Certificate Issuing System";

            mailMessage.setText(text);
            mailSender.send(mailMessage);
            System.out.println("Appointment Confirmation Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.out.println("Error sending appointment email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendVaccinationEmail(String toEmail, String userName, int doseNumber, String batchNumber, String vialNumber, String date, String time, String doctorName) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("spring.mail.username"); // or specific email
            mailMessage.setTo(toEmail);
            mailMessage.setSubject("Vaccination Successful - Dose " + doseNumber);

            // Helper for ordinal suffix (1st, 2nd, 3rd...)
            String doseSuffix;
            if (doseNumber % 10 == 1 && doseNumber % 100 != 11) doseSuffix = "st";
            else if (doseNumber % 10 == 2 && doseNumber % 100 != 12) doseSuffix = "nd";
            else if (doseNumber % 10 == 3 && doseNumber % 100 != 13) doseSuffix = "rd";
            else doseSuffix = "th";

            String text = "Dear " + userName + ",\n\n"
                    + "Congratulations! Your " + doseNumber + doseSuffix + " dose has been successfully administered.\n\n"
                    + "Here are your vaccination details:\n"
                    + "- Vaccine Batch Number: " + batchNumber + "\n"
                    + "- Vial Barcode: " + vialNumber + "\n"
                    + "- Administered By: " + doctorName + "\n"
                    + "- Date: " + date + "\n"
                    + "- Time: " + time + "\n\n"
                    + "Thank you for getting vaccinated and staying safely protected!\n\n"
                    + "Regards,\nE-Booking Vaccination and Certificate Issuing System";

            mailMessage.setText(text);
            mailSender.send(mailMessage);
            System.out.println("Vaccination Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.out.println("Failed to send email to " + toEmail + ". Reason: " + e.getMessage());
        }
    }
}
