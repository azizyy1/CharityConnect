package com.charityconnect.service;

import com.charityconnect.model.ActionStatus;
import com.charityconnect.model.CharityAction;
import com.charityconnect.model.Donation;
import com.charityconnect.model.User;
import com.charityconnect.repository.CharityActionRepository;
import com.charityconnect.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final CharityActionRepository charityActionRepository;
    private final DonationRepository donationRepository;

    public List<CharityAction> getRecommendations(User user) {
        if (user == null) {
            return List.of();
        }

        Set<String> recommendedCategories = new HashSet<>();

        // 1. Ajouter les catégories basées sur les intérêts de l'utilisateur
        if (user.getInterests() != null) {
            recommendedCategories.addAll(user.getInterests());
        }

        // 2. Ajouter les catégories basées sur les dons passés
        List<Donation> pastDonations = donationRepository.findByUserOrderByDonationDateDesc(user);
        for (Donation donation : pastDonations) {
            if (donation.getCharityAction() != null && donation.getCharityAction().getCategory() != null) {
                recommendedCategories.add(donation.getCharityAction().getCategory());
            }
        }

        // 3. Rechercher les actions actives dans ces catégories
        // On limite à 4 recommandations pour ne pas surcharger
        return charityActionRepository.findByStatus(ActionStatus.ACTIVE).stream()
                .filter(action -> recommendedCategories.contains(action.getCategory()))
                .limit(4)
                .collect(Collectors.toList());
    }
}
