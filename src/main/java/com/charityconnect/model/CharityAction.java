package com.charityconnect.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharityAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required.")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Description is required.")
    @Column(length = 3000)
    private String description;

    @NotBlank(message = "Category is required.")
    @Column(nullable = false)
    private String category;

    private String location;

    @NotNull(message = "Target amount is required.")
    @DecimalMin(value = "0.01", inclusive = true, message = "Target amount must be positive.")
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal targetAmount;

    @NotNull(message = "Collected amount is required.")
    @DecimalMin(value = "0.00", inclusive = true, message = "Collected amount cannot be negative.")
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal collectedAmount;

    @NotNull(message = "Start date is required.")
    private LocalDate startDate;

    @NotNull(message = "End date is required.")
    private LocalDate endDate;

    private String image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionStatus status;

    @Valid
    @ManyToOne(optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @PrePersist
    void onCreate() {
        if (targetAmount == null) {
            targetAmount = BigDecimal.ZERO;
        }
        if (collectedAmount == null) {
            collectedAmount = BigDecimal.ZERO;
        }
        if (status == null) {
            status = ActionStatus.ACTIVE;
        }
    }
}
