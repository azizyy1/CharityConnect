package com.charityconnect.controller;

import com.charityconnect.model.Donation;
import com.charityconnect.model.Participation;
import com.charityconnect.model.User;
import com.charityconnect.repository.DonationRepository;
import com.charityconnect.repository.ParticipationRepository;
import com.charityconnect.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;
    private final DonationRepository donationRepository;

    @GetMapping("/user/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        List<Donation> donations = donationRepository.findByUserOrderByDonationDateDesc(user);
        List<Participation> participations = participationRepository.findByUserOrderByParticipationDateDesc(user);

        BigDecimal totalDonated = donations.stream()
                .map(Donation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("user", user);
        model.addAttribute("totalDonations", donations.size());
        model.addAttribute("totalDonated", totalDonated);
        model.addAttribute("totalParticipations", participations.size());

        return "user/dashboard";
    }

    @GetMapping("/user/participations")
    public String participations(Authentication authentication, Model model) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        model.addAttribute("user", user);
        model.addAttribute("participations", participationRepository.findByUserOrderByParticipationDateDesc(user));
        return "user/participations";
    }

    @GetMapping("/user/donations")
    public String donations(Authentication authentication, Model model) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        model.addAttribute("user", user);
        model.addAttribute("donations", donationRepository.findByUserOrderByDonationDateDesc(user));
        return "user/donations";
    }

    @GetMapping("/user/profile")
    public String profile(Authentication authentication, Model model) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/user/profile/edit")
    public String editProfileForm(Authentication authentication, Model model) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        model.addAttribute("user", user);
        return "user/profile-edit";
    }

    @GetMapping("/user/interests")
    public String selectInterestsForm(Authentication authentication, Model model) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        model.addAttribute("user", user);
        return "user/interests";
    }

    @PostMapping("/user/interests")
    public String saveInterests(@org.springframework.validation.annotation.Validated User formUser,
                                 org.springframework.validation.BindingResult bindingResult,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        user.setInterests(formUser.getInterests());
        userRepository.save(user);
        
        redirectAttributes.addFlashAttribute("message", "Interests updated successfully. Welcome!");
        return "redirect:/";
    }

    @PostMapping("/user/profile/edit")
    public String updateProfile(@org.springframework.validation.annotation.Validated User formUser,
                                 org.springframework.validation.BindingResult bindingResult,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "user/profile-edit";
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        user.setFirstName(formUser.getFirstName());
        user.setLastName(formUser.getLastName());
        user.setPhone(formUser.getPhone());
        user.setInterests(formUser.getInterests());

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("message", "Profile updated successfully.");
        return "redirect:/user/profile";
    }
}
