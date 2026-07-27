# Spécification fonctionnelle — Projet ApiService

Objectif

- Fournir la description fonctionnelle des fonctionnalités exposées par le service REST de gestion d'une bibliothèque : gestion des utilisateurs, des livres et des emprunts, avec contrôle d'accès par JWT.

Périmètre

- Scope: API backend uniquement (tous les endpoints définis dans `src/main/java/com/bibliotheque/apiservice/controller/`).
- Exclusions: UI, scripts d'administration externes et intégrations tierces (hors authentification via JWT incluse).

Acteurs

- Utilisateur authentifié (role `USER`) : consulter livres, créer emprunts, consulter ses emprunts.
- Administrateur (role `ADMIN`) : gestion complète des livres et utilisateurs, gérer emprunts.
- Système : authentification (JWT), base de données.

Cas d'utilisation clés (résumé)

1. Authentification

- But : obtenir un token JWT.
- Acteur : tout utilisateur (login).
- Entrée : email, mot de passe.
- Sortie : JWT valide (avec expiry) et données utilisateur minimales.
- Erreurs attendues : 401 sur identifiants invalides.

2. Gestion utilisateur

- Créer utilisateur (POST /users) — champs : `email`, `password`, `nom`, `role` (optionnel).
- Lire utilisateur (GET /users/{id}) — accessible selon rôle/ownership.
- Mettre à jour / Supprimer — réservés aux `ADMIN`.
- Validation : email format, mot de passe min length 8.
- Réponses : 201 créé, 200 OK, 400 validations, 403 non autorisé, 404 non trouvé.

3. Gestion des livres

- Lister livres (GET /livres) — filtres optionnels (titre, auteur, disponiblité).
- Créer livre (POST /livres) — `ADMIN` uniquement.
- Mettre à jour / Supprimer — `ADMIN`.
- Validation : titre requis, auteur requis.
- Réponses : 200/201/400/403/404.

4. Emprunts

- Créer emprunt (POST /emprunts) — utilisateur authentifié peut emprunter un livre disponible.
- Règles métiers :
  - Un livre doit être disponible (pas déjà emprunté ou statut réservé).
  - Un utilisateur ne peut pas dépasser un nombre maximal d'emprunts (ex. 5) — si non paramétré dans code, mentionner comme recommandation.
  - Lors de la création : statut `PENDING` ou `ACTIVE` selon implémentation; stocker date emprunt et date de retour prévue si applicable.
- Retour d'un livre (PUT /emprunts/{id}/return) — met à jour disponibilité du livre.
- Réponses : 201 créé, 409 conflit si non disponible, 400 validations, 403, 404.

Flux d'authentification (fonctionnel)

- POST /auth/login → vérification des identifiants → génération JWT contenant `userId` et `roles`.
- Toutes les requêtes protégées requièrent l'en-tête `Authorization: Bearer <token>`.

Contrats d'API (extraits)

1) POST /auth/login

- Requête JSON:
  {
  "email": "user@example.com",
  "password": "secret"
  }
- Réponse 200:
  {
  "token": "<jwt></jwt>",
  "user": { "id": 1, "email": "user@example.com", "role": "USER" }
  }
- Erreurs: 401 Unauthorized.

2) POST /users

- Requête JSON:
  {
  "email": "user@example.com",
  "password": "P@ssw0rd",
  "nom": "Dupont"
  }
- Réponse 201: création avec `Location` ou body contenant `id`.
- Erreurs: 400 validation, 409 si email déjà utilisé.

3) POST /livres

- Auth: `ADMIN`.
- Requête JSON:
  {
  "titre": "Le Petit Prince",
  "auteur": "Antoine de Saint-Exupéry",
  "isbn": "..."
  }
- Réponse 201: livre créé.

4) POST /emprunts

- Auth: `USER`.
- Requête JSON:
  {
  "userId": 1,
  "livreId": 2
  }
- Réponse 201: emprunt créé avec `status` et dates.
- Erreurs: 409 si livre non disponible, 400 validations.

Règles de validation et erreurs standard

- Réponse d'erreur standardisée via `ApiError` (voir `GlobalExceptionHandler`).
- Codes usuels : 400 (Bad Request), 401 (Unauthorized), 403 (Forbidden), 404 (Not Found), 409 (Conflict), 500 (Internal Server Error).

Critères d'acceptation (exemples)

- Un utilisateur valide peut s'authentifier et recevoir un JWT utilisable pour accéder aux endpoints protégés.
- Un `ADMIN` peut créer/mettre à jour/supprimer un livre et voir la liste complète.
- Un `USER` peut créer un emprunt pour un livre disponible; après création, le livre n'est plus disponible.
- Les erreurs de validation retournent 400 avec message détaillé.

Contraintes et points techniques

- Les mots de passe doivent être hachés. Ne jamais renvoyer le mot de passe en clair.
- JWT doit être signé et vérifier `exp`.
- Transactions : création d'un emprunt doit être atomique (mise à jour disponibilité livre + création emprunt).
