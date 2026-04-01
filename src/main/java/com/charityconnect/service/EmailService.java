package com.charityconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    
    @org.springframework.beans.factory.annotation.Value("${spring.mail.username}")
    private String fromEmail;

    public String sendVolunteerConfirmation(String to, String actionTitle, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Volunteer Confirmation: " + actionTitle);
        message.setText("Dear " + userName + ",\n\n" +
                "Thank you for signing up to volunteer for '" + actionTitle + "'.\n" +
                "Your request has been received and the team will contact you soon.\n\n" +
                "Best regards,\n" +
                "CharityConnect Team");
        
        try {
            System.out.println("[DEBUG_LOG] Sending email to: " + to + " from: " + fromEmail);
            mailSender.send(message);
            System.out.println("[DEBUG_LOG] Email sent successfully to " + to + "!");
            return "SUCCESS";
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] FAILED to send email to: " + to);
            System.err.println("[DEBUG_LOG] Error details: " + e.getMessage());
            String errorMessage = e.getMessage();
            if (fromEmail != null && fromEmail.contains("your-gmail@gmail.com")) {
                errorMessage = "You are still using placeholder credentials in application.properties! Please update spring.mail.username and spring.mail.password.";
            }
            e.printStackTrace();
            return errorMessage;
        }
    }
}
