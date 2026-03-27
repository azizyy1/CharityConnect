package com.charityconnect.controller;

import com.charityconnect.model.User;
import com.charityconnect.repository.ParticipationRepository;
import com.charityconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;

    @GetMapping("/user/dashboard")
    public String dashboard() {
        return "user/dashboard";
    }

    @GetMapping("/user/participations")
    public String participations(Authentication authentication, Model model) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        model.addAttribute("participations", participationRepository.findByUserOrderByParticipationDateDesc(user));
        return "user/participations";
    }

}
