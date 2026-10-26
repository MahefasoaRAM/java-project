# Analyse – API REST de gestion d'une médiathèque numérique

---

## 1. Description du système

La médiathèque souhaite moderniser sa gestion manuelle des livres et des emprunts, source de plusieurs problèmes opérationnels :

| Problème identifié | Impact |
|---|---|
| Difficultés de suivi des emprunts | Impossibilité de savoir quels livres sont sortis et par qui |
| Pertes de livres non retournés | Aucune alerte sur les dépassements de date de retour |
| Absence de visibilité sur la disponibilité | Impossible de savoir rapidement si un ouvrage est disponible |
| Manque de traçabilité des utilisateurs | Les emprunteurs ne sont pas reliés de façon fiable aux emprunts |

Le système à développer est une **API REST Spring Boot** exposant des endpoints JSON pour la gestion du catalogue de livres, des utilisateurs et des emprunts. Elle sera consommée par une application web ou mobile (hors périmètre de ce projet).

---

## 2. Acteurs du système

### 2.1 Utilisateur
- Personne enregistrée dans le système.
- Peut **consulter** le catalogue de livres.
- Peut **emprunter** et **retourner** des livres.
- Peut être lié à plusieurs emprunts simultanément.

### 2.2 Administrateur
- Responsable de la gestion du catalogue.
- Peut **ajouter**, **modifier** et **supprimer** des livres.
- Peut **consulter** et **gérer** tous les emprunts et tous les utilisateurs.

---

## 3. Besoins fonctionnels

### 3.1 Gestion des livres

