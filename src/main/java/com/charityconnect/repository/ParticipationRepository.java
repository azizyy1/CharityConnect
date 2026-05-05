package com.charityconnect.repository;

import com.charityconnect.model.CharityAction;
import com.charityconnect.model.Participation;
import com.charityconnect.model.User;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParticipationRepository extends MongoRepository<Participation, String> {

    List<Participation> findByUserOrderByParticipationDateDesc(User user);

    List<Participation> findByUserIdOrderByParticipationDateDesc(String userId);

    boolean existsByUserAndCharityAction(User user, CharityAction charityAction);
}
