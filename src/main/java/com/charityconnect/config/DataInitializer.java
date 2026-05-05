package com.charityconnect.config;

import com.charityconnect.model.ActionStatus;
import com.charityconnect.model.CharityAction;
import com.charityconnect.model.Organization;
import com.charityconnect.model.Role;
import com.charityconnect.model.User;
import com.charityconnect.repository.CharityActionRepository;
import com.charityconnect.repository.OrganizationRepository;
import com.charityconnect.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final CharityActionRepository charityActionRepository;
    private final com.charityconnect.repository.ParticipationRepository participationRepository;
    private final com.charityconnect.repository.DonationRepository donationRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDefaultData() {
        return args -> {
            User adminUser = createUserIfMissing("admin@charityconnect.com", "Admin", "System", Role.ROLE_ADMIN, "Admin@123");
            User organizationUser = createUserIfMissing("org@charityconnect.com", "Org", "Manager", Role.ROLE_ORGANIZATION, "Org@12345");
            User demoUser = createUserIfMissing("user@charityconnect.com", "Demo", "User", Role.ROLE_USER, "User@12345");

            Organization organization = organizationRepository.findByUser(organizationUser)
                    .orElseGet(() -> organizationRepository.save(Organization.builder()
                            .name("Hope Association")
                            .legalAddress("Casablanca")
                            .taxId("HC-2026")
                            .description("Demonstration organization for CharityConnect")
                            .approved(true)
                            .user(organizationUser)
                            .build()));

            if (charityActionRepository.count() <= 16) {
                // Clear existing data to re-initialize with better variety if we only have default samples
                if (charityActionRepository.count() > 0) {
                    donationRepository.deleteAll();
                    participationRepository.deleteAll();
                    charityActionRepository.deleteAll();
                }
                class ActionData {
                    String title;
                    String category;
                    String image;
                    String description;
                    ActionData(String t, String c, String i, String d) {
                        this.title = t; this.category = c; this.image = i; this.description = d;
                    }
                }

                ActionData[] sampleActions = {
                    new ActionData(
                        "Orphan Care Essentials",
                        "Youth",
                        "/images/orphan-care.jpg",
                        "Every child deserves a chance at a bright future. Our 'Orphan Care Essentials' program provides orphans with the basic necessities they need to thrive, including nutritious meals, clean clothing, and access to medical care. Your contribution helps us provide a safe and nurturing environment for these children, ensuring they have the resources they need to grow, learn, and succeed. By supporting this campaign, you are investing in the lives of vulnerable children and giving them the hope they need to build a better tomorrow."
                    ),
                    new ActionData(
                        "Emergency Medical Rides",
                        "Health",
                        "/images/emergency-medical.jpg",
                        "In critical moments, every second counts. Many people in remote or underserved areas face significant barriers to accessing life-saving medical care due to lack of transportation. Our 'Emergency Medical Rides' initiative provides free, reliable transport for patients in urgent need. Whether it's a woman in labor, a child with a sudden high fever, or an elderly person suffering from a stroke, your donation ensures they get to a hospital quickly and safely. We operate 24/7 to bridge the gap between emergency and treatment."
                    ),
                    new ActionData(
                        "Animal Rescue Initiative",
                        "Environment",
                        "/images/animal-rescue-initiative.jpg",
                        "Our furry friends often cannot speak for themselves. This initiative is dedicated to rescuing abandoned, abused, and neglected animals. We provide them with immediate veterinary care, shelter, and high-quality food. Our goal is to rehabilitate these animals and find them loving forever homes. Your support covers medical costs, vaccination, and the maintenance of our rescue centers. Together, we can end animal suffering and promote a community that values and protects its animals."
                    ),
                    new ActionData(
                        "Winter Clothing Drive",
                        "Urgent",
                        "/images/winter-clothing-drive.png",
                        "As the temperatures drop, many families struggle to keep warm. A simple coat or a pair of gloves can make a world of difference. This campaign focuses on providing warm winter clothing—including jackets, sweaters, scarves, and boots—to homeless individuals and families in poverty. We distribute these items directly to those living on the streets or in unheated shelters. Your donation will directly purchase new thermal wear and help us collect and clean donated items for distribution."
                    ),
                    new ActionData(
                        "Critical Surgery Fund",
                        "Health",
                        "/images/critical-surgery.jpg",
                        "Life-saving surgeries should not be a luxury. This fund is dedicated to individuals who require urgent operations but lack the financial means to pay for them. From heart surgeries to tumor removals, we work with partner hospitals to identify patients in desperate need. Every dirham you contribute goes towards hospital bills, surgeon fees, and post-operative care. You have the power to give someone a second chance at life by removing the financial barrier to their recovery."
                    ),
                    new ActionData(
                        "School Supply Drive",
                        "Education",
                        "/images/school-supply-drive.png",
                        "Education is the most powerful weapon which you can use to change the world. However, many students start the school year without the basic tools they need. This drive provides backpacks, notebooks, pens, pencils, and calculators to underprivileged children. By ensuring every student is prepared for their classes, we improve their confidence and their ability to learn. Your support helps us equip local schools and individual students, ensuring that no child's education is hindered by a lack of supplies."
                    ),
                    new ActionData(
                        "Women Empowerment Projects",
                        "Education",
                        "/images/women-empowerment.png",
                        "When you empower a woman, you empower a community. Our projects focus on providing women with vocational training, literacy classes, and micro-loans to start their own small businesses. We create safe spaces for learning and growth, helping women gain financial independence and leadership skills. Your contribution supports training materials, expert mentors, and the seed capital needed for these women to transform their lives and the lives of their children."
                    ),
                    new ActionData(
                        "Urgent Medical Funds",
                        "Urgent",
                        "/images/urgent-medical.jpg",
                        "Medical emergencies don't wait for a paycheck. This campaign provides immediate financial assistance for medication, diagnostic tests, and short-term treatments for those in crisis. We focus on providing aid within 24 hours of a verified request. Whether it's insulin for a diabetic patient or oxygen for someone with respiratory issues, your donation provides an immediate lifeline. We ensure that poverty does not mean a death sentence for those facing sudden health challenges."
                    ),
                    new ActionData(
                        "Meals for the Hungry",
                        "Food",
                        "/images/meals-for-the-hungry.png",
                        "No one should go to sleep hungry. Our community kitchen and food pantry program provides hot, nutritious meals and weekly grocery bags to families and individuals in need. We focus on providing balanced nutrition, including fresh produce and protein. Your donation helps us purchase bulk food items, maintain our kitchen equipment, and transport food to elderly or disabled individuals who cannot leave their homes. Every 10 DH you donate can provide a complete meal for a person in need."
                    ),
                    new ActionData(
                        "Disaster Recovery Aid",
                        "Urgent",
                        "/images/disaster-recovery.jpg.avif",
                        "When disaster strikes, the road to recovery is long. This fund provides long-term support for communities affected by natural disasters such as floods or earthquakes. While emergency aid covers immediate needs, we focus on rebuilding homes, restoring clean water systems, and helping local businesses reopen. Your contribution ensures that communities are not forgotten once the news cameras leave. We stay for the duration of the rebuilding process to ensure a resilient recovery."
                    ),
                    new ActionData(
                        "Help Children Access School",
                        "Education",
                        "/images/help-children-access-school.jpg",
                        "In many rural areas, the distance to the nearest school is a major barrier to education. This program provides bicycles for students, funds school bus routes, and helps pay for school uniforms and tuition fees for families in extreme poverty. Our mission is to ensure that every child has the physical and financial means to attend school regularly. Your donation helps us remove the obstacles that keep children out of the classroom, paving the way for a generation of educated and empowered citizens."
                    ),
                    new ActionData(
                        "Acts of Love Change the World",
                        "Event",
                        "/images/acts-of-love.jpg",
                        "Join us for a gala evening dedicated to celebrating our community's impact and raising funds for future projects."
                    ),
                    new ActionData(
                        "Care Flows Where Hands Unite",
                        "Event",
                        "/images/care-flows-where-hands-unite.jpg",
                        "A community-led workshop where volunteers come together to pack care packages for those in need."
                    ),
                    new ActionData(
                        "Building Bridges for the Future",
                        "Event",
                        "/images/building-bridges.png",
                        "A networking event for youth and professionals to share experiences and build mentorship opportunities."
                    ),
                    new ActionData(
                        "Unity in Diversity Festival",
                        "Event",
                        "/images/unity-in-diversity-festival.jpg",
                        "Celebrating the rich cultural heritage of Morocco through music, art, and food."
                    ),
                    new ActionData(
                        "Safe Water Initiative",
                        "Environment",
                        "/images/water-issues.jpg",
                        "Access to clean and safe water is a fundamental human right. Our 'Safe Water Initiative' works to provide sustainable solutions for communities facing water scarcity and contamination. By building wells, installing purification systems, and educating local residents on water management, we aim to ensure long-term health and prosperity. Your support directly funds the infrastructure and expertise needed to bring life-changing clean water to those who need it most."
                    )
                };

                java.util.Random random = new java.util.Random();
                String[] moroccanLocations = {
                    "Congress Palace, Marrakech",
                    "Anfa Park, Casablanca",
                    "National Library, Rabat",
                    "Technopark, Casablanca",
                    "Esplanade of the Kasbah, Tangier",
                    "Michlifen Hotel, Ifrane"
                };

                for (int i = 0; i < sampleActions.length; i++) {
                    ActionData data = sampleActions[i];
                    BigDecimal target = new BigDecimal(15000 + random.nextInt(35000));
                    
                    String location = data.category.equals("Event") ? moroccanLocations[i % moroccanLocations.length] : "Various";
                    LocalDate startDate = data.category.equals("Event") ? LocalDate.now().plusMonths(1 + random.nextInt(6)) : LocalDate.now();

                    int daysToAdd = 5 + random.nextInt(60);
                    CharityAction action = charityActionRepository.save(CharityAction.builder()
                        .title(data.title)
                        .description(data.description)
                        .category(data.category)
                        .image(data.image)
                        .location(location)
                        .targetAmount(target)
                        .collectedAmount(BigDecimal.ZERO)
                        .startDate(startDate)
                        .endDate(startDate.plusDays(daysToAdd))
                        .status(ActionStatus.ACTIVE)
                        .organization(organization)
                        .build());

                    // Generate more natural progress by category:
                    // urgent causes are usually funded faster, while events are often still early.
                    double percentage;
                    if ("Urgent".equals(data.category)) {
                        percentage = 0.78 + (random.nextDouble() * 0.18); // 78% - 96%
                    } else if ("Health".equals(data.category)) {
                        percentage = 0.52 + (random.nextDouble() * 0.28); // 52% - 80%
                    } else if ("Food".equals(data.category)) {
                        percentage = 0.45 + (random.nextDouble() * 0.30); // 45% - 75%
                    } else if ("Education".equals(data.category) || "Youth".equals(data.category)) {
                        percentage = 0.25 + (random.nextDouble() * 0.35); // 25% - 60%
                    } else if ("Environment".equals(data.category)) {
                        percentage = 0.18 + (random.nextDouble() * 0.35); // 18% - 53%
                    } else if ("Event".equals(data.category)) {
                        percentage = 0.05 + (random.nextDouble() * 0.22); // 5% - 27%
                    } else {
                        percentage = 0.20 + (random.nextDouble() * 0.45); // fallback
                    }

                    // Guarantee visible variety: some almost complete, others still at the beginning.
                    if (i % 5 == 0) {
                        percentage = Math.max(percentage, 0.90);
                    } else if (i % 4 == 0) {
                        percentage = Math.min(percentage, 0.22);
                    }

                    // Keep the first visible cards clearly mixed (almost done vs just started)
                    // so the home/actions grids look natural at first glance.
                    if (i < 6) {
                        double[] showcaseProgress = {0.92, 0.64, 0.16, 0.85, 0.24, 0.09};
                        double jitter = (random.nextDouble() - 0.5) * 0.06; // +/- 3%
                        percentage = showcaseProgress[i] + jitter;
                    }

                    percentage = Math.max(0.05, Math.min(percentage, 0.98));

                    BigDecimal targetAmount = action.getTargetAmount();
                    BigDecimal targetCollected = targetAmount.multiply(BigDecimal.valueOf(percentage))
                            .setScale(0, java.math.RoundingMode.HALF_UP);

                    // Distribute this amount into several donations
                    int numDonations = 5 + random.nextInt(10);
                    BigDecimal remainingToCollect = targetCollected;
                    
                    for (int j = 0; j < numDonations; j++) {
                        BigDecimal amount;
                        if (j == numDonations - 1) {
                            amount = remainingToCollect;
                        } else {
                            // Random slice of the remaining amount
                            double slice = 0.05 + (random.nextDouble() * 0.25);
                            amount = remainingToCollect.multiply(BigDecimal.valueOf(slice))
                                    .setScale(0, java.math.RoundingMode.HALF_UP);
                        }
                        
                        if (amount.compareTo(BigDecimal.ZERO) > 0) {
                            donationRepository.save(com.charityconnect.model.Donation.builder()
                                .amount(amount)
                                .user(adminUser)
                                .charityAction(action)
                                .donationDate(java.time.LocalDateTime.now().minusDays(random.nextInt(30)))
                                .build());
                            remainingToCollect = remainingToCollect.subtract(amount);
                        }
                    }
                    
                    action.setCollectedAmount(targetCollected);
                    charityActionRepository.save(action);
                }
            }
        };
    }

    private User createUserIfMissing(String email,
                                     String firstName,
                                     String lastName,
                                     Role role,
                                     String rawPassword) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .password(passwordEncoder.encode(rawPassword))
                        .role(role)
                        .enabled(true)
                        .build()));
    }
}
