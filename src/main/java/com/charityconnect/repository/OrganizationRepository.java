package com.charityconnect.repository;

import com.charityconnect.model.Organization;
import com.charityconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByUser(User user);
    List<Organization> findByApprovedFalse();
}