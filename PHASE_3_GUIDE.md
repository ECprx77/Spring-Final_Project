# Phase 3 - Guide d'utilisation API d'authentification

## 🚀 Démarrage rapide

### Démarrer l'application

```bash
cd Spring-Final_Project/TechZone
./mvnw spring-boot:run
```

Application sur: `http://localhost:8080`

---

## 📝 Endpoints d'authentification

### 1. **Inscription** - `POST /api/auth/register`

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "fullName": "John Doe",
    "password": "SecurePass123"
  }'
```

**Réponse (201):**
```json
{
  "success": true,
  "message": "Utilisateur enregistré avec succès. Vous pouvez maintenant vous connecter."
}
```

---

### 2. **Connexion** - `POST /api/auth/login`

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePass123"
  }'
```

**Réponse (200):**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzA2MjcwMDAwLCJleHAiOjE3MDYzNTY0MDB9...",
  "tokenType": "Bearer"
}
```

---

### 3. **Profil utilisateur** - `GET /api/auth/me`

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <accessToken>"
```

**Réponse (200):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "fullName": "John Doe",
  "role": {
    "id": 2,
    "name": "USER"
  }
}
```

---

### 4. **Renouvellement du token** - `POST /api/auth/refresh-token`

```bash
curl -X POST http://localhost:8080/api/auth/refresh-token \
  -H "Authorization: Bearer <accessToken>"
```

**Réponse (200):**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer"
}
```

---

### 5. **Déconnexion** - `POST /api/auth/logout`

```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer <accessToken>"
```

**Réponse (200):**
```json
{
  "success": true,
  "message": "Vous avez été déconnecté avec succès"
}
```

---

## 🔐 Utilisation du JWT Token

Le token JWT doit être envoyé dans le header `Authorization` :

```
Authorization: Bearer <JWT_TOKEN>
```

**Exemple:**
```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzA2MjcwMDAwLCJleHAiOjE3MDYzNTY0MDB9..."

curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🛡️ Sécurité

| Aspect | Configuration |
|--------|---------------|
| **Mots de passe** | Hachage BCrypt |
| **Tokens JWT** | HS512 (HMAC-SHA512) |
| **Durée de vie** | 24 heures |
| **Session** | Stateless (pas de JSESSIONID) |
| **CSRF** | Désactivé (API REST) |

---

## 📊 Ressources publiques (sans token)

- `GET /api/products` - Catalogue des produits
- `GET /api/categories` - Catégories de produits
- `POST /api/auth/register` - Inscription
- `POST /api/auth/login` - Connexion

---

## 🔒 Ressources protégées (avec token)

- `GET /api/auth/me` - Profil utilisateur
- `POST /api/auth/logout` - Déconnexion
- `POST /api/auth/refresh-token` - Renouvellement token
- `GET /api/orders` - Historique des commandes
- `POST /api/orders` - Créer une commande
- `GET /api/cart` - Panier utilisateur

---

## 👮 Ressources admin (`ROLE_ADMIN` requis)

- `GET /api/admin/products` - Lister les produits
- `POST /api/admin/products` - Créer un produit
- `PUT /api/admin/products/{id}` - Modifier un produit
- `DELETE /api/admin/products/{id}` - Supprimer un produit
- `GET /api/admin/categories` - Lister les catégories
- `POST /api/admin/categories` - Créer une catégorie
- `GET /api/admin/users` - Lister les utilisateurs

---

## ⚠️ Erreurs courantes

### 401 Unauthorized
Token manquant, expiré ou invalide.

```json
{
  "status": 401,
  "error": "Non autorisé",
  "message": "JWT signature does not match locally computed signature",
  "path": "/api/auth/me"
}
```

**Solution:** Se reconnecter avec `/api/auth/login`

---

### 400 Bad Request (Login)
Email ou mot de passe incorrect.

```json
{
  "success": false,
  "message": "Email ou mot de passe incorrect"
}
```

---

### 400 Bad Request (Register)
Email déjà utilisé.

```json
{
  "success": false,
  "message": "Cet email est déjà utilisé"
}
```

---

**Documentation Phase 3 - 26/01/2026**
