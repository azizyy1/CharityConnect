package com.charityconnect.controller;

import com.charityconnect.model.ActionStatus;
import com.charityconnect.model.User;
import com.charityconnect.model.CharityAction;
import com.charityconnect.model.Organization;
import com.charityconnect.model.Donation;
import com.charityconnect.model.DonationStatus;
import com.charityconnect.repository.DonationRepository;
import com.charityconnect.repository.CharityActionRepository;
import com.charityconnect.repository.OrganizationRepository;
import com.charityconnect.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final CharityActionRepository charityActionRepository;
    private final DonationRepository donationRepository;

    @GetMapping("/admin/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/organizations")
    public String organizations(Model model) {
        model.addAttribute("organizations", organizationRepository.findAll());
        return "admin/organizations";
    }

    @GetMapping("/admin/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @PostMapping("/admin/organizations/{id}/approve")
    public String approveOrganization(@PathVariable Long id,
                                      RedirectAttributes redirectAttributes) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Organisation introuvable."));

        organization.setApproved(true);
        organizationRepository.save(organization);
        redirectAttributes.addFlashAttribute("message", "Organisation validée.");
        return "redirect:/admin/organizations";
    }
    @GetMapping("/admin/actions")
    public String actions(@RequestParam(required = false) ActionStatus status,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                          Model model) {
        List<CharityAction> actions;
        if (status != null && date != null) {
            actions = charityActionRepository.findByStatusAndStartDateGreaterThanEqualOrderByStartDateDesc(status, date);
        } else if (status != null) {
            actions = charityActionRepository.findByStatusOrderByStartDateDesc(status);
        } else if (date != null) {
            actions = charityActionRepository.findByStartDateGreaterThanEqualOrderByStartDateDesc(date);
        } else {
            actions = charityActionRepository.findAll(Sort.by(Sort.Direction.DESC, "startDate"));
        }
        model.addAttribute("actions", actions);
        model.addAttribute("statuses", ActionStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedDate", date);
        return "admin/actions";
    }


    @GetMapping("/admin/donations")
    public String donations(@RequestParam(required = false) DonationStatus status,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                            Model model) {
        List<Donation> donations;
        LocalDateTime dateStart = date != null ? date.atStartOfDay() : null;
        if (status != null && dateStart != null) {
            donations = donationRepository.findByStatusAndDonationDateGreaterThanEqualOrderByDonationDateDesc(status, dateStart);
        } else if (status != null) {
            donations = donationRepository.findByStatusOrderByDonationDateDesc(status);
        } else if (dateStart != null) {
            donations = donationRepository.findByDonationDateGreaterThanEqualOrderByDonationDateDesc(dateStart);
        } else {
            donations = donationRepository.findAll(Sort.by(Sort.Direction.DESC, "donationDate"));
        }

        model.addAttribute("donations", donations);
        model.addAttribute("statuses", DonationStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedDate", date);
        return "admin/donations";
    }

    @PostMapping("/admin/actions/{id}/archive")
    public String archiveAction(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        CharityAction action = charityActionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Action introuvable."));

        action.setStatus(ActionStatus.ARCHIVED);
        charityActionRepository.save(action);
        redirectAttributes.addFlashAttribute("message", "Action archivée avec succès.");
        return "redirect:/admin/actions";
    }

    @PostMapping("/admin/users/{id}/toggle-enabled")
    public String toggleUserEnabled(@PathVariable Long id,
                                    RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        redirectAttributes.addFlashAttribute(
                "message",
                user.isEnabled() ? "Compte activé." : "Compte désactivé."
        );
        return "redirect:/admin/users";
    }
}
