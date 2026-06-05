# CharityConnect 🌟

CharityConnect est une plateforme web moderne conçue pour combler le fossé entre les donateurs et les organisations caritatives. Elle permet aux utilisateurs de découvrir des causes significatives, de participer à des événements et de faire des dons pour soutenir diverses initiatives.

## 📝 À propos
Plateforme de mise en relation entre donateurs et associations caritatives intégrant un système de recommandations intelligentes, un suivi d'impact en temps réel et une gestion simplifiée des campagnes de bienfaisance.

## 🚀 Fonctionnalités Clés

### 👤 Pour les Donateurs
- **Recommandations Personnalisées** : Algorithme suggérant des actions basées sur vos intérêts (Éducation, Santé, Environnement, etc.) et votre historique.
- **Participation en un Clic** : Rejoignez des événements caritatifs instantanément.
- **Suivi des Dons & Impact** : Visualisez l'évolution globale des collectes en temps réel.
- **Tableau de Bord Dédié** : Gérez vos préférences, consultez vos participations et vos contributions financières.
- **Parcours d'Onboarding** : Expérience personnalisée dès la première connexion pour définir vos centres d'intérêt.

### 🏢 Pour les Organisations
- **Gestion Complète des Actions** : Création, édition et suivi de campagnes caritatives.
- **Gestion des Partisans** : Accès aux listes détaillées des participants et des donateurs.
- **Validation de Profil** : Système d'approbation administrative pour garantir la légitimité des associations.

### 🛡️ Pour les Administrateurs
- **Modération des Organisations** : Revue et validation des inscriptions des nouveaux partenaires.
- **Supervision Globale** : Monitoring de l'activité, des utilisateurs et des flux financiers du site.

## 🛠 Stack Technique

- **Backend** : Java 17+, Spring Boot 3.5.x
- **Base de données** : MongoDB (Spring Data MongoDB)
- **Sécurité** : Spring Security (RBAC, protection contre le cache, logout sécurisé)
- **Frontend** : Thymeleaf, HTML5, CSS3, JavaScript (Vanilla)
- **Localisation** : Internationalisation (i18n) - Français et Anglais
- **Email** : Envoi de confirmations via MimeMessage avec branding CharityConnect

## 📦 Installation et Configuration

### Prérequis
- **Java 17** ou version supérieure

### Démarrage Rapide

1. **Cloner le projet** :
   ```bash
   git clone https://github.com/azizyy1/CharityConnect.git
   cd CharityConnect
   ```

2. **Base de données** :
   Le projet utilise un **MongoDB Embarqué**. Aucune installation de base de données n'est requise localement ; elle démarre automatiquement avec l'application.

3. **Lancer le serveur** :
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Accès local** :
   Rendez-vous sur **`http://localhost:8081`**.

## 🔐 Comptes de Test

| Rôle | Email | Mot de passe |
|------|-------|----------|
| **Administrateur** | `admin@charityconnect.com` | `Admin@123` |
| **Organisation** | `org@charityconnect.com` | `Org@12345` |
| **Utilisateur** | `user@charityconnect.com` | `User@12345` |

## 🌍 Localisation

L'application détecte automatiquement la langue ou permet un changement manuel via le pied de page. Les fichiers de traduction sont situés dans `src/main/resources/messages`.

## 🛡 Sécurité & Performance

- **Protection History-Back** : Les tableaux de bord forcent la vérification de session pour empêcher le retour en arrière après déconnexion.
- **Live Counters** : Les statistiques de la page d'accueil (dons, heures) s'animent en temps réel pour dynamiser l'interface.
- **Embedded DB** : Facilitation du développement collaboratif sans configuration complexe.

---
## 📄 Documentation & Démonstration

* 📘 **Rapport du Projet**
* 📊 **Présentation PowerPoint**

🔗 Google Drive :
https://drive.google.com/drive/folders/1fjCURipLtQkeLj2yt5TVWaDoccq2TFYc?usp=sharing

## 👨‍💻 Équipe

Projet réalisé par :

* **Hajar Azizi**

Dans le cadre du module **JEE — EMSI 2025/2026**.

