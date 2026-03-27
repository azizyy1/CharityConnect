package com.charityconnect.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Organization {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String name;
    private String legalAddress;

    @Column(unique = true)
    private String taxId;

    @Column(length = 2000)
    private String description;

    private String logo;

    @Column(nullable = false)
    private boolean approved;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}