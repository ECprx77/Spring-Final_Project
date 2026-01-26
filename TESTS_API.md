# 🧪 Tests API TechZone - Exemples cURL complets

## 📝 Prérequis

Application démarrée: `http://localhost:8080`

---

## ✅ Test complet du flux

### 1️⃣ Inscription d'un nouvel utilisateur

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "demo@example.com",
    "fullName": "Demo User",
    "password": "DemoPass123"
  }'
```

**Réponse:**
```json
{
  "success": true,
  "message": "Utilisateur enregistré avec succès. Vous pouvez maintenant vous connecter."
}
```

---

### 2️⃣ Connexion et récupération du token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "demo@example.com",
    "password": "DemoPass123"
  }'
```

**Réponse:**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzA2Mjk0NDAwLCJleHAiOjE3MDYzODA4MDB9.xyz...",
  "tokenType": "Bearer"
}
```

**Stocker le token:**
```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzA2Mjk0NDAwLCJleHAiOjE3MDYzODA4MDB9.xyz..."
```

---

### 3️⃣ Consulter le profil utilisateur

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

**Réponse:**
```json
{
  "id": 1,
  "email": "demo@example.com",
  "fullName": "Demo User",
  "role": {
    "id": 2,
    "name": "USER"
  }
}
```

---

## 🛒 Endpoints Catégories (Phase 4)

### Lister les catégories (public)

```bash
curl http://localhost:8080/api/categories
```

**Réponse:**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Électronique",
      "description": "Tous les produits électroniques"
    },
    {
      "id": 2,
      "name": "Informatique",
      "description": "Produits informatiques"
    }
  ],
  "pageable": {...},
  "totalElements": 2,
  "totalPages": 1,
  "size": 10,
  "number": 0,
  "empty": false
}
```

---

### Détails d'une catégorie (public)

```bash
curl http://localhost:8080/api/categories/1
```

**Réponse:**
```json
{
  "id": 1,
  "name": "Électronique",
  "description": "Tous les produits électroniques"
}
```

---

### Créer une catégorie (admin)

⚠️ **Nécessite token ADMIN**

```bash
curl -X POST http://localhost:8080/api/admin/categories \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Vêtements",
    "description": "Vêtements et accessoires"
  }'
```

**Réponse:**
```json
{
  "id": 3,
  "name": "Vêtements",
  "description": "Vêtements et accessoires"
}
```

---

### Modifier une catégorie (admin)

```bash
curl -X PUT http://localhost:8080/api/admin/categories/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Électronique Premium",
    "description": "Produits électroniques haut de gamme"
  }'
```

**Réponse:**
```json
{
  "id": 1,
  "name": "Électronique Premium",
  "description": "Produits électroniques haut de gamme"
}
```

---

### Supprimer une catégorie (admin)

```bash
curl -X DELETE http://localhost:8080/api/admin/categories/3 \
  -H "Authorization: Bearer $TOKEN"
```

**Réponse:** 204 No Content

---

## 📦 Endpoints Produits (Phase 4)

### Lister les produits (paginal, public)

```bash
# Page 1, 10 produits
curl "http://localhost:8080/api/products?page=0&size=10"

# Avec tri par prix décroissant
curl "http://localhost:8080/api/products?page=0&size=10&sort=price,desc"

# Avec tri par nom croissant
curl "http://localhost:8080/api/products?page=0&size=10&sort=name,asc"
```

**Réponse:**
```json
{
  "content": [
    {
      "id": 1,
      "name": "MacBook Pro 16",
      "description": "Laptop professionnel",
      "price": 2499.99,
      "status": "en_stock",
      "isPromo": false,
      "categoryId": 1,
      "category": {
        "id": 1,
        "name": "Électronique"
      },
      "createdAt": "2026-01-26T12:00:00"
    }
  ],
  "pageable": {...},
  "totalElements": 25,
  "totalPages": 3,
  "size": 10,
  "number": 0
}
```

---

### Détails d'un produit (public)

```bash
curl http://localhost:8080/api/products/1
```

**Réponse:**
```json
{
  "id": 1,
  "name": "MacBook Pro 16",
  "description": "Laptop professionnel haute performance",
  "price": 2499.99,
  "status": "en_stock",
  "isPromo": false,
  "categoryId": 1,
  "category": {
    "id": 1,
    "name": "Électronique"
  },
  "createdAt": "2026-01-26T12:00:00"
}
```

---

### Produits par catégorie (public)

```bash
curl "http://localhost:8080/api/products/category/1?page=0&size=10"
```

**Réponse:** Même format que "Lister les produits"

---

### Rechercher des produits (public)

```bash
# Recherche simple
curl "http://localhost:8080/api/products/search?query=laptop"

# Avec pagination
curl "http://localhost:8080/api/products/search?query=laptop&page=0&size=10"

# Avec tri
curl "http://localhost:8080/api/products/search?query=laptop&sort=price,desc&page=0&size=10"
```

**Réponse:** Même format que "Lister les produits"

---

### Produits en promotion (public)

```bash
curl http://localhost:8080/api/products/promo/list
```

