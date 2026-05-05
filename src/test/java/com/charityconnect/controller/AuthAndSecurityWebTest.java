package com.charityconnect.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.charityconnect.model.User;
import com.charityconnect.repository.CharityActionRepository;
import com.charityconnect.repository.OrganizationRepository;
import com.charityconnect.repository.UserRepository;
import com.charityconnect.security.CustomSuccessHandler;
import com.charityconnect.security.CustomUserDetailsService;
import com.charityconnect.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {AuthController.class, UserController.class, OrganizationController.class, CharityActionController.class})
@Import(SecurityConfig.class)
class AuthAndSecurityWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private CustomSuccessHandler customSuccessHandler;

    @MockBean
    private OrganizationRepository organizationRepository;

    @MockBean
    private CharityActionRepository charityActionRepository;
    
    @MockBean
    private com.charityconnect.repository.ParticipationRepository participationRepository;

    @MockBean
    private com.charityconnect.repository.DonationRepository donationRepository;

    @MockBean
    private com.charityconnect.service.DonationService donationService;

    @MockBean
    private com.charityconnect.service.EmailService emailService;

    @MockBean
    private com.charityconnect.service.RecommendationService recommendationService;

    @Test
    void loginPageShouldBeAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void registerShouldCreateUserAndRedirectWhenValid() throws Exception {
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "Nina")
                        .param("lastName", "Martin")
                        .param("email", "new@test.com")
                        .param("password", "password123")
                        .param("phone", "0102030405"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/login?registered*"));

        verify(userRepository).save(any());
    }

    @Test
    void registerShouldReturnFormWhenValidationFails() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("email", "not-an-email")
                        .param("password", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("user", "firstName", "lastName", "email", "password"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void protectedUserRouteShouldRedirectToLoginWhenAnonymous() throws Exception {
        mockMvc.perform(get("/user/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void protectedUserRouteShouldBeAccessibleWithUserRole() throws Exception {
        User user = User.builder().email("user@test.com").firstName("Test").lastName("User").build();
        when(userRepository.findByEmail("user@test.com")).thenReturn(java.util.Optional.of(user));
        when(donationRepository.findByUserOrderByDonationDateDesc(any())).thenReturn(java.util.List.of());
        when(participationRepository.findByUserOrderByParticipationDateDesc(any())).thenReturn(java.util.List.of());

        mockMvc.perform(get("/user/dashboard")
                        .with(user("user@test.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("user/dashboard"));
    }

    @Test
    void protectedUserRouteShouldHaveCacheControlHeaders() throws Exception {
        User user = User.builder().email("user@test.com").firstName("Test").lastName("User").build();
        when(userRepository.findByEmail("user@test.com")).thenReturn(java.util.Optional.of(user));
        when(donationRepository.findByUserOrderByDonationDateDesc(any())).thenReturn(java.util.List.of());
        when(participationRepository.findByUserOrderByParticipationDateDesc(any())).thenReturn(java.util.List.of());

        mockMvc.perform(get("/user/dashboard")
                        .with(user("user@test.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate"))
                .andExpect(header().string("Pragma", "no-cache"))
                .andExpect(header().string("Expires", "0"));
    }

    @Test
    void organizationRouteShouldReturnForbiddenForSimpleUserRole() throws Exception {
        mockMvc.perform(get("/organization/dashboard")
                        .with(user("user@test.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void publicPagesShouldBeAccessibleWithoutAuthentication() throws Exception {
        when(charityActionRepository.findByStatus(any())).thenReturn(java.util.List.of());
        when(donationRepository.findAll()).thenReturn(java.util.List.of());
        when(participationRepository.count()).thenReturn(0L);
        when(charityActionRepository.count()).thenReturn(0L);

        mockMvc.perform(get("/")).andExpect(status().isOk());
        mockMvc.perform(get("/about")).andExpect(status().isOk());
        mockMvc.perform(get("/events")).andExpect(status().isOk());
        mockMvc.perform(get("/blog")).andExpect(status().isOk());
        mockMvc.perform(get("/contact")).andExpect(status().isOk());
        mockMvc.perform(get("/volunteer")).andExpect(status().isOk());
        mockMvc.perform(get("/actions")).andExpect(status().isOk());
    }
}