| Action | Description |
|---|---|
| Ajouter un livre | Créer une nouvelle entrée dans le catalogue |
| Modifier un livre | Mettre à jour les informations d'un livre existant |
| Supprimer un livre | Retirer un livre du catalogue (s'il n'a pas d'emprunt en cours) |
| Consulter la liste | Lister tous les livres disponibles |
| Consulter le détail | Afficher les informations complètes d'un livre |

**Attributs d'un Livre :**
- `id` – identifiant unique
- `titre` – titre de l'ouvrage
- `auteur` – nom de l'auteur
- `categorie` – genre / catégorie de l'ouvrage
- `disponible` – statut de disponibilité (`true` / `false`)

---

### 3.2 Gestion des utilisateurs

| Action | Description |
|---|---|
| Ajouter un utilisateur | Inscrire un nouvel utilisateur dans le système |
| Modifier un utilisateur | Mettre à jour les informations d'un utilisateur |
| Supprimer un utilisateur | Retirer un utilisateur du système |
| Consulter les utilisateurs | Lister tous les utilisateurs enregistrés |

**Attributs d'un Utilisateur :**
- `id` – identifiant unique
- `nom` – nom complet de l'utilisateur
- `email` – adresse email (unique)

---

### 3.3 Gestion des emprunts

| Action | Description |
|---|---|
| Emprunter un livre | Créer un emprunt liant un utilisateur à un livre disponible |
| Retourner un livre | Clôturer l'emprunt et rendre le livre disponible |
| Consulter les emprunts en cours | Lister tous les emprunts avec statut `EN_COURS` |

**Attributs d'un Emprunt :**
- `id` – identifiant unique
- `utilisateur` – référence à l'utilisateur emprunteur
- `livre` – référence au livre emprunté
- `dateEmprunt` – date de création de l'emprunt
- `dateRetourPrevue` – date limite de retour
- `statut` – `EN_COURS` ou `TERMINE`

---

## 4. Règles de gestion

| # | Règle |
|---|---|
| RG-01 | Un livre ne peut être emprunté que s'il est **disponible** |
| RG-02 | Un livre ne peut être emprunté que par **un seul utilisateur à la fois** |
| RG-03 | Un utilisateur peut emprunter **plusieurs livres simultanément** |
| RG-04 | Un emprunt retourné rend **automatiquement** le livre disponible |
| RG-05 | Un emprunt ne peut pas être créé pour un utilisateur ou un livre **inexistant** |
| RG-06 | Un livre **ne peut pas être supprimé** s'il a des emprunts en cours |

---

## 5. Diagrammes UML

### 5.1 Diagramme de cas d'utilisation

**Conventions UML appliquées :**
- Acteurs représentés par des **stickman** (bonshommes filaires) 🧍
- **Héritage d'acteurs** : `Administrateur` généralise `Utilisateur` — il possède toutes les actions de l'Utilisateur, avec des actions d'administration supplémentaires
- **`«include»`** : le cas « Se connecter » est obligatoirement exécuté avant tout cas protégé (emprunter, retourner, gérer…)
- **`«extend»`** : « Consulter le détail d'un livre » étend optionnellement « Consulter le catalogue »

> Fichier source : [`uml-cas-utilisation.mmd`](uml-cas-utilisation.mmd)

```mermaid
flowchart LR

    ADMIN(["🧍<br/><b>Administrateur</b>"])
    UTILISATEUR(["🧍<br/><b>Utilisateur</b>"])

    %% Héritage d'acteurs : Administrateur hérite d'Utilisateur
    ADMIN -.->|"hérite de (généralisation)"| UTILISATEUR

    subgraph SYSTEME["Système Médiathèque"]
        direction TB
        UC0(["Se connecter"])
        UC1(["Consulter le catalogue"])
        UC2(["Emprunter un livre"])
        UC3(["Retourner un livre"])
        UC4(["Ajouter un livre"])
        UC5(["Modifier un livre"])
        UC6(["Supprimer un livre"])
        UC7(["Consulter les emprunts"])
        UC8(["Gérer les utilisateurs"])

        %% «include» : Se connecter est obligatoire avant tout cas protégé
        UC2 -.->|"«include»"| UC0
        UC3 -.->|"«include»"| UC0
        UC4 -.->|"«include»"| UC0
        UC5 -.->|"«include»"| UC0
        UC6 -.->|"«include»"| UC0
        UC7 -.->|"«include»"| UC0
        UC8 -.->|"«include»"| UC0

        %% «extend» : Consulter le détail étend optionnellement le catalogue
        UCD(["Consulter le détail d'un livre"])
        UCD -.->|"«extend»"| UC1
    end

    UTILISATEUR --> UC1
    UTILISATEUR --> UC2
    UTILISATEUR --> UC3

    ADMIN --> UC4
    ADMIN --> UC5
    ADMIN --> UC6
    ADMIN --> UC7
    ADMIN --> UC8
```

**Scénario type illustrant `include` :** avant d'emprunter un livre (UC2), l'utilisateur doit d'abord passer par « Se connecter » (UC0) — la flèche `«include»` de UC2 vers UC0 matérialise cette obligation.

---

### 5.2 Diagramme de classes (modèle entité-relation)

Le diagramme se limite aux **classes principales du modèle de données** (entités persistées) — les services, contrôleurs et DTO n'en font pas partie :

| Classe | Rôle |
|---|---|
| `Utilisateur` | Personne inscrite, emprunte des livres |
| `Livre` | Ouvrage du catalogue, disponible ou non |
| `Emprunt` | Association dynamique entre un utilisateur et un livre |

> Fichier source : [`uml-classes.mmd`](uml-classes.mmd)

```mermaid
classDiagram
    direction LR

    class Utilisateur {
        +Long id
        +String nom
        +String email
        +String password
        +RoleUser role
    }

    class Livre {
        +Long id
        +String titre
        +String auteur
        +String categorie
        +boolean disponible
    }

    class Emprunt {
        +Long id
        +LocalDate dateEmprunt
        +LocalDate dateRetourPrevue
        +EmpruntStatus statut
    }

    class RoleUser {
        <<enumeration>>
        ADMIN
        USER
    }

    class EmpruntStatus {
        <<enumeration>>
        EN_COURS
        TERMINE
    }

    %% ── Associations (cardinalités UML) ──
    Utilisateur "1" --> "0..*" Emprunt : effectue
    Livre "1" --> "0..*" Emprunt : concerné par

    %% ── Énumérations liées aux entités ──
    Utilisateur ..> RoleUser : utilise
    Emprunt ..> EmpruntStatus : utilise
```

**Lecture des cardinalités :** un `Utilisateur` effectue `0..*` emprunts ; un `Livre` est concerné par `0..*` emprunts ; chaque `Emprunt` relie exactement **1** utilisateur et **1** livre (classes d'association du modèle entité-relation).

---

### 5.3 Diagramme de séquence – Emprunter un livre

Cas d'utilisation étudié seul. L'utilisateur est identifié via le JWT (incluant « Se connecter »).

> Fichier source : [`uml-sequence-emprunt.mmd`](uml-sequence-emprunt.mmd)

```mermaid
sequenceDiagram
    actor Utilisateur
    participant API as EmpruntController
    participant SVC as EmpruntService
    participant UREPO as UserRepository
    participant LREPO as LivreRepository
    participant EREPO as EmpruntRepository

    Utilisateur->>API: 1. POST /api/emprunts { livreId, dateRetourPrevue }
    activate API
    API->>SVC: 2. emprunterLivre(empruntRequest)
    activate SVC

    SVC->>UREPO: 3. findById(utilisateurId)  [utilisateur connecté via JWT]
    activate UREPO
    UREPO-->>SVC: 4. Utilisateur | ResourceNotFoundException
    deactivate UREPO

    SVC->>LREPO: 5. findById(livreId)
    activate LREPO
    LREPO-->>SVC: 6. Livre | ResourceNotFoundException
    deactivate LREPO

    alt Utilisateur ou livre introuvable
        SVC-->>API: 7a. ResourceNotFoundException
        API-->>Utilisateur: 8a. 404 Not Found { erreur }
    else Livre indisponible (RG-01/RG-02)
        SVC-->>API: 7b. DataConflictException
        API-->>Utilisateur: 8b. 409 Conflict { message: "Livre indisponible" }
    else Livre disponible
        SVC->>LREPO: 7c. save(livre.setDisponible(false))
        activate LREPO
        LREPO-->>SVC: 8c. LivreDTO (disponible = false)
        deactivate LREPO
        SVC->>EREPO: 9c. save(newEmprunt)
        activate EREPO
        EREPO-->>SVC: 10c. EmpruntDTO (statut = EN_COURS)
        deactivate EREPO
        SVC-->>API: 11c. EmpruntDTO
        deactivate SVC
        API-->>Utilisateur: 12c. 201 Created { empruntDTO }
        deactivate API
    end
```

---

### 5.4 Diagramme de séquence – Ajouter un livre

Un **seul cas d'utilisation** par diagramme : ici « Ajouter un livre » (Administrateur). Les autres cas d'administration (modifier, supprimer, gérer les utilisateurs) font l'objet de diagrammes séparés.

> Fichier source : [`uml-sequence-admin.mmd`](uml-sequence-admin.mmd)

```mermaid
sequenceDiagram
    actor Admin
    participant API as LivreController
    participant SVC as LivreService
    participant REPO as LivreRepository

    Admin->>API: 1. POST /api/livres { titre, auteur, categorie }
    activate API
    API->>SVC: 2. ajouterLivre(livreRequest)
    activate SVC
    SVC->>SVC: 3. Vérifier unicité du titre
    SVC->>REPO: 4. save(newLivre)
    activate REPO
    REPO-->>SVC: 5. livreEnregistré (id généré)
    deactivate REPO
    SVC-->>API: 6. LivreDTO
    deactivate SVC
    API-->>Admin: 7. 201 Created { livreDTO }
    deactivate API
```

### 5.4 bis – Supprimer un livre (avec scénarios alternatifs)

Illustration de la règle **RG-06** : un livre avec emprunts en cours ne peut pas être supprimé.

> Fichier source : [`uml-sequence-supprimer-livre.mmd`](uml-sequence-supprimer-livre.mmd)

```mermaid
sequenceDiagram
    actor Admin
    participant API as LivreController
    participant SVC as LivreService
    participant LREPO as LivreRepository
    participant EREPO as EmpruntRepository

    Admin->>API: 1. DELETE /api/livres/{id}
    activate API
    API->>SVC: 2. supprimerLivre(id)
    activate SVC

    SVC->>LREPO: 3. findById(id)
    activate LREPO
    LREPO-->>SVC: 4. Livre | NotFoundException
    deactivate LREPO

    alt Livre introuvable
        SVC-->>API: 5a. ResourceNotFoundException
        API-->>Admin: 6a. 404 Not Found { erreur }
    else Livre trouvé
        SVC->>EREPO: 5b. existsByLivreIdAndStatut(id, EN_COURS)
        activate EREPO
        EREPO-->>SVC: 6b. true | false
        deactivate EREPO

        alt Emprunt en cours sur ce livre
            SVC-->>API: 7b. DataConflictException (RG-06)
            API-->>Admin: 8b. 409 Conflict { message: "Livre avec emprunts en cours" }
        else Aucun emprunt en cours
            SVC->>LREPO: 7c. deleteById(id)
            activate LREPO
            LREPO-->>SVC: 8c. livre supprimé
            deactivate LREPO
            SVC-->>API: 9c. void
            API-->>Admin: 10c. 204 No Content
        end
    end
    deactivate SVC
    deactivate API
```

---

### 5.5 Diagramme de séquence – Retourner un livre

> Fichier source : [`uml-sequence-retour.mmd`](uml-sequence-retour.mmd)

```mermaid
sequenceDiagram
    actor Utilisateur
    participant API as EmpruntController
    participant SVC as EmpruntService
    participant EREPO as EmpruntRepository
    participant LREPO as LivreRepository

    Utilisateur->>API: 1. PUT /api/emprunts/{id}/retour
    activate API
    API->>SVC: 2. retournerLivre(empruntId)
    activate SVC

    SVC->>EREPO: 3. findById(empruntId)
    activate EREPO
    EREPO-->>SVC: 4. Emprunt | NotFoundException
    deactivate EREPO

    alt Emprunt déjà terminé
        SVC-->>API: 5a. DataConflictException
        API-->>Utilisateur: 6a. 409 Conflict { message: "Emprunt déjà terminé" }
    else Emprunt en cours
        SVC->>SVC: 5b. emprunt.setStatut(TERMINE)
        SVC->>EREPO: 6b. save(emprunt)
        activate EREPO
        EREPO-->>SVC: 7b. EmpruntDTO (statut = TERMINE)
        deactivate EREPO
        SVC->>LREPO: 8b. save(livre.setDisponible(true))  [RG-04]
        activate LREPO
        LREPO-->>SVC: 9b. LivreDTO (disponible = true)
        deactivate LREPO
        SVC-->>API: 10b. EmpruntDTO
        deactivate SVC
        API-->>Utilisateur: 11b. 200 OK { empruntDTO }
        deactivate API
    end
```

---

## 6. Architecture technique

L'application suit une **architecture en couches** :

```
controller      ← Points d'entrée REST (HTTP)
service         ← Interfaces des services métier
service.impl    ← Implémentations des services
repository      ← Accès base de données (JPA)
entity          ← Entités JPA (Livre, Utilisateur, Emprunt)
dto             ← Objets de transfert de données
exception       ← Exceptions métier personnalisées
config          ← Configuration Spring (Security, Swagger…)
```

**Stack technique :**
- Java 17
- Spring Boot 3.4.2
- Spring Data JPA
- Spring Security + JWT
- MySQL
- Lombok
- JUnit 5 + Mockito (tests)
- Swagger / OpenAPI (documentation)
