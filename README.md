# API Service - Médiathèque numérique

API REST Spring Boot pour gérer une médiathèque numérique.  
Le projet permet de gérer les livres, les utilisateurs et les emprunts, avec authentification JWT et contrôle d’accès par rôle.

## Vue d’ensemble

L’application expose une API JSON pour :

- créer et authentifier des utilisateurs ;
- gérer le catalogue de livres ;
- créer et clôturer des emprunts ;
- consulter les données selon les droits de l’utilisateur connecté.

Le backend suit une architecture classique en couches :

- `controller` : expose les endpoints HTTP ;
- `service` : contient la logique métier ;
- `repository` : accès à la base de données ;
- `entity` : modèle persistant ;
- `dto` / `request` : objets d’échange pour l’API ;
- `config` / `filter` : sécurité JWT et configuration Spring Security.

## Technologies

- Java 17
- Spring Boot 3.4.2
- Spring Web
- Spring Security
- Spring Data JPA
- JWT
- MySQL
- Lombok

## Fonctionnalités

### Authentification

- inscription d’un utilisateur ;
- connexion avec email et mot de passe ;
- génération d’un token JWT ;
- protection des routes via Spring Security.

### Gestion des livres

- ajouter un livre ;
- modifier un livre ;
- supprimer un livre ;
- lister les livres ;
- consulter un livre par id, titre, auteur ou catégorie.

### Gestion des utilisateurs

- créer un utilisateur ;
- modifier un utilisateur ;
- supprimer un utilisateur ;
- lister les utilisateurs ;
- filtrer par rôle ou email.

### Gestion des emprunts

- créer un emprunt pour un livre disponible ;
- retourner un livre ;
- lister tous les emprunts ;
- lister les emprunts en cours.

## Règles métier principales

- un livre ne peut être emprunté que s’il est disponible ;
- un livre ne peut avoir qu’un emprunt actif à la fois ;
- un utilisateur peut avoir plusieurs emprunts ;
- un retour rend automatiquement le livre disponible ;
- un livre ne peut pas être supprimé s’il a encore des emprunts en cours.

## Sécurité

L’API utilise des endpoints publics pour l’authentification :

- `POST /api/auth/register`
- `POST /api/auth/login`

Les autres routes sont protégées par JWT.  
Les opérations de création, modification et suppression de livres sont réservées au rôle `ADMIN`.

## Endpoints principaux

### Authentification

- `POST /api/auth/register`
- `POST /api/auth/login`

### Livres

- `POST /api/livres`
- `GET /api/livres`
- `GET /api/livres/{id}`
- `GET /api/livres/titre/{titre}`
- `GET /api/livres/auteur/{auteur}`
- `GET /api/livres/categorie/{categorie}`
- `PUT /api/livres/{id}`
- `DELETE /api/livres/{id}`

### Utilisateurs

- `POST /api/users`
- `GET /api/users`
- `GET /api/users/{id}`
- `GET /api/users/email/{email}`
- `GET /api/users/role/{roleUser}`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`

### Emprunts

- `POST /api/emprunts`
- `GET /api/emprunts`
- `GET /api/emprunts/en-cours`
- `PUT /api/emprunts/{id}/retour`

## Formats de données

### Inscription utilisateur

```json
{
  "nom": "Jean Dupont",
  "email": "jean@example.com",
  "password": "secret123",
  "role": "USER"
}
```

### Connexion

```json
{
  "email": "jean@example.com",
  "password": "secret123"
}
```

### Livre

```json
{
  "titre": "Le Petit Prince",
  "auteur": "Antoine de Saint-Exupéry",
  "categorie": "Littérature"
}
```

### Emprunt

```json
{
  "livreId": 1,
  "dateRetourPrevue": "2026-07-30"
}
```

## Configuration locale

Le projet est configuré pour MySQL dans `src/main/resources/application.properties`.

Exemple de paramètres utilisés :

- base de données : `jdbc:mysql://localhost:3306/java_project`
- utilisateur : `root`
- mot de passe : `password`
- stratégie Hibernate : `update`

Le secret JWT et la durée de validité du token sont également définis dans ce fichier.

## Lancer le projet

### Prérequis

- Java 17
- MySQL démarré localement
- base de données `java_project` créée

### Commandes

```bash
./gradlew bootRun
```

Pour exécuter les tests :

```bash
./gradlew test
```

## Organisation du code

- `src/main/java/com/bibliotheque/apiservice/controller` : routes HTTP ;
- `src/main/java/com/bibliotheque/apiservice/service` : logique métier ;
- `src/main/java/com/bibliotheque/apiservice/repository` : accès base de données ;
- `src/main/java/com/bibliotheque/apiservice/entity` : entités JPA ;
- `src/main/java/com/bibliotheque/apiservice/dto` : réponses API ;
- `src/main/java/com/bibliotheque/apiservice/request` : requêtes d’entrée ;
- `src/main/java/com/bibliotheque/apiservice/config` : sécurité ;
- `analyse/` : notes d’analyse et diagrammes UML.

## Remarque

Le projet est pensé comme backend pour une interface web ou mobile séparée.  
Le front-end n’est pas inclus dans ce dépôt.
