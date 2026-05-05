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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
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

        try {
            Organization organization = getCurrentOrganization(authentication);

            formAction.setId(null);
            formAction.setOrganization(organization);
            
            handleFileUploads(formAction);

            if (formAction.getCollectedAmount() == null) {
                formAction.setCollectedAmount(BigDecimal.ZERO);
            }
            if (formAction.getStatus() == null) {
                formAction.setStatus(ActionStatus.ACTIVE);
            }

            charityActionRepository.save(formAction);
            redirectAttributes.addFlashAttribute("message", "Action created successfully.");
            return "redirect:/organization/actions";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("formMode", "create");
            return "organization/action-form";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            model.addAttribute("formMode", "create");
            return "organization/action-form";
        }
    }

    @GetMapping("/actions/{id}/edit")
    public String editActionForm(@PathVariable String id,
                                 Authentication authentication,
                                 Model model) {
        Organization organization = getCurrentOrganization(authentication);
        CharityAction action = charityActionRepository.findByIdAndOrganization(id, organization)
                .orElseThrow(() -> new IllegalArgumentException("Action not found for this organization."));

        model.addAttribute("action", action);
        model.addAttribute("formMode", "edit");
        return "organization/action-form";
    }

    @PostMapping("/actions/{id}")
    public String updateAction(@PathVariable String id,
                               @Valid @ModelAttribute("action") CharityAction formAction,
                               BindingResult bindingResult,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            formAction.setId(id);
            model.addAttribute("formMode", "edit");
            return "organization/action-form";
        }

        try {
            Organization organization = getCurrentOrganization(authentication);
            CharityAction existingAction = charityActionRepository.findByIdAndOrganization(id, organization)
                    .orElseThrow(() -> new IllegalArgumentException("Action not found for this organization."));

            existingAction.setTitle(formAction.getTitle());
            existingAction.setDescription(formAction.getDescription());
            existingAction.setCategory(formAction.getCategory());
            existingAction.setLocation(formAction.getLocation());
            existingAction.setTargetAmount(formAction.getTargetAmount());
            existingAction.setStartDate(formAction.getStartDate());
            existingAction.setEndDate(formAction.getEndDate());

            if (formAction.getImage() != null && !formAction.getImage().isBlank()) {
                existingAction.setImage(formAction.getImage());
            }
            if (formAction.getVideoUrl() != null && !formAction.getVideoUrl().isBlank()) {
                existingAction.setVideoUrl(formAction.getVideoUrl());
            }

            formAction.setId(existingAction.getId()); 
            handleFileUploads(formAction);

            if (formAction.getImage() != null && formAction.getImage().startsWith("/images/uploads/")) {
                existingAction.setImage(formAction.getImage());
            }
            if (formAction.getVideoUrl() != null && formAction.getVideoUrl().startsWith("/videos/uploads/")) {
                existingAction.setVideoUrl(formAction.getVideoUrl());
            }

            charityActionRepository.save(existingAction);
            redirectAttributes.addFlashAttribute("message", "Action updated successfully.");
            return "redirect:/organization/actions";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("formMode", "edit");
            return "organization/action-form";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            model.addAttribute("formMode", "edit");
            return "organization/action-form";
        }
    }

    @PostMapping("/actions/{id}/archive")
    public String archiveAction(@PathVariable String id,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        Organization organization = getCurrentOrganization(authentication);
        CharityAction action = charityActionRepository.findByIdAndOrganization(id, organization)
                .orElseThrow(() -> new IllegalArgumentException("Action not found for this organization."));

        action.setStatus(ActionStatus.ARCHIVED);
        charityActionRepository.save(action);
        redirectAttributes.addFlashAttribute("message", "Action archived.");
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
        redirectAttributes.addFlashAttribute("message", "Organization profile updated successfully.");
        return "redirect:/organization/profile";
    }

    private void handleFileUploads(CharityAction action) {
        String baseDir = "src/main/resources/static/";
        
        try {
            // Handle image upload
            if (action.getImageFile() != null && !action.getImageFile().isEmpty()) {
                String fileName = saveFile(action.getImageFile(), baseDir + "images/uploads/");
                action.setImage("/images/uploads/" + fileName);
            }
            
            // Handle video upload
            if (action.getVideoFile() != null && !action.getVideoFile().isEmpty()) {
                String fileName = saveFile(action.getVideoFile(), baseDir + "videos/uploads/");
                action.setVideoUrl("/videos/uploads/" + fileName);
            }
        } catch (IOException e) {
            System.err.println("[DEBUG_LOG] Error saving uploaded file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String saveFile(MultipartFile file, String uploadDirStr) throws IOException {
        Path uploadPath = Paths.get(uploadDirStr);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        String fileName = UUID.randomUUID().toString() + extension;
        Path path = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    private Organization getCurrentOrganization(Authentication authentication) {
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        return organizationRepository.findByUser(currentUser)
                .orElseThrow(() -> new IllegalArgumentException("Organization profile not found."));
    }
}
