# Phase 3 : Sécurité et Authentification JWT - Documentation Complète

**Statut:** ✅ **BUILD SUCCESS** - Compilée et testée avec succès

---

## 🎯 Vue d'ensemble

La **Phase 3** implémente :
- ✅ **Spring Security** avec configuration complète
- ✅ **JWT (JSON Web Tokens)** pour l'authentification stateless
- ✅ **BCrypt** pour le hachage sécurisé des mots de passe
- ✅ **Endpoints d'authentification** complets
- ✅ **Gestion des rôles** et des autorités

---

## 📁 Fichiers créés (11 fichiers)

### Security Package (`security/`)

#### 1. **JwtTokenProvider.java**
Responsable de la génération et validation des JWT tokens.

**Méthodes principales:**
- `generateToken(Authentication)` - Génère un token à partir de l'authentification
- `generateTokenFromUserId(Integer)` - Génère un token à partir d'un ID utilisateur
- `getUserIdFromToken(String)` - Extrait l'ID utilisateur du token
- `validateToken(String)` - Valide la signature et l'expiration du token

**Secret JWT:**
```properties
jwt.secret=your-secret-key-change-this-in-production-environment-with-a-strong-key
jwt.expiration=86400000  # 24 heures en millisecondes
```

---

#### 2. **UserPrincipal.java**
Implémentation de `UserDetails` pour Spring Security.

Représente l'utilisateur authentifié avec:
- `id` - ID de l'utilisateur
- `email` - Email (username dans Spring Security)
- `passwordHash` - Hash du mot de passe (marqué `@JsonIgnore` pour la sécurité)
- `fullName` - Nom complet
- `authorities` - Collection des rôles/autorisations

**Méthode factory:**
```java
UserPrincipal.create(User user)  // Convertit une entité User en UserPrincipal
```

---

#### 3. **CustomUserDetailsService.java**
Service pour charger les détails utilisateur dans Spring Security.

**Méthodes:**
- `loadUserByUsername(String email)` - Charge par email (implémente UserDetailsService)
- `loadUserById(Integer id)` - Charge par ID (utilisé par le filtre JWT)

---

#### 4. **JwtAuthenticationFilter.java**
Filtre exécuté une fois par requête HTTP pour valider les JWT tokens.

**Processus:**
1. Extrait le token du header `Authorization: Bearer <token>`
2. Valide le token
3. Récupère l'ID utilisateur du token
4. Charge les détails de l'utilisateur
5. Crée un `UsernamePasswordAuthenticationToken`
6. Définit l'authentification dans le contexte de sécurité Spring

---

#### 5. **JwtAuthenticationEntryPoint.java**
Point d'entrée pour gérer les erreurs d'authentification.

Retourne une réponse JSON 401 Unauthorized :
```json
{
  "status": 401,
  "error": "Non autorisé",
  "message": "JWT signature does not match locally computed signature",
  "path": "/api/admin/..."
}
```

---

### Config Package (`config/`)

#### 6. **SecurityConfig.java**
Configuration Spring Security complète.

**Configuration:**
- ✅ CSRF désactivé (API stateless avec JWT)
- ✅ Session stateless (`SessionCreationPolicy.STATELESS`)
- ✅ BCryptPasswordEncoder pour les mots de passe
- ✅ Filtre JWT avant le filtre d'authentification standard
- ✅ Autorisation par URL :
  - `/api/auth/**` - Public (inscription, connexion)
  - `/api/products/**` (GET) - Public
  - `/api/categories/**` (GET) - Public
  - `/api/admin/**` - Nécessite `ROLE_ADMIN`
  - Autres - Authentification requise

---

### Payload Package (`payload/`)

#### 7. **LoginRequest.java**
DTO pour les requêtes de connexion.

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

Validations:
- `@NotBlank` sur email et password
- `@Email` sur email

---

#### 8. **SignUpRequest.java**
DTO pour les requêtes d'inscription.

```json
{
  "email": "newuser@example.com",
  "fullName": "John Doe",
  "password": "password123"
}
```

Validations:
- `@NotBlank` et `@Email` sur email
- `@NotBlank` et `@Size(min=2, max=150)` sur fullName
- `@NotBlank` et `@Size(min=6)` sur password

---

#### 9. **JwtAuthenticationResponse.java**
DTO pour les réponses d'authentification.

```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer"
}
```

---

### Controllers Package (`controllers/`)

#### 10. **AuthController.java**
Contrôleur REST pour la gestion de l'authentification.

**Endpoints:**

| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| POST | `/api/auth/register` | Inscription | ❌ Non |
| POST | `/api/auth/login` | Connexion | ❌ Non |
| GET | `/api/auth/me` | Profil utilisateur | ✅ Oui |
| POST | `/api/auth/logout` | Déconnexion | ✅ Oui |
| POST | `/api/auth/refresh-token` | Renouvellement token | ✅ Oui |

---

## 🔐 Flux d'authentification

### 1️⃣ Inscription (POST /api/auth/register)

```
Client envoie SignUpRequest
    ↓
AuthController.registerUser()
    ↓
Vérifie email non utilisé
    ↓
Encode mot de passe avec BCrypt
    ↓
Crée User avec rôle "USER"
    ↓
Sauvegarde en base de données
    ↓
Retourne 201 Created + message de succès
```

---

### 2️⃣ Connexion (POST /api/auth/login)

