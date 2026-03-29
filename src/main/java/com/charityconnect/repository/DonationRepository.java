package com.charityconnect.repository;

import com.charityconnect.model.CharityAction;
import com.charityconnect.model.Donation;
import com.charityconnect.model.DonationStatus;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.charityconnect.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d")
    BigDecimal sumAllDonations();

    List<Donation> findByUserOrderByDonationDateDesc(User user);

    List<Donation> findByCharityAction(CharityAction charityAction);

    List<Donation> findByStatusOrderByDonationDateDesc(DonationStatus status);

    List<Donation> findByDonationDateGreaterThanEqualOrderByDonationDateDesc(LocalDateTime dateTime);

    List<Donation> findByStatusAndDonationDateGreaterThanEqualOrderByDonationDateDesc(DonationStatus status,
                                                                                      LocalDateTime dateTime);
}
