package com.charityconnect.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    public record MailResponse(String status, String subject, String body, String error) {}

    private final JavaMailSender mailSender;
    
    @org.springframework.beans.factory.annotation.Value("${spring.mail.username}")
    private String fromEmail;

    @org.springframework.beans.factory.annotation.Value("${spring.mail.password}")
    private String mailPassword;

    public MailResponse sendVolunteerConfirmation(String to, String actionTitle, String userName) {
        String subject = "Volunteer Confirmation: " + actionTitle;
        String body = "Dear " + userName + ",\n\n" +
                "Thank you for signing up to volunteer for '" + actionTitle + "'.\n" +
                "Your request has been received and the team will contact you soon.\n\n" +
                "Best regards,\n" +
                "CharityConnect Team";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, "CharityConnect");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            
            boolean isPlaceholder = fromEmail == null || fromEmail.isBlank() || fromEmail.contains("your-gmail@gmail.com") 
                                    || mailPassword == null || mailPassword.contains("your-app-password");
            
            if (isPlaceholder) {
                System.out.println("[DEBUG_LOG] (SIMULATED) Skip real email sending for: " + to + " (Using placeholder credentials)");
                System.out.println("[DEBUG_LOG] (SIMULATED) Subject: " + subject);
                System.out.println("[DEBUG_LOG] (SIMULATED) Content: " + body.substring(0, Math.min(body.length(), 30)) + "...");
                return new MailResponse("SIMULATED", subject, body, null);
            }
            
            System.out.println("[DEBUG_LOG] Sending email to: " + to + " from: CharityConnect <" + fromEmail + ">");
            mailSender.send(message);
            System.out.println("[DEBUG_LOG] Email sent successfully to " + to + "!");
            return new MailResponse("SUCCESS", subject, body, null);
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] FAILED to send email to: " + to);
            System.err.println("[DEBUG_LOG] Error details: " + e.getMessage());
            e.printStackTrace();
            return new MailResponse("ERROR", subject, body, e.getMessage());
        }
    }
}
