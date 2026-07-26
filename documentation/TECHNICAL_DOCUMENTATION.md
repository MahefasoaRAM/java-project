# Documentation technique — Projet ApiService

- **But**: Service REST pour la gestion d'une bibliothèque (utilisateurs, livres, emprunts, authentification JWT).
- **Langage / Framework**: Java 17+, Spring Boot, Gradle.
- **Scope**: Composants backend uniquement (src/main/java), configuration et diagrammes UML fournis dans `analyse/`.

1. Structure du projet

- Entrée de l'application : `src/main/java/com/bibliotheque/apiservice/ApiServiceApplication.java`.
- Configuration sécurité : `src/main/java/com/bibliotheque/apiservice/config/SecurityConfig.java` et `src/main/java/com/bibliotheque/apiservice/filter/JwtFilter.java`.
- Contrôleurs (endpoints) :
  - [ ] `AuthController` — authentification / login
  - [ ] `UserController` — gestion des utilisateurs
  - [ ] `LivreController` — gestion des livres
  - [ ] `EmpruntController` — gestion des emprunts
- Services : interfaces et implémentations dans `service/` et `service/impl/` (ex. `UserService`, `LivreService`, `EmpruntService`).
- Repositories : interfaces Spring Data JPA dans `repository/` (UserRepository, LivreRepository, EmpruntRepository).
- Entités JPA : `entity/` (User, Livre, Emprunt).
- DTOs / Requests / Mappers : transferts de données entre API et couche métier (`dto/`, `request/`, `mapper/`).
- Gestion des erreurs : classes d'exception et `GlobalExceptionHandler` dans `exception/`.

2. Principes d'architecture

- Architecture en couches : Controller → Service → Repository.
- Persistence : Spring Data JPA (entités dans `entity/`).
- Sécurité : JWT pour authentification et filtres pour protéger les endpoints.
- Mapping : utilisation de mappers (manuels ou via utilitaires) pour convertir Entité ↔ DTO.
- Validation : contrôles effectués au niveau des requêtes (`request/`) et dans les services.

3. Endpoints principaux (résumé)

- POST /auth/login — authentification, retourne token JWT.
- POST /users — créer utilisateur.
- GET /users/{id} — obtenir utilisateur.
- GET /livres — lister livres.
- POST /livres — créer livre.
- GET /emprunts — lister emprunts.
- POST /emprunts — créer emprunt.
  (Consulter les classes `*Controller` pour la liste complète et les paramètres.)

4. Modèle de données (essentiel)

- `User` : identifiant, email, mot de passe (haché), rôle (`RoleUser`).
- `Livre` : identifiant, titre, auteur, disponibilité.
- `Emprunt` : identifiant, référence vers `User` et `Livre`, date emprunt, statut (`EmpruntStatus`).
  Voir `src/main/java/com/bibliotheque/apiservice/entity/` pour les détails des champs.

5. Sécurité

- Flux : client s'authentifie → obtient JWT → en-tête `Authorization: Bearer <token>`→ `JwtFilter` valide et peuple le contexte.
- Règles : configuration des routes protégées et des rôles dans `SecurityConfig`.

6. Gestion des erreurs

- Erreurs spécifiques : `BadRequestException`, `UnauthorizedException`, `ResourceNotFoundException`, `DataConflictException`, `ServiceUnavailableException`, `ForbiddenException`, `InternalServerException`, `NoContentException`.
- `GlobalExceptionHandler` standardise le format des réponses d'erreur (voir `exception/ApiError.java`).

7. Diagrammes et analyses

- Diagrammes UML disponibles dans `analyse/` :
  - `analyse/uml-classes.mmd` — diagramme de classes.
  - `analyse/uml-sequence-emprunt.mmd` — séquence emprunt.
  - `analyse/uml-sequence-admin.mmd` — séquence administration.
  - `analyse/uml-cas-utilisation.mmd` — cas d'utilisation.
- Usage : ces fichiers sont au format Mermaid; ils peuvent être rendu par un visualiseur Mermaid (VSCode Mermaid Preview, GitLab/GitHub rendu, ou plugin Mermaid).

8. Build & exécution

- Prérequis : JDK 17+, Gradle wrapper fourni.
- Construire : `./gradlew build` (ou `gradlew.bat build` sur Windows).
- Exécuter : `./gradlew bootRun` ou lancer la classe `ApiServiceApplication` depuis l'IDE.
- Configuration : propriétés dans `src/main/resources/application.properties`.

9. Tests

- Emplacement des tests : `src/test/java/...`.
- Commande : `./gradlew test`.

10. Points d'attention / recommandations

- S'assurer que les mots de passe sont toujours hachés et non exposés dans les logs.
- Configurer les variables sensibles (JWT secret, datasource) via variables d'environnement ou vault.
- Compléter la documentation des API (ex. OpenAPI / Swagger) si utile pour client externe.

11. Références rapides (fichiers clés)

- `src/main/java/com/bibliotheque/apiservice/ApiServiceApplication.java`
- `src/main/java/com/bibliotheque/apiservice/config/SecurityConfig.java`
- `src/main/java/com/bibliotheque/apiservice/filter/JwtFilter.java`
- `src/main/java/com/bibliotheque/apiservice/controller/*Controller.java`
- `src/main/java/com/bibliotheque/apiservice/service/*`
- `analyse/` (diagrammes Mermaid)
