package com.charityconnect.controller;

import com.charityconnect.model.ActionStatus;
import com.charityconnect.model.CharityAction;
import com.charityconnect.model.User;
import com.charityconnect.model.Participation;
import com.charityconnect.repository.CharityActionRepository;
import com.charityconnect.repository.ParticipationRepository;
import com.charityconnect.repository.UserRepository;
import com.charityconnect.service.DonationService;
import com.charityconnect.service.EmailService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CharityActionController {

    private final CharityActionRepository charityActionRepository;
    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;
    private final DonationService donationService;
    private final EmailService emailService;

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
                .orElseThrow(() -> new IllegalArgumentException("Action not found."));

        model.addAttribute("action", action);
        model.addAttribute("progress", calculateProgress(action));
        return "action/details";
    }

    @GetMapping("/actions/{id}/volunteer")
    public String volunteerForm(@PathVariable Long id, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        CharityAction action = charityActionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Action not found."));

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        model.addAttribute("action", action);
        model.addAttribute("user", user);
        return "action/volunteer-form";
    }

    @PostMapping("/actions/{id}/donate")
    public String donate(@PathVariable Long id,
                         @RequestParam BigDecimal amount,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        return processDonation(id, amount, authentication, redirectAttributes);
    }

    @PostMapping("/actions/donate-home")
    public String donateHome(@RequestParam Long actionId,
                             @RequestParam BigDecimal amount,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        return processDonation(actionId, amount, authentication, redirectAttributes);
    }

    private String processDonation(Long id, BigDecimal amount, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please log in to make a donation.");
            return "redirect:/login";
        }

        CharityAction action = charityActionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Action not found."));

        User donor = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        donationService.donate(donor, action, amount);
        redirectAttributes.addFlashAttribute("message", "Your generosity changes lives. Thank you from the bottom of our hearts for this precious donation that brings hope and support to those who need it most.");
        return "redirect:/actions/" + id;
    }
    @PostMapping("/actions/{id}/participate")
    public String participate(@PathVariable Long id,
                              @RequestParam(required = false) String note,
                              @RequestParam(required = false) String phone,
                              @RequestParam(required = false) String email,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        CharityAction action = charityActionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Action not found."));

        User participant = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // Update phone if provided
        if (phone != null && !phone.isBlank()) {
            participant.setPhone(phone);
            userRepository.save(participant);
        }

        if (!participationRepository.existsByUserAndCharityAction(participant, action)) {
            participationRepository.save(Participation.builder()
                    .user(participant)
                    .charityAction(action)
                    .note(note)
                    .build());
        }

        // Send confirmation email
        String recipientEmail = (email != null && !email.isBlank()) ? email : participant.getEmail();
        String mailResult = emailService.sendVolunteerConfirmation(recipientEmail, action.getTitle(), participant.getFirstName());

        if ("SUCCESS".equals(mailResult)) {
            redirectAttributes.addFlashAttribute("mailSuccess", true);
            return "redirect:/actions/" + id + "?participated";
        } else {
            redirectAttributes.addFlashAttribute("mailErrorDetail", mailResult + " (Attempted recipient: " + recipientEmail + ")");
            return "redirect:/actions/" + id + "?participated&mailError";
        }
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
