package com.charityconnect.repository;

import com.charityconnect.model.CharityAction;
import com.charityconnect.model.Participation;
import com.charityconnect.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    List<Participation> findByUserOrderByParticipationDateDesc(User user);

    boolean existsByUserAndCharityAction(User user, CharityAction charityAction);
}
