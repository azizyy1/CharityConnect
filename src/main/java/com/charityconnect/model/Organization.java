package com.charityconnect.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document(collection = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

    @Id
    private String id;

    @NotBlank(message = "Organization name is required.")
    private String name;

    @NotBlank(message = "Legal address is required.")
    private String legalAddress;

    @NotBlank(message = "Tax ID is required.")
    @Indexed(unique = true)
    private String taxId;

    @NotBlank(message = "Description is required.")
    private String description;

    private String logo;

    private boolean approved;

    @Valid
    @DocumentReference
    private User user;
}
