package com.charityconnect.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de l'organisation est obligatoire.")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "L'adresse légale est obligatoire.")
    private String legalAddress;

    @NotBlank(message = "Le numéro fiscal est obligatoire.")
    @Column(unique = true)
    private String taxId;

    @NotBlank(message = "La description est obligatoire.")
    @Column(length = 2000)
    private String description;

    private String logo;

    @Column(nullable = false)
    private boolean approved;

    @Valid
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
