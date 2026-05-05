# CharityConnect

CharityConnect is a modern web platform designed to bridge the gap between donors and charitable organizations. It enables users to discover meaningful causes, participate in events, and make donations to support various initiatives.

## 🚀 Key Features

### For Donors
- **Personalized Recommendations**: Discover actions based on your interests (Education, Health, Environment, etc.) and past contributions.
- **Easy Participation**: Join charitable events and actions with a single click.
- **Donation Tracking**: Support causes financially and track the overall impact.
- **User Dashboard**: Manage your interests, view your participation history, and see your contributions.
- **Onboarding Flow**: New users are guided to select their interests upon their first login to personalize their experience.

### For Organizations
- **Action Management**: Create, update, and manage charitable actions and campaigns.
- **Supporter Insights**: View lists of participants and donors for each action.
- **Verified Profiles**: Organizations undergo an admin approval process to ensure trust and transparency.

### For Administrators
- **Organization Approval**: Review and approve new organization registrations.
- **Global Overview**: Monitor all activities, users, and actions on the platform.

## 🛠 Tech Stack

- **Backend**: Java 17+, Spring Boot 3.5.x
- **Database**: MongoDB (Spring Data MongoDB)
- **Security**: Spring Security (Role-based access control, secure session management)
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript (Vanilla)
- **Localization**: Multi-language support (English & French)
- **Email**: Integrated email notification system (MimeMessage with "CharityConnect" branding)

## 📦 Installation & Setup

### Prerequisites
- **Java 17** or higher
- **Maven** (optional, you can use the included `./mvnw` wrapper)

### Getting Started

1. **Clone the repository**:
   ```bash
   git clone https://github.com/azizyy1/CharityConnect.git
   cd CharityConnect
   ```

2. **Database Configuration**:
   The project uses an **Embedded MongoDB** for development. You don't need to install or run a separate MongoDB instance. It will automatically download and start a local instance when you run the application.

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the site**:
   Open your browser and navigate to **`http://localhost:8081`**.

## 🔐 Default Accounts

For testing purposes, you can use the following pre-configured accounts:

| Role | Email | Password |
|------|-------|----------|
| **Admin** | `admin@charityconnect.com` | `Admin@123` |
| **Organization** | `org@charityconnect.com` | `Org@12345` |
| **User** | `user@charityconnect.com` | `User@12345` |

## 🌍 Localization

The application currently supports **English** and **French**. The language preference is handled via Spring's `LocaleResolver` and can be toggled through the footer/navigation.

## 🛡 Security & Performance

- **Cache Control**: Protected dashboard pages use a strict "no-cache" policy to prevent unauthorized access via the browser's history after logout.
- **Live Stats**: The home page features a dynamic counter that simulates real-time activity for donations and volunteer hours to enhance user engagement.
- **Embedded DB**: Zero-configuration setup for new developers thanks to Flapdoodle Embedded MongoDB.

---
*© 2026 CharityConnect Team*
