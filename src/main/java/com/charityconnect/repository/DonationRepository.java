package com.charityconnect.repository;

import com.charityconnect.model.CharityAction;
import com.charityconnect.model.Donation;
import com.charityconnect.model.DonationStatus;
import com.charityconnect.dto.DonationSum;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.charityconnect.model.User;
import java.util.List;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DonationRepository extends MongoRepository<Donation, String> {

    @Aggregation("{ $group: { _id: null, total: { $sum: '$amount' } } }")
    DonationSum sumAllDonationsAggregation();

    default BigDecimal sumAllDonations() {
        DonationSum result = sumAllDonationsAggregation();
        return (result != null && result.getTotal() != null) ? result.getTotal() : BigDecimal.ZERO;
    }

    List<Donation> findByUserOrderByDonationDateDesc(User user);

    List<Donation> findByCharityAction(CharityAction charityAction);

    List<Donation> findByStatusOrderByDonationDateDesc(DonationStatus status);

    List<Donation> findByDonationDateGreaterThanEqualOrderByDonationDateDesc(LocalDateTime dateTime);

    List<Donation> findByStatusAndDonationDateGreaterThanEqualOrderByDonationDateDesc(DonationStatus status,
                                                                                      LocalDateTime dateTime);
}
