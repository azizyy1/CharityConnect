package com.charityconnect.repository;

import com.charityconnect.model.ActionStatus;
import com.charityconnect.model.CharityAction;
import com.charityconnect.model.Organization;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharityActionRepository extends JpaRepository<CharityAction, Long> {

    List<CharityAction> findByStatus(ActionStatus status);

    List<CharityAction> findByCategoryIgnoreCaseAndStatus(String category, ActionStatus status);

    List<CharityAction> findByTitleContainingIgnoreCaseAndStatus(String keyword, ActionStatus status);

    List<CharityAction> findByOrganization(Organization organization);

    Optional<CharityAction> findByIdAndOrganization(Long id, Organization organization);
}
