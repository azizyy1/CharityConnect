package com.charityconnect.service;

import com.charityconnect.model.CharityAction;
import com.charityconnect.model.Donation;
import com.charityconnect.model.DonationStatus;
import com.charityconnect.model.User;
import com.charityconnect.repository.CharityActionRepository;
import com.charityconnect.repository.DonationRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final CharityActionRepository charityActionRepository;

    @Transactional
    public void donate(User donor, CharityAction action, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif.");
        }

        Donation donation = Donation.builder()
                .amount(amount)
                .status(DonationStatus.SUCCESS)
                .user(donor)
                .charityAction(action)
                .build();

        action.setCollectedAmount(action.getCollectedAmount().add(amount));
        Donation savedDonation = donationRepository.save(donation);
        charityActionRepository.save(action);
        
        System.out.println("[DEBUG_LOG] Donation saved successfully: ID=" + savedDonation.getId() + " for User=" + donor.getEmail());
    }
}
