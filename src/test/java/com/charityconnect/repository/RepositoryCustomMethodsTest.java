package com.charityconnect.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.charityconnect.model.ActionStatus;
import com.charityconnect.model.CharityAction;
import com.charityconnect.model.Organization;
import com.charityconnect.model.Role;
import com.charityconnect.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

@DataMongoTest
class RepositoryCustomMethodsTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private CharityActionRepository charityActionRepository;

    @Test
    void userRepositoryShouldFindByEmailAndExistsByEmail() {
        User user = User.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .email("jean.dupont@test.com")
                .password("password123")
                .role(Role.ROLE_USER)
                .enabled(true)
                .build();

        userRepository.save(user);

        assertThat(userRepository.existsByEmail("jean.dupont@test.com")).isTrue();
        assertThat(userRepository.findByEmail("jean.dupont@test.com")).isPresent();
    }

    @Test
    void charityActionRepositoryShouldFilterByOrganizationAndStatus() {
        User orgUser = userRepository.save(User.builder()
                .firstName("Org")
                .lastName("Owner")
                .email("org@test.com")
                .password("password123")
                .role(Role.ROLE_ORGANIZATION)
                .enabled(true)
                .build());

        Organization organization = organizationRepository.save(Organization.builder()
                .name("Hope Org")
                .legalAddress("1 Rue Solidaire")
                .taxId("TAX-123")
                .description("Organisation test")
                .approved(true)
                .user(orgUser)
                .build());

        CharityAction action = charityActionRepository.save(CharityAction.builder()
                .title("Collecte alimentaire")
                .description("Description")
                .category("Santé")
                .location("Paris")
                .targetAmount(new BigDecimal("1000.00"))
                .collectedAmount(new BigDecimal("120.00"))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .status(ActionStatus.ACTIVE)
                .organization(organization)
                .build());

        assertThat(charityActionRepository.findByOrganization(organization)).hasSize(1);
        assertThat(charityActionRepository.findByIdAndOrganization(action.getId(), organization)).contains(action);
        assertThat(charityActionRepository.findByCategoryIgnoreCaseAndStatus("santé", ActionStatus.ACTIVE)).hasSize(1);
    }
}
