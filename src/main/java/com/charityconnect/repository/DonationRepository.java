package com.charityconnect.repository;

import com.charityconnect.model.CharityAction;
import com.charityconnect.model.Donation;
import com.charityconnect.model.DonationStatus;
import java.time.LocalDateTime;
import com.charityconnect.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    List<Donation> findByUser(User user);

    List<Donation> findByCharityAction(CharityAction charityAction);

    List<Donation> findByStatusOrderByDonationDateDesc(DonationStatus status);

    List<Donation> findByDonationDateGreaterThanEqualOrderByDonationDateDesc(LocalDateTime dateTime);

    List<Donation> findByStatusAndDonationDateGreaterThanEqualOrderByDonationDateDesc(DonationStatus status,
                                                                                      LocalDateTime dateTime);
}
