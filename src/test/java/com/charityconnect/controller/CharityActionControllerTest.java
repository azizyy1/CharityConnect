package com.charityconnect.controller;

import com.charityconnect.model.ActionStatus;
import com.charityconnect.model.CharityAction;
import com.charityconnect.model.User;
import com.charityconnect.repository.CharityActionRepository;
import com.charityconnect.repository.ParticipationRepository;
import com.charityconnect.repository.UserRepository;
import com.charityconnect.service.DonationService;
import com.charityconnect.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(CharityActionController.class)
class CharityActionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CharityActionRepository charityActionRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ParticipationRepository participationRepository;

    @MockBean
    private DonationService donationService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private com.charityconnect.service.RecommendationService recommendationService;

    @Test
    @WithMockUser(username = "test@example.com")
    void participateShouldRedirectToSimulationWhenEmailSimulated() throws Exception {
        CharityAction action = CharityAction.builder().id("1").title("Test Action").status(ActionStatus.ACTIVE).build();
        User user = User.builder().email("test@example.com").firstName("Test").build();

        when(charityActionRepository.findById("1")).thenReturn(Optional.of(action));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(emailService.sendVolunteerConfirmation(anyString(), anyString(), anyString()))
                .thenReturn(new EmailService.MailResponse("SIMULATED", "Subj", "Body", null));

        mockMvc.perform(post("/actions/1/participate")
                        .with(csrf())
                        .param("email", "test@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/actions/1?participated&mailSimulated*"))
                .andExpect(flash().attribute("simulatedSubject", "Subj"))
                .andExpect(flash().attribute("simulatedBody", "Body"));
    }
}
