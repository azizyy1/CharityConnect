package com.charityconnect.controller;

import com.charityconnect.model.ActionStatus;
import com.charityconnect.model.CharityAction;
import com.charityconnect.model.Organization;
import com.charityconnect.model.Role;
import com.charityconnect.model.User;
import com.charityconnect.repository.CharityActionRepository;
import com.charityconnect.repository.OrganizationRepository;
import com.charityconnect.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final CharityActionRepository charityActionRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.charityconnect.repository.DonationRepository donationRepository;
    private final com.charityconnect.repository.ParticipationRepository participationRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("actions", charityActionRepository.findByStatus(ActionStatus.ACTIVE));
        
        long activeCampaigns = charityActionRepository.count(); // Actually findByStatus(ACTIVE).size() would be better but let's keep it simple for now
        java.math.BigDecimal totalDonations = donationRepository.findAll().stream()
                .map(com.charityconnect.model.Donation::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        long volunteerHours = participationRepository.count() * 4; // Arbitrary calculation for display

        model.addAttribute("activeCampaigns", activeCampaigns);
        model.addAttribute("totalDonations", totalDonations);
        model.addAttribute("volunteerHours", volunteerHours);
        
        return "index";
    }

    @GetMapping("/about")
    public String aboutPage() {
        return "about";
    }

    @GetMapping("/events")
    public String eventsPage(Model model) {
        model.addAttribute("events", charityActionRepository.findByCategoryIgnoreCaseAndStatus("Event", ActionStatus.ACTIVE));
        return "events";
    }

    @GetMapping("/events/register/{id}")
    public String registerEventPage(@PathVariable Long id, Model model) {
        CharityAction action = charityActionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));
        model.addAttribute("event", action);
        return "events/register";
    }

    @GetMapping("/blog")
    public String blogPage() {
        return "blog";
    }

    @GetMapping("/contact")
    public String contactPage() {
        return "contact";
    }

    @GetMapping("/volunteer")
    public String volunteerPage(Model model) {
        model.addAttribute("opportunities", charityActionRepository.findByStatus(ActionStatus.ACTIVE));
        return "volunteer";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (userRepository.existsByEmail(user.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "This email is already in use.");
            bindingResult.rejectValue("email", "email.exists", "This email is already in use.");
        }

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("message", "Account created successfully.");
        return "redirect:/login?registered";
    }

    @GetMapping("/register/organization")
    public String registerOrganizationPage() {
        return "auth/register-organization";
    }

    @PostMapping("/register/organization")
    public String registerOrganization(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam(required = false) String phone,
            @RequestParam String password,
            @RequestParam String organizationName,
            @RequestParam(required = false) String legalAddress,
            @RequestParam(required = false) String taxId,
            @RequestParam(required = false) String description
    ) {
        if (userRepository.existsByEmail(email)) {
            return "redirect:/register/organization?error=email_exists";
        }

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .password(passwordEncoder.encode(password))
                .role(Role.ROLE_ORGANIZATION)
                .enabled(true)
                .build();
        User savedUser = userRepository.save(user);

        Organization organization = Organization.builder()
                .name(organizationName)
                .legalAddress(legalAddress)
                .taxId(taxId)
                .description(description)
                .approved(false)
                .user(savedUser)
                .build();
        organizationRepository.save(organization);

        return "redirect:/login?organization_pending";
    }

    @Getter
    @Setter
    public static class OrganizationRegistrationForm {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String password;

        private String name;
        private String legalAddress;
        private String taxId;
        private String description;
    }
}
