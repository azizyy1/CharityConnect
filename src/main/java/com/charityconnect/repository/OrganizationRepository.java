package com.charityconnect.repository;

import com.charityconnect.model.Organization;
import com.charityconnect.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrganizationRepository extends MongoRepository<Organization, String> {

    Optional<Organization> findByUser(User user);

    List<Organization> findByApprovedFalse();
}
