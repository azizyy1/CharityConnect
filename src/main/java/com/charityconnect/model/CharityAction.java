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

    @NotBlank(message = "Le titre est obligatoire.")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "La description est obligatoire.")
    @Column(length = 3000)
    private String description;

    @NotBlank(message = "La catégorie est obligatoire.")
    @Column(nullable = false)
    private String category;

    private String location;

    @NotNull(message = "L'objectif financier est obligatoire.")
    @DecimalMin(value = "0.01", inclusive = true, message = "L'objectif financier doit être positif.")
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal targetAmount;

    @NotNull(message = "Le montant collecté est obligatoire.")
    @DecimalMin(value = "0.00", inclusive = true, message = "Le montant collecté ne peut pas être négatif.")
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal collectedAmount;

    @NotNull(message = "La date de début est obligatoire.")
    private LocalDate startDate;

    @NotNull(message = "La date de fin est obligatoire.")
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
