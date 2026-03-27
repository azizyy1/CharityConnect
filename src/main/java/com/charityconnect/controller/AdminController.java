package com.charityconnect.controller;

import com.charityconnect.model.Organization;
import com.charityconnect.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final OrganizationRepository organizationRepository;

    @GetMapping("/admin/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/organizations")
    public String organizations(Model model) {
        model.addAttribute("organizations", organizationRepository.findAll());
        return "admin/organizations";
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
}
