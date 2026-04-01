package com.charityconnect.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender);
    }

    @Test
    void shouldSimulateSuccessWithPlaceholderCredentials() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "your-gmail@gmail.com");
        ReflectionTestUtils.setField(emailService, "mailPassword", "some-password");
        
        EmailService.MailResponse response = emailService.sendVolunteerConfirmation("test@example.com", "Action Title", "User Name");
        
        assertEquals("SIMULATED", response.status());
        assertEquals("Volunteer Confirmation: Action Title", response.subject());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldSimulateSuccessWithPlaceholderPassword() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "real-email@gmail.com");
        ReflectionTestUtils.setField(emailService, "mailPassword", "your-app-password");
        
        EmailService.MailResponse response = emailService.sendVolunteerConfirmation("test@example.com", "Action Title", "User Name");
        
        assertEquals("SIMULATED", response.status());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldCallMailSenderWithRealCredentials() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "real-email@gmail.com");
        ReflectionTestUtils.setField(emailService, "mailPassword", "real-password");
        
        EmailService.MailResponse response = emailService.sendVolunteerConfirmation("test@example.com", "Action Title", "User Name");
        
        verify(mailSender).send(any(SimpleMailMessage.class));
        assertEquals("SUCCESS", response.status());
    }
}
