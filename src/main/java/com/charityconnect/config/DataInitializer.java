package com.charityconnect.config;

import com.charityconnect.model.ActionStatus;
import com.charityconnect.model.CharityAction;
import com.charityconnect.model.Organization;
import com.charityconnect.model.Role;
import com.charityconnect.model.User;
import com.charityconnect.repository.CharityActionRepository;
import com.charityconnect.repository.OrganizationRepository;
import com.charityconnect.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final CharityActionRepository charityActionRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDefaultData() {
        return args -> {
            createUserIfMissing("admin@charityconnect.com", "Admin", "System", Role.ROLE_ADMIN, "Admin@123");
            User organizationUser = createUserIfMissing("org@charityconnect.com", "Org", "Manager", Role.ROLE_ORGANIZATION, "Org@12345");
            createUserIfMissing("user@charityconnect.com", "Demo", "User", Role.ROLE_USER, "User@12345");

            Organization organization = organizationRepository.findByUser(organizationUser)
                    .orElseGet(() -> organizationRepository.save(Organization.builder()
                            .name("Hope Association")
                            .legalAddress("Casablanca")
                            .taxId("HC-2026")
                            .description("Organisation de démonstration pour CharityConnect")
                            .approved(true)
                            .user(organizationUser)
                            .build()));

            if (charityActionRepository.count() == 0) {
                charityActionRepository.save(CharityAction.builder()
                        .title("Aide scolaire pour enfants")
                        .description("Collecte pour kits scolaires et fournitures de 120 enfants.")
                        .category("Education")
                        .location("Rabat")
                        .targetAmount(new BigDecimal("10000.00"))
                        .collectedAmount(new BigDecimal("2500.00"))
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusMonths(2))
                        .status(ActionStatus.ACTIVE)
                        .organization(organization)
                        .build());
            }
        };
    }

    private User createUserIfMissing(String email,
                                     String firstName,
                                     String lastName,
                                     Role role,
                                     String rawPassword) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .password(passwordEncoder.encode(rawPassword))
                        .role(role)
                        .enabled(true)
                        .build()));
    }
}
