package com.charityconnect.service;

import jakarta.mail.internet.MimeMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        
        EmailService.MailResponse response = emailService.sendVolunteerConfirmation("test@example.com", "Action Title", "User Name");
        
        assertEquals("SIMULATED", response.status());
        assertEquals("Volunteer Confirmation: Action Title", response.subject());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void shouldSimulateSuccessWithPlaceholderPassword() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "real-email@gmail.com");
        ReflectionTestUtils.setField(emailService, "mailPassword", "your-app-password");
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        
        EmailService.MailResponse response = emailService.sendVolunteerConfirmation("test@example.com", "Action Title", "User Name");
        
        assertEquals("SIMULATED", response.status());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void shouldCallMailSenderWithRealCredentials() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "real-email@gmail.com");
        ReflectionTestUtils.setField(emailService, "mailPassword", "real-password");
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        
        EmailService.MailResponse response = emailService.sendVolunteerConfirmation("test@example.com", "Action Title", "User Name");
        
        verify(mailSender).send(any(MimeMessage.class));
        assertEquals("SUCCESS", response.status());
    }
}
