package com.charityconnect.repository;

import com.charityconnect.model.ActionStatus;
import com.charityconnect.model.CharityAction;
import com.charityconnect.model.Organization;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CharityActionRepository extends MongoRepository<CharityAction, String> {

    List<CharityAction> findByStatus(ActionStatus status);

    long countByStatus(ActionStatus status);

    List<CharityAction> findByCategoryIgnoreCaseAndStatus(String category, ActionStatus status);

    List<CharityAction> findByTitleContainingIgnoreCaseAndStatus(String keyword, ActionStatus status);

    List<CharityAction> findByOrganization(Organization organization);

    Optional<CharityAction> findByIdAndOrganization(String id, Organization organization);

    List<CharityAction> findByStatusOrderByStartDateDesc(ActionStatus status);

    List<CharityAction> findByStartDateGreaterThanEqualOrderByStartDateDesc(LocalDate date);

    List<CharityAction> findByStatusAndStartDateGreaterThanEqualOrderByStartDateDesc(ActionStatus status,
                                                                                     LocalDate date);
}
