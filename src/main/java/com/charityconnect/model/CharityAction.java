package com.charityconnect.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CharityAction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String title;
    @Column(length = 3000) private String description;

    private String category;
    private String location;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal targetAmount;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal collectedAmount;

    private LocalDate startDate;
    private LocalDate endDate;
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @PrePersist
    void onCreate() {
        if (targetAmount == null) targetAmount = BigDecimal.ZERO;
        if (collectedAmount == null) collectedAmount = BigDecimal.ZERO;
        if (status == null) status = ActionStatus.ACTIVE;
    }
}