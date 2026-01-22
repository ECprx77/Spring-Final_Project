# Phase 2 : Entités JPA - Guide Concis

## 📌 Vue d'ensemble

La **Phase 2** crée le modèle de données JPA qui mappe la base de données PostgreSQL en classes Java.

---

## 🏗️ Architecture

### Entités créées (7 fichiers)

| Entité | Rôle | Champs clés |
|--------|------|-----------|
| **Role** | Rôles utilisateur | id, name |
| **User** | Utilisateurs | id, email, passwordHash, fullName, role_id |
| **Category** | Catégories produits | id, name, description |
| **Product** | Produits | id, name, price, status, category_id |
| **Order** | Commandes | id, user_id, orderDate, status, total |
| **OrderLine** | Lignes commande | id, order_id, product_id, quantity, unitPrice |
| **AuditLog** | Logs d'audit | id, action, entityType, userId, createdAt |

---

## 🔗 Relations entre entités

```
Role ◄─── User ◄─── Order ◄─── OrderLine ───► Product ───► Category
(1)    (Many)   (1)     (Many)    (Many)        (Many)        (1)
```

**Relations configurées:**
- `@ManyToOne` : Chaque User a UN Role, chaque Order a UN User
- `@OneToMany` : Chaque Role a PLUSIEURS Users, chaque Order a PLUSIEURS OrderLines
- `@JoinColumn` : Clés étrangères explicites
- `cascade = CascadeType.ALL` : Suppression en cascade des OrderLines

---

## ✅ Validations & Annotations

### Annotations JPA utilisées
- `@Entity` : Marque une classe comme entité persistante
- `@Table(name = "...")` : Mappe à une table spécifique
- `@Id @GeneratedValue` : Clé primaire auto-incrémentée
- `@Column` : Configure les colonnes (unique, length, etc.)
- `@ManyToOne / @OneToMany` : Définit les relations
- `@JoinColumn` : Spécifie la clé étrangère
- `@Enumerated(EnumType.STRING)` : Stocke les enums en texte

### Validations de données
- `@NotBlank` : Champ obligatoire (non vide)
- `@Email` : Valide un email
- `@Positive` : Valeur > 0 (prix, quantité)
- `@PositiveOrZero` : Valeur >= 0

---

## 📦 Repositories (7 fichiers)

Chaque entité a un **Repository** qui étend `JpaRepository<Entity, ID>`.

```java
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email); // Requête personnalisée
}
```

**Opérations disponibles automatiquement:**
- `save()` - Créer/Modifier
- `findById()` - Récupérer par ID
- `findAll()` - Récupérer tous
- `deleteById()` - Supprimer
- `exists()` - Vérifier l'existence

---

## 🛠️ DTOs (6 fichiers)

Les **DTOs** (Data Transfer Objects) sont des classes intermédiaires pour l'API.

**Pourquoi ?**
- Évite d'exposer directement les entités
- Sépare l'API de la logique métier
- Permet de valider les données à l'entrée
- Contrôle ce qui est retourné au client

```java
// DTO pour l'API (sécurisé)
public class UserDTO {
    private Integer id;
    @Email
    private String email;
    private String fullName;
    private String roleName;
    // PAS de passwordHash !
}

// Entité interne (complet)
@Entity
public class User {
    private String passwordHash; // Sécurisé
}
```

---

## ⚙️ Exceptions personnalisées (2 fichiers)

Pour une gestion d'erreurs cohérente :
- `ResourceNotFoundException` : Entité non trouvée (404)
- `UnauthorizedException` : Accès refusé (401)

---

## 📚 Dépendances ajoutées

```xml
<!-- Lombock : Réduit le boilerplate -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>

<!-- Validation Jakarta -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- JWT pour authentification futur -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
</dependency>
```

---

## 🔄 Flux complet

```
Frontend envoie JSON
    ↓
Spring désérialise → UserDTO
    ↓
Contrôleur reçoit UserDTO (validé)
    ↓
Service convertit UserDTO → User Entity
    ↓
Repository sauvegarde dans H2
    ↓
Service convertit User Entity → UserDTO
    ↓
Spring sérialise → JSON
    ↓
Frontend reçoit JSON
```

---

## 📁 Structure finale

```
src/main/java/com/TZ/TechZone/
├── entities/           (7 fichiers)
│   ├── Role.java
│   ├── User.java
│   ├── Category.java
│   ├── Product.java
│   ├── Order.java
│   ├── OrderLine.java
│   └── AuditLog.java
├── repositories/       (7 fichiers)
│   ├── RoleRepository.java
│   ├── UserRepository.java
│   ├── ...
├── dto/               (6 fichiers)
│   ├── UserDTO.java
│   ├── ProductDTO.java
│   ├── ...
└── exceptions/        (2 fichiers)
    ├── ResourceNotFoundException.java
    └── UnauthorizedException.java
```

---

## ✨ Résumé

**22 fichiers créés** pour la Phase 2 :
- 7 Entités JPA + relations
- 7 Repositories JPA
- 6 DTOs (validations)
- 2 Exceptions personnalisées
- Dépendances Maven (Lombok, Validation, JWT)

**Statut:** ✅ BUILD SUCCESS - Code compilé sans erreurs

**Prochaine étape:** Phase 3 - Spring Security & Authentification
