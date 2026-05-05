package com.charityconnect.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document(collection = "donations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donation {

    @Id
    private String id;

    @NotNull(message = "Amount is required.")
    @Positive(message = "Donation amount must be positive.")
    private BigDecimal amount;

    @NotNull(message = "Donation date is required.")
    @Builder.Default
    private LocalDateTime donationDate = LocalDateTime.now();

    @Builder.Default
    private DonationStatus status = DonationStatus.SUCCESS;

    @Valid
    @DocumentReference
    private User user;

    @Valid
    @DocumentReference
    private CharityAction charityAction;
}
