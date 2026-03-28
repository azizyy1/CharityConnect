package com.charityconnect.controller;

import com.charityconnect.model.ActionStatus;
import com.charityconnect.model.CharityAction;
import com.charityconnect.model.Organization;
import com.charityconnect.model.User;
import jakarta.validation.Valid;
import com.charityconnect.repository.CharityActionRepository;
import com.charityconnect.repository.OrganizationRepository;
import com.charityconnect.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final CharityActionRepository charityActionRepository;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        Organization organization = getCurrentOrganization(authentication);
        List<CharityAction> actions = charityActionRepository.findByOrganization(organization);
        
        long activeActions = actions.stream()
                .filter(a -> a.getStatus() == ActionStatus.ACTIVE)
                .count();
        
        BigDecimal totalCollected = actions.stream()
                .map(a -> a.getCollectedAmount() != null ? a.getCollectedAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("totalActions", actions.size());
        model.addAttribute("activeActions", activeActions);
        model.addAttribute("totalCollected", totalCollected);
        model.addAttribute("organization", organization);
        
        return "organization/dashboard";
    }

    @GetMapping("/actions")
    public String listMyActions(Authentication authentication, Model model) {
        Organization organization = getCurrentOrganization(authentication);
        model.addAttribute("actions", charityActionRepository.findByOrganization(organization));
        model.addAttribute("organization", organization);
        return "organization/actions";
    }

    @GetMapping("/actions/new")
    public String newActionForm(Model model) {
        CharityAction action = CharityAction.builder()
                .targetAmount(BigDecimal.ZERO)
                .collectedAmount(BigDecimal.ZERO)
                .status(ActionStatus.ACTIVE)
                .build();
        model.addAttribute("action", action);
        model.addAttribute("formMode", "create");
        return "organization/action-form";
    }

    @PostMapping("/actions")
    public String createAction(@Valid @ModelAttribute("action") CharityAction formAction,
                               BindingResult bindingResult,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "create");
            return "organization/action-form";
        }

        Organization organization = getCurrentOrganization(authentication);

        formAction.setId(null);
        formAction.setOrganization(organization);
        if (formAction.getCollectedAmount() == null) {
            formAction.setCollectedAmount(BigDecimal.ZERO);
        }
        if (formAction.getStatus() == null) {
            formAction.setStatus(ActionStatus.ACTIVE);
        }

        charityActionRepository.save(formAction);
        redirectAttributes.addFlashAttribute("message", "Action créée avec succès.");
        return "redirect:/organization/actions";
    }

    @GetMapping("/actions/{id}/edit")
    public String editActionForm(@PathVariable Long id,
                                 Authentication authentication,
                                 Model model) {
        Organization organization = getCurrentOrganization(authentication);
        CharityAction action = charityActionRepository.findByIdAndOrganization(id, organization)
                .orElseThrow(() -> new IllegalArgumentException("Action introuvable pour cette organisation."));

        model.addAttribute("action", action);
        model.addAttribute("formMode", "edit");
        return "organization/action-form";
    }

    @PostMapping("/actions/{id}")
    public String updateAction(@Valid @PathVariable Long id,
                               @ModelAttribute("action") CharityAction formAction, BindingResult bindingResult,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            formAction.setId(id);
            model.addAttribute("formMode", "edit");
            return "organization/action-form";
        }
        Organization organization = getCurrentOrganization(authentication);
        CharityAction existingAction = charityActionRepository.findByIdAndOrganization(id, organization)
                .orElseThrow(() -> new IllegalArgumentException("Action introuvable pour cette organisation."));

        existingAction.setTitle(formAction.getTitle());
        existingAction.setDescription(formAction.getDescription());
        existingAction.setCategory(formAction.getCategory());
        existingAction.setLocation(formAction.getLocation());
        existingAction.setTargetAmount(formAction.getTargetAmount());
        existingAction.setStartDate(formAction.getStartDate());
        existingAction.setEndDate(formAction.getEndDate());
        existingAction.setImage(formAction.getImage());

        charityActionRepository.save(existingAction);
        redirectAttributes.addFlashAttribute("message", "Action mise à jour avec succès.");
        return "redirect:/organization/actions";
    }

    @PostMapping("/actions/{id}/archive")
    public String archiveAction(@PathVariable Long id,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        Organization organization = getCurrentOrganization(authentication);
        CharityAction action = charityActionRepository.findByIdAndOrganization(id, organization)
                .orElseThrow(() -> new IllegalArgumentException("Action introuvable pour cette organisation."));

        action.setStatus(ActionStatus.ARCHIVED);
        charityActionRepository.save(action);
        redirectAttributes.addFlashAttribute("message", "Action archivée.");
        return "redirect:/organization/actions";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        Organization organization = getCurrentOrganization(authentication);
        model.addAttribute("organization", organization);
        return "organization/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Authentication authentication, Model model) {
        Organization organization = getCurrentOrganization(authentication);
        model.addAttribute("organization", organization);
        return "organization/profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@Valid @ModelAttribute("organization") Organization formOrg,
                                 BindingResult bindingResult,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "organization/profile-edit";
        }

        Organization organization = getCurrentOrganization(authentication);
        organization.setName(formOrg.getName());
        organization.setTaxId(formOrg.getTaxId());
        organization.setLegalAddress(formOrg.getLegalAddress());
        organization.setDescription(formOrg.getDescription());
        organization.setLogo(formOrg.getLogo());

        organizationRepository.save(organization);
        redirectAttributes.addFlashAttribute("message", "Profil de l'organisation mis à jour avec succès.");
        return "redirect:/organization/profile";
    }

    private Organization getCurrentOrganization(Authentication authentication) {
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        return organizationRepository.findByUser(currentUser)
                .orElseThrow(() -> new IllegalArgumentException("Profil organisation introuvable."));
    }
}
