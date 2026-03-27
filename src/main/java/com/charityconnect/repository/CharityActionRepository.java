package com.charityconnect.repository;

import com.charityconnect.model.ActionStatus;
import com.charityconnect.model.CharityAction;
import com.charityconnect.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CharityActionRepository extends JpaRepository<CharityAction, Long> {
    List<CharityAction> findByStatus(ActionStatus status);
    List<CharityAction> findByCategoryIgnoreCaseAndStatus(String category, ActionStatus status);
    List<CharityAction> findByTitleContainingIgnoreCaseAndStatus(String keyword, ActionStatus status);
    List<CharityAction> findByOrganization(Organization organization);
}