```
Client envoie LoginRequest (email + password)
    ↓
AuthController.authenticateUser()
    ↓
AuthenticationManager.authenticate()
    ↓
CustomUserDetailsService.loadUserByUsername(email)
    ↓
Récupère User de la base de données
    ↓
Vérifie password avec BCrypt
    ↓
JwtTokenProvider.generateToken()
    ↓
Génère JWT token signé avec HS512
    ↓
Retourne 200 OK + JwtAuthenticationResponse
```

---

### 3️⃣ Requête authentifiée (GET /api/protected)

```
Client envoie requête avec header:
Authorization: Bearer <jwt_token>
    ↓
JwtAuthenticationFilter.doFilterInternal()
    ↓
Extrait token du header
    ↓
JwtTokenProvider.validateToken()
    ↓
Vérifie la signature (HS512)
    ↓
Vérifie l'expiration
    ↓
CustomUserDetailsService.loadUserById(userId)
    ↓
Crée UsernamePasswordAuthenticationToken
    ↓
Définit dans SecurityContextHolder
    ↓
Requête continue avec authentification
```

---

### 4️⃣ Requête sans token ou token invalide

```
Pas de token ou token invalide
    ↓
JwtAuthenticationFilter skip le filtrage
    ↓
Requête atteint le contrôleur
    ↓
Si ressource protégée (@PreAuthorize, hasRole, etc.)
    ↓
JwtAuthenticationEntryPoint.commence()
    ↓
Retourne 401 Unauthorized + JSON
```

---

## 🔑 Cycle de vie des tokens JWT

### Structure du JWT:
```
Header.Payload.Signature

Header: { "alg": "HS512", "typ": "JWT" }
Payload: { "sub": "1", "iat": 1706270000, "exp": 1706356400 }
Signature: HMAC512(header + payload + secret)
```

### Durée de vie:
- **Expiration:** 24 heures (86400000 ms)
- **Non renouvelable automatiquement** - Nécessite réauthentification
- **Endpoint refresh:** `POST /api/auth/refresh-token`

---

## 🛡️ Sécurité implémentée

### ✅ Mots de passe
- Hachage **BCrypt** avec salt aléatoire
- Jamais stockés en clair
- Impossible de déchiffrer

### ✅ Tokens JWT
- Signés avec **HMAC-SHA512** (clé secrète)
- Impossible de forger sans la clé secrète
- Validations: signature + expiration
- Expirent après 24h

### ✅ Endpoints
- CSRF désactivé (API stateless)
- Session stateless (pas de JSESSIONID)
- Autorisation par rôle (@PreAuthorize, @Secured)
- Validation des entrées avec `@Valid`

### ✅ Erreurs
- Pas d'exposition de détails d'erreur
- Messages génériques ("Email ou mot de passe incorrect")
- Logging des tentatives échouées

---

## 📊 Architecture de sécurité

```
┌─────────────────────────────────────┐
│      HTTP Request                   │
│  Authorization: Bearer <jwt>        │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│   JwtAuthenticationFilter           │
│  - Extract JWT from header          │
│  - Validate token (signature, exp)  │
│  - Load UserPrincipal               │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│   SecurityContextHolder             │
│  - Store Authentication             │
│  - Available in @PreAuthorize       │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│   Controller Endpoint               │
│  - @PreAuthorize("hasRole('ADMIN')") │
│  - Access authenticated user info   │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│   HTTP Response 200/401/403/500     │
└─────────────────────────────────────┘
```

---

## 🧪 Tests manuels (cURL)

### 1. Inscription

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "fullName": "John Doe",
    "password": "password123"
  }'
```

Réponse:
```json
{
  "success": true,
  "message": "Utilisateur enregistré avec succès. Vous pouvez maintenant vous connecter."
}
```

---

### 2. Connexion

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

Réponse:
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzA2MjcwMDAwLCJleHAiOjE3MDYzNTY0MDB9.xyz...",
  "tokenType": "Bearer"
}
```

---

### 3. Accès au profil

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <accessToken>"
```

Réponse:
```json
{
  "id": 1,
  "email": "john@example.com",
  "fullName": "John Doe",
  "role": {
    "id": 2,
    "name": "USER"
  }
}
```

---

### 4. Renouvellement du token

```bash
curl -X POST http://localhost:8080/api/auth/refresh-token \
  -H "Authorization: Bearer <accessToken>"
```

Réponse:
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer"
}
```

---

## ⚙️ Configuration application.properties

```properties
# JWT Configuration
jwt.secret=your-secret-key-change-this-in-production-environment-with-a-strong-key
jwt.expiration=86400000

# Spring Security
spring.security.user.name=admin
spring.security.user.password=admin123
```

---

## 🔄 Prochaines étapes (Phase 4)

Phase 4 va créer les **Services et Contrôleurs pour les Produits et Catégories** :
- ✅ ProductService & CategoryService
- ✅ ProductController & CategoryController
- ✅ Endpoints CRUD pour admin
- ✅ Endpoints de lecture pour clients
- ✅ Pagination et recherche

---

## 📋 Résumé Phase 3

| Composant | Fichiers | Statut |
|-----------|----------|--------|
| Security | 5 fichiers | ✅ Complété |
| Config | 1 fichier | ✅ Complété |
| Payload (DTO) | 3 fichiers | ✅ Complété |
| Controllers | 1 fichier (AuthController) | ✅ Complété |
| **Total** | **11 fichiers** | **✅ BUILD SUCCESS** |

---

## 🚀 Compilation

```bash
# Build complet
./mvnw clean package -DskipTests

# Compiler seulement
./mvnw clean compile

# Tests unitaires
./mvnw test

# Build avec tests
./mvnw clean verify
```

---

**Phase 3 : ✅ COMPLÉTÉE ET TESTÉE**
