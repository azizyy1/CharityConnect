package com.charityconnect.controller;

import com.charityconnect.model.ActionStatus;
import com.charityconnect.model.CharityAction;
import com.charityconnect.model.User;
import com.charityconnect.repository.CharityActionRepository;
import com.charityconnect.repository.UserRepository;
import com.charityconnect.service.DonationService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CharityActionController {

    private final CharityActionRepository charityActionRepository;
    private final UserRepository userRepository;
    private final DonationService donationService;

    @GetMapping("/actions")
    public String listActions(@RequestParam(required = false) String category,
                              @RequestParam(required = false) String keyword,
                              Model model) {
        if (category != null && !category.isBlank()) {
            model.addAttribute("actions", charityActionRepository.findByCategoryIgnoreCaseAndStatus(category, ActionStatus.ACTIVE));
        } else if (keyword != null && !keyword.isBlank()) {
            model.addAttribute("actions", charityActionRepository.findByTitleContainingIgnoreCaseAndStatus(keyword, ActionStatus.ACTIVE));
        } else {
            model.addAttribute("actions", charityActionRepository.findByStatus(ActionStatus.ACTIVE));
        }

        return "action/list";
    }

    @GetMapping("/actions/{id}")
    public String actionDetails(@PathVariable Long id, Model model) {
        CharityAction action = charityActionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Action introuvable."));

        model.addAttribute("action", action);
        model.addAttribute("progress", calculateProgress(action));
        return "action/details";
    }

    @PostMapping("/actions/{id}/donate")
    public String donate(@PathVariable Long id,
                         @RequestParam BigDecimal amount,
                         Authentication authentication) {
        CharityAction action = charityActionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Action introuvable."));

        User donor = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        donationService.donate(donor, action, amount);
        return "redirect:/actions/" + id + "?donated";
    }

    private int calculateProgress(CharityAction action) {
        if (action.getTargetAmount() == null || action.getTargetAmount().signum() == 0) {
            return 0;
        }
        BigDecimal percent = action.getCollectedAmount()
                .multiply(BigDecimal.valueOf(100))
                .divide(action.getTargetAmount(), 0, java.math.RoundingMode.DOWN);
        return Math.min(percent.intValue(), 100);
    }
}