**Réponse:**
```json
[
  {
    "id": 2,
    "name": "iPad Pro 12.9",
    "price": 1099.99,
    "isPromo": true,
    ...
  },
  {
    "id": 4,
    "name": "iPhone 15 Pro",
    "price": 999.99,
    "isPromo": true,
    ...
  }
]
```

---

### Créer un produit (admin)

⚠️ **Nécessite token ADMIN**

```bash
curl -X POST http://localhost:8080/api/admin/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "AirPods Pro",
    "description": "Écouteurs sans fil avec réduction de bruit",
    "price": 249.99,
    "categoryId": 1,
    "status": "en_stock",
    "isPromo": false
  }'
```

**Réponse:**
```json
{
  "id": 5,
  "name": "AirPods Pro",
  "description": "Écouteurs sans fil avec réduction de bruit",
  "price": 249.99,
  "categoryId": 1,
  "status": "en_stock",
  "isPromo": false,
  "createdAt": "2026-01-26T12:30:00"
}
```

---

### Modifier un produit (admin)

```bash
curl -X PUT http://localhost:8080/api/admin/products/5 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "AirPods Pro 2",
    "price": 299.99,
    "isPromo": true
  }'
```

**Réponse:**
```json
{
  "id": 5,
  "name": "AirPods Pro 2",
  "price": 299.99,
  "isPromo": true,
  ...
}
```

---

### Supprimer un produit (admin)

```bash
curl -X DELETE http://localhost:8080/api/admin/products/5 \
  -H "Authorization: Bearer $TOKEN"
```

**Réponse:** 204 No Content

---

## 🔄 Gestion des tokens

### Renouveler le token

```bash
curl -X POST http://localhost:8080/api/auth/refresh-token \
  -H "Authorization: Bearer $TOKEN"
```

**Réponse:**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzA2Mjk0NjAwLCJleHAiOjE3MDYzODEwMDB9.xyz...",
  "tokenType": "Bearer"
}
```

---

### Déconnexion

```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer $TOKEN"
```

**Réponse:**
```json
{
  "success": true,
  "message": "Vous avez été déconnecté avec succès"
}
```

---

## ⚠️ Erreurs courantes

### 401 Unauthorized (token manquant/invalide)

```bash
curl http://localhost:8080/api/auth/me
```

**Réponse:**
```json
{
  "status": 401,
  "error": "Non autorisé",
  "message": "JWT signature does not match locally computed signature",
  "path": "/api/auth/me"
}
```

---

### 403 Forbidden (pas admin)

```bash
curl -X POST http://localhost:8080/api/admin/products \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test"}'
```

**Réponse:**
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Accès refusé - rôle ADMIN requis"
}
```

---

### 404 Not Found

```bash
curl http://localhost:8080/api/products/999
```

**Réponse:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Produit avec l'ID 999 non trouvé"
}
```

---

### 400 Bad Request (validation)

```bash
curl -X POST http://localhost:8080/api/admin/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","price":-100}'
```

**Réponse:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Le prix doit être supérieur à 0"
}
```

---

## 🚀 Commandes bash utiles

### Créer une variable de token

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "demo@example.com",
    "password": "DemoPass123"
  }' | jq -r '.accessToken')

echo "Token: $TOKEN"
```

---

### Tester un endpoint protégé

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $(curl -s -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"email":"demo@example.com","password":"DemoPass123"}' | jq -r '.accessToken')"
```

---

### Lister tous les produits en JSON formaté

```bash
curl -s http://localhost:8080/api/products | jq '.'
```

---

### Rechercher et formater

```bash
curl -s "http://localhost:8080/api/products/search?query=laptop" | jq '.content[] | {id, name, price}'
```

---

## 📋 Récapitulatif des endpoints

| Endpoint | Méthode | Auth | Type |
|----------|---------|------|------|
| `/api/auth/register` | POST | ❌ | Public |
| `/api/auth/login` | POST | ❌ | Public |
| `/api/auth/me` | GET | ✅ | Authentifié |
| `/api/auth/logout` | POST | ✅ | Authentifié |
| `/api/auth/refresh-token` | POST | ✅ | Authentifié |
| `/api/categories` | GET | ❌ | Public |
| `/api/categories/{id}` | GET | ❌ | Public |
| `/api/admin/categories` | POST | ✅ ADMIN | Admin |
| `/api/admin/categories/{id}` | PUT | ✅ ADMIN | Admin |
| `/api/admin/categories/{id}` | DELETE | ✅ ADMIN | Admin |
| `/api/products` | GET | ❌ | Public |
| `/api/products/{id}` | GET | ❌ | Public |
| `/api/products/category/{id}` | GET | ❌ | Public |
| `/api/products/search` | GET | ❌ | Public |
| `/api/products/promo/list` | GET | ❌ | Public |
| `/api/admin/products` | POST | ✅ ADMIN | Admin |
| `/api/admin/products/{id}` | PUT | ✅ ADMIN | Admin |
| `/api/admin/products/{id}` | DELETE | ✅ ADMIN | Admin |

---

**TechZone - Tests API**
**Version:** 1.0 (Phases 3-4)
**Date:** 26/01/2026
