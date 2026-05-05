package com.charityconnect.model;

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
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.web.multipart.MultipartFile;

@Document(collection = "charity_actions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharityAction {

    @Id
    private String id;

    @NotBlank(message = "Title is required.")
    private String title;

    @NotBlank(message = "Description is required.")
    private String description;

    @NotBlank(message = "Category is required.")
    private String category;

    private String location;

    @NotNull(message = "Target amount is required.")
    @DecimalMin(value = "0.01", inclusive = true, message = "Target amount must be positive.")
    @Builder.Default
    private BigDecimal targetAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", inclusive = true, message = "Collected amount cannot be negative.")
    @Builder.Default
    private BigDecimal collectedAmount = BigDecimal.ZERO;

    @NotNull(message = "Start date is required.")
    private LocalDate startDate;

    @NotNull(message = "End date is required.")
    private LocalDate endDate;

    private String image;
    private String videoUrl;

    @Transient
    private MultipartFile imageFile;

    @Transient
    private MultipartFile videoFile;

    @Builder.Default
    private ActionStatus status = ActionStatus.ACTIVE;

    @DocumentReference
    private Organization organization;
}
