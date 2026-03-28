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
            createUserIfMissing("admin@charityconnect.com", "Admin", "System", Role.ROLE_ADMIN, "Admin@123");
            User organizationUser = createUserIfMissing("org@charityconnect.com", "Org", "Manager", Role.ROLE_ORGANIZATION, "Org@12345");
            User demoUser = createUserIfMissing("user@charityconnect.com", "Demo", "User", Role.ROLE_USER, "User@12345");

            Organization organization = organizationRepository.findByUser(organizationUser)
                    .orElseGet(() -> organizationRepository.save(Organization.builder()
                            .name("Hope Association")
                            .legalAddress("Casablanca")
                            .taxId("HC-2026")
                            .description("Organisation de démonstration pour CharityConnect")
                            .approved(true)
                            .user(organizationUser)
                            .build()));

            if (charityActionRepository.count() <= 1) {
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
                        "Orphan care essentials",
                        "Children",
                        "https://images.unsplash.com/photo-1594708767771-a7502209ff51?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        "Every child deserves a chance at a bright future. Our 'Orphan Care Essentials' program provides orphans with the basic necessities they need to thrive, including nutritious meals, clean clothing, and access to medical care. Your contribution helps us provide a safe and nurturing environment for these children, ensuring they have the resources they need to grow, learn, and succeed. By supporting this campaign, you are investing in the lives of vulnerable children and giving them the hope they need to build a better tomorrow."
                    ),
                    new ActionData(
                        "Emergency medical rides",
                        "Health",
                        "https://images.unsplash.com/photo-1581093196277-9f60800987ae?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        "In critical moments, every second counts. Many people in remote or underserved areas face significant barriers to accessing life-saving medical care due to lack of transportation. Our 'Emergency Medical Rides' initiative provides free, reliable transport for patients in urgent need. Whether it's a woman in labor, a child with a sudden high fever, or an elderly person suffering from a stroke, your donation ensures they get to a hospital quickly and safely. We operate 24/7 to bridge the gap between emergency and treatment."
                    ),
                    new ActionData(
                        "Animal rescue initiative",
                        "Animals",
                        "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        "Our furry friends often cannot speak for themselves. This initiative is dedicated to rescuing abandoned, abused, and neglected animals. We provide them with immediate veterinary care, shelter, and high-quality food. Our goal is to rehabilitate these animals and find them loving forever homes. Your support covers medical costs, vaccination, and the maintenance of our rescue centers. Together, we can end animal suffering and promote a community that values and protects its animals."
                    ),
                    new ActionData(
                        "Winter clothing drive",
                        "Humanitarian",
                        "https://images.unsplash.com/photo-1542332606-bdfd259c1814?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        "As the temperatures drop, many families struggle to keep warm. A simple coat or a pair of gloves can make a world of difference. This campaign focuses on providing warm winter clothing—including jackets, sweaters, scarves, and boots—to homeless individuals and families in poverty. We distribute these items directly to those living on the streets or in unheated shelters. Your donation will directly purchase new thermal wear and help us collect and clean donated items for distribution."
                    ),
                    new ActionData(
                        "Critical surgery fund",
                        "Health",
                        "https://images.unsplash.com/photo-1516574187841-cb9cc2ca948b?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        "Life-saving surgeries should not be a luxury. This fund is dedicated to individuals who require urgent operations but lack the financial means to pay for them. From heart surgeries to tumor removals, we work with partner hospitals to identify patients in desperate need. Every dirham you contribute goes towards hospital bills, surgeon fees, and post-operative care. You have the power to give someone a second chance at life by removing the financial barrier to their recovery."
                    ),
                    new ActionData(
                        "School supply drive",
                        "Education",
                        "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        "Education is the most powerful weapon which you can use to change the world. However, many students start the school year without the basic tools they need. This drive provides backpacks, notebooks, pens, pencils, and calculators to underprivileged children. By ensuring every student is prepared for their classes, we improve their confidence and their ability to learn. Your support helps us equip local schools and individual students, ensuring that no child's education is hindered by a lack of supplies."
                    ),
                    new ActionData(
                        "Women empowerment projects",
                        "Empowerment",
                        "https://images.unsplash.com/photo-1484981184820-2e84ea0af397?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        "When you empower a woman, you empower a community. Our projects focus on providing women with vocational training, literacy classes, and micro-loans to start their own small businesses. We create safe spaces for learning and growth, helping women gain financial independence and leadership skills. Your contribution supports training materials, expert mentors, and the seed capital needed for these women to transform their lives and the lives of their children."
                    ),
                    new ActionData(
                        "Urgent medical funds",
                        "Health",
                        "https://images.unsplash.com/photo-1532938911079-1b06ac7ceec7?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        "Medical emergencies don't wait for a paycheck. This campaign provides immediate financial assistance for medication, diagnostic tests, and short-term treatments for those in crisis. We focus on providing aid within 24 hours of a verified request. Whether it's insulin for a diabetic patient or oxygen for someone with respiratory issues, your donation provides an immediate lifeline. We ensure that poverty does not mean a death sentence for those facing sudden health challenges."
                    ),
                    new ActionData(
                        "Meals for the hungry",
                        "Food",
                        "https://images.unsplash.com/photo-1504159506859-f9007d346577?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        "No one should go to sleep hungry. Our community kitchen and food pantry program provides hot, nutritious meals and weekly grocery bags to families and individuals in need. We focus on providing balanced nutrition, including fresh produce and protein. Your donation helps us purchase bulk food items, maintain our kitchen equipment, and transport food to elderly or disabled individuals who cannot leave their homes. Every 10 DH you donate can provide a complete meal for a person in need."
                    ),
                    new ActionData(
                        "Disaster recovery aid",
                        "Relief",
                        "https://images.unsplash.com/photo-1547683905-f686c993aae5?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        "When disaster strikes, the road to recovery is long. This fund provides long-term support for communities affected by natural disasters such as floods or earthquakes. While emergency aid covers immediate needs, we focus on rebuilding homes, restoring clean water systems, and helping local businesses reopen. Your contribution ensures that communities are not forgotten once the news cameras leave. We stay for the duration of the rebuilding process to ensure a resilient recovery."
                    ),
                    new ActionData(
                        "Help children access school",
                        "Education",
                        "https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        "In many rural areas, the distance to the nearest school is a major barrier to education. This program provides bicycles for students, funds school bus routes, and helps pay for school uniforms and tuition fees for families in extreme poverty. Our mission is to ensure that every child has the physical and financial means to attend school regularly. Your donation helps us remove the obstacles that keep children out of the classroom, paving the way for a generation of educated and empowered citizens."
                    ),
                    new ActionData(
                        "Acts of love change the world",
                        "Event",
                        "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        "Join us for a gala evening dedicated to celebrating our community's impact and raising funds for future projects."
                    ),
                    new ActionData(
                        "Care flows where hands unite",
                        "Event",
                        "https://images.unsplash.com/photo-1559027615-cd99713b8bb7?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        "A community-led workshop where volunteers come together to pack care packages for those in need."
                    ),
                    new ActionData(
                        "Building bridges for the future",
                        "Event",
                        "https://images.unsplash.com/photo-1517048676732-d65bc937f952?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        "A networking event for youth and professionals to share experiences and build mentorship opportunities."
                    ),
                    new ActionData(
                        "Unity in diversity festival",
                        "Event",
                        "https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        "Celebrating the rich cultural heritage of Morocco through music, art, and food."
                    ),
                    new ActionData(
                        "Empowerment through education gala",
                        "Event",
                        "https://images.unsplash.com/photo-1540575861501-7ad0582373f2?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        "A formal event to raise awareness and funds for educational scholarships and school infrastructure."
                    )
                };

                java.util.Random random = new java.util.Random();
                String[] moroccanLocations = {
                    "Palais des Congrès, Marrakech",
                    "Anfa Park, Casablanca",
                    "Bibliothèque Nationale, Rabat",
                    "Technopark, Casablanca",
                    "Esplanade de la Kasbah, Tanger",
                    "Hôtel Michlifen, Ifrane"
                };

                for (int i = 0; i < sampleActions.length; i++) {
                    ActionData data = sampleActions[i];
                    BigDecimal target = new BigDecimal(5000 + random.nextInt(45000));
                    BigDecimal collected = target.multiply(new BigDecimal(random.nextDouble() * 0.8)).setScale(2, java.math.RoundingMode.HALF_UP);
                    
                    String location = data.category.equals("Event") ? moroccanLocations[i % moroccanLocations.length] : "Various";
                    LocalDate startDate = data.category.equals("Event") ? LocalDate.now().plusMonths(1 + random.nextInt(6)) : LocalDate.now();

                    CharityAction action = charityActionRepository.save(CharityAction.builder()
                        .title(data.title)
                        .description(data.description)
                        .category(data.category)
                        .image(data.image)
                        .location(location)
                        .targetAmount(target)
                        .collectedAmount(collected)
                        .startDate(startDate)
                        .endDate(startDate.plusDays(30))
                        .status(ActionStatus.ACTIVE)
                        .organization(organization)
                        .build());

                    // Add demo participations for some actions
                    if (i < 3) {
                        participationRepository.save(com.charityconnect.model.Participation.builder()
                                .user(demoUser)
                                .charityAction(action)
                                .participationDate(java.time.LocalDateTime.now().minusDays(i + 1))
                                .note("I'm happy to help with this important cause!")
                                .build());

                        donationRepository.save(com.charityconnect.model.Donation.builder()
                                .amount(new BigDecimal(100 + random.nextInt(400)))
                                .user(demoUser)
                                .charityAction(action)
                                .donationDate(java.time.LocalDateTime.now().minusDays(i + 1))
                                .status(com.charityconnect.model.DonationStatus.SUCCESS)
                                .build());
                    }
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
