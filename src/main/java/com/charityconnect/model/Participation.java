package com.charityconnect.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document(collection = "participations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participation {

    @Id
    private String id;

    @Builder.Default
    private LocalDateTime participationDate = LocalDateTime.now();

    private String note;

    @DocumentReference
    private User user;

    @DocumentReference
    private CharityAction charityAction;
}
