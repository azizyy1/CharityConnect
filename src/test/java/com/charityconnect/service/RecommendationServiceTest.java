package com.charityconnect.service;

import com.charityconnect.model.ActionStatus;
import com.charityconnect.model.CharityAction;
import com.charityconnect.model.Donation;
import com.charityconnect.model.User;
import com.charityconnect.repository.CharityActionRepository;
import com.charityconnect.repository.DonationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class RecommendationServiceTest {

    @Mock
    private CharityActionRepository charityActionRepository;

    @Mock
    private DonationRepository donationRepository;

    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recommendationService = new RecommendationService(charityActionRepository, donationRepository);
    }

    @Test
    void shouldRecommendBasedOnInterests() {
        User user = User.builder()
                .interests(Set.of("Education", "Health"))
                .build();

        CharityAction action1 = CharityAction.builder().id("1").category("Education").status(ActionStatus.ACTIVE).build();
        CharityAction action2 = CharityAction.builder().id("2").category("Health").status(ActionStatus.ACTIVE).build();
        CharityAction action3 = CharityAction.builder().id("3").category("Environment").status(ActionStatus.ACTIVE).build();

        when(charityActionRepository.findByStatus(ActionStatus.ACTIVE)).thenReturn(List.of(action1, action2, action3));
        when(donationRepository.findByUserOrderByDonationDateDesc(user)).thenReturn(List.of());

        List<CharityAction> recommendations = recommendationService.getRecommendations(user);

        assertEquals(2, recommendations.size());
        assertTrue(recommendations.contains(action1));
        assertTrue(recommendations.contains(action2));
    }

    @Test
    void shouldRecommendBasedOnPastDonations() {
        User user = User.builder().interests(Set.of()).build();
        CharityAction pastAction = CharityAction.builder().category("Food").build();
        Donation donation = Donation.builder().charityAction(pastAction).build();

        CharityAction action1 = CharityAction.builder().id("1").category("Food").status(ActionStatus.ACTIVE).build();
        CharityAction action2 = CharityAction.builder().id("2").category("Health").status(ActionStatus.ACTIVE).build();

        when(charityActionRepository.findByStatus(ActionStatus.ACTIVE)).thenReturn(List.of(action1, action2));
        when(donationRepository.findByUserOrderByDonationDateDesc(user)).thenReturn(List.of(donation));

        List<CharityAction> recommendations = recommendationService.getRecommendations(user);

        assertEquals(1, recommendations.size());
        assertEquals("Food", recommendations.get(0).getCategory());
    }
}
