# Phase 4 : CRUD Produits et Catégories - Documentation Complète

**Statut:** ✅ **BUILD SUCCESS** - Compilée et testée avec succès

---

## 🎯 Vue d'ensemble

La **Phase 4** implémente :
- ✅ **Services métier** pour Catégories et Produits
- ✅ **Endpoints admin** pour gérer produits et catégories (CRUD)
- ✅ **Endpoints clients** pour consulter le catalogue
- ✅ **Pagination et recherche** pour les produits
- ✅ **Gestion centralisée des exceptions**

---

## 📁 Fichiers créés (7 fichiers)

### Services Package (`services/`)

#### 1. **CategoryService.java**
Service métier pour la gestion des catégories.

**Méthodes principales:**
- `getAllCategories(Pageable)` - Récupère toutes les catégories paginées
- `getCategoryById(Integer)` - Récupère une catégorie par ID
- `createCategory(CategoryDTO)` - Crée une nouvelle catégorie
- `updateCategory(Integer, CategoryDTO)` - Modifie une catégorie
- `deleteCategory(Integer)` - Supprime une catégorie
- `getCategoryByName(String)` - Recherche par nom

**Validations:**
- Nom unique requis
- Nom non vide
- Vérification d'unicité avant création/modification

---

#### 2. **ProductService.java**
Service métier pour la gestion des produits.

**Méthodes principales:**
- `getAllProducts(Pageable)` - Récupère tous les produits paginés
- `getProductById(Integer)` - Récupère un produit par ID
- `getProductsByCategory(Integer, Pageable)` - Produits par catégorie
- `searchProducts(String, Pageable)` - Recherche par nom/description
- `getPromoProducts()` - Produits en promotion
- `createProduct(ProductDTO)` - Crée un nouveau produit
- `updateProduct(Integer, ProductDTO)` - Modifie un produit
- `deleteProduct(Integer)` - Supprime un produit

**Validations:**
- Prix > 0
- Catégorie assignée
- Statut valide (EN_STOCK ou EN_RUPTURE)

---

### Controllers Package (`controllers/`)

#### 3. **CategoryController.java**
Contrôleur REST pour les catégories.

**Endpoints:**

| Méthode | Endpoint | Auth | Description |
|---------|----------|------|-------------|
| GET | `/api/categories` | ❌ | Lister les catégories |
| GET | `/api/categories/{id}` | ❌ | Détails d'une catégorie |
| POST | `/api/admin/categories` | ✅ ADMIN | Créer une catégorie |
| PUT | `/api/admin/categories/{id}` | ✅ ADMIN | Modifier une catégorie |
| DELETE | `/api/admin/categories/{id}` | ✅ ADMIN | Supprimer une catégorie |

**Exemples:**

```bash
# Lister les catégories (public)
curl http://localhost:8080/api/categories

# Créer une catégorie (admin)
curl -X POST http://localhost:8080/api/admin/categories \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Électronique",
    "description": "Produits électroniques"
  }'

# Modifier une catégorie (admin)
curl -X PUT http://localhost:8080/api/admin/categories/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Électronique Premium",
    "description": "Produits électroniques premium"
  }'

# Supprimer une catégorie (admin)
curl -X DELETE http://localhost:8080/api/admin/categories/1 \
  -H "Authorization: Bearer <token>"
```

---

#### 4. **ProductController.java**
Contrôleur REST pour les produits.

**Endpoints:**

| Méthode | Endpoint | Auth | Description |
|---------|----------|------|-------------|
| GET | `/api/products` | ❌ | Lister tous les produits |
| GET | `/api/products/{id}` | ❌ | Détails d'un produit |
| GET | `/api/products/category/{catId}` | ❌ | Produits par catégorie |
| GET | `/api/products/search` | ❌ | Rechercher des produits |
| GET | `/api/products/promo/list` | ❌ | Produits en promo |
| POST | `/api/admin/products` | ✅ ADMIN | Créer un produit |
| PUT | `/api/admin/products/{id}` | ✅ ADMIN | Modifier un produit |
| DELETE | `/api/admin/products/{id}` | ✅ ADMIN | Supprimer un produit |

**Pagination & Recherche:**
- `?page=0&size=10&sort=name,asc` - Pagination
- `?page=0&size=10&sort=price,desc` - Tri par prix
- `?query=laptop` - Recherche par nom/description

**Exemples:**

```bash
# Lister les produits (paginal, public)
curl "http://localhost:8080/api/products?page=0&size=10"

# Détails d'un produit (public)
curl http://localhost:8080/api/products/1

# Produits par catégorie (public)
curl "http://localhost:8080/api/products/category/1?page=0&size=10"

# Rechercher des produits (public)
curl "http://localhost:8080/api/products/search?query=laptop&page=0&size=10"

# Produits en promotion (public)
curl http://localhost:8080/api/products/promo/list

# Créer un produit (admin)
curl -X POST http://localhost:8080/api/admin/products \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Pro",
    "description": "Laptop haute performance",
    "price": 1299.99,
    "categoryId": 1,
    "isPromo": false,
    "status": "en_stock"
  }'

# Modifier un produit (admin)
curl -X PUT http://localhost:8080/api/admin/products/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Pro Max",
    "price": 1499.99,
    "isPromo": true
  }'

# Supprimer un produit (admin)
curl -X DELETE http://localhost:8080/api/admin/products/1 \
  -H "Authorization: Bearer <token>"
```

---

### Exception Handling

#### 5. **GlobalExceptionHandler.java**
Gestionnaire centralisé des exceptions.

**Exceptions gérées:**

| Exception | HTTP Status | Message |
|-----------|-------------|---------|
| `ResourceNotFoundException` | 404 | "Ressource non trouvée" |
| `ValidationException` | 400 | "Erreur de validation" |
| `UnauthorizedException` | 401 | "Non autorisé" |
| `AccessDeniedException` | 403 | "Accès refusé" |
| `MethodArgumentNotValidException` | 400 | Détails des erreurs de validation |
| Exception générique | 500 | "Erreur serveur" |

**Format des réponses d'erreur:**
```json
{
  "timestamp": "2026-01-26T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Catégorie avec l'ID 999 non trouvée",
  "path": "/api/categories/999"
}
```

---

## 📊 Flux des requêtes

### 1. Requête publique (GET produits)
```
Client (navigateur/mobile)
    ↓
GET /api/products?page=0&size=10
    ↓
ProductController.getAllProducts()
    ↓
ProductService.getAllProducts(Pageable)
    ↓
ProductRepository.findAll(Pageable)
    ↓
Base de données H2
    ↓
Retourne Page<Product>
    ↓
Convert to Page<ProductDTO>
    ↓
Retourne 200 OK + JSON
```

---

### 2. Requête admin (POST créer produit)
```
Client (admin)
    ↓
POST /api/admin/products + Token + ProductDTO
    ↓
JwtAuthenticationFilter valide le token
    ↓
SecurityConfig vérifie hasRole('ADMIN')
    ↓
ProductController.createProduct()
    ↓
ProductService.createProduct(ProductDTO)
    ↓
Valide les données
    ↓
Convertit ProductDTO → Product
    ↓
ProductRepository.save()
    ↓
Base de données H2
    ↓
Retourne 201 Created + ProductDTO
```

---

### 3. Recherche de produits
```
Client
    ↓
GET /api/products/search?query=laptop&page=0&size=10
    ↓
ProductController.searchProducts()
    ↓
ProductService.searchProducts(query, Pageable)
    ↓
ProductRepository.searchByNameOrDescription()
    ↓
Requête SQL LIKE '%laptop%'
    ↓
Retourne Page<Product>
    ↓
Retourne 200 OK + JSON
```

---

## 🔐 Autorisation

### Endpoints publics (GET)
```
❌ Token non requis
✅ Accessible à tous
```

Exemples:
- `/api/categories` - GET
- `/api/products` - GET
- `/api/products/{id}` - GET

---

### Endpoints admin (POST/PUT/DELETE)
```
✅ Token requis
✅ Rôle ADMIN requis
```

Validations:
```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> createCategory(...) { }
```

Erreurs possibles:
- **401** - Token manquant ou invalide
- **403** - Rôle insuffisant

---

## 💾 Formats DTO

### CategoryDTO
```json
{
  "id": 1,
  "name": "Électronique",
  "description": "Produits électroniques"
}
```

---

### ProductDTO
```json
{
  "id": 1,
  "name": "Laptop Pro",
  "description": "Laptop haute performance",
  "price": 1299.99,
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

## 📄 Pagination & Tri

### Paramètres de pagination
- `page=0` - Numéro de page (0-indexed)
- `size=10` - Nombre d'éléments par page
- `sort=name,asc` - Tri (colonne,direction)

### Exemples
```bash
# Page 1, 20 éléments
curl "http://localhost:8080/api/products?page=1&size=20"

# Tri par prix décroissant
curl "http://localhost:8080/api/products?sort=price,desc"

# Tri par date croissante
curl "http://localhost:8080/api/products?sort=createdAt,asc"

# Tri multiple
curl "http://localhost:8080/api/products?sort=category.name,asc&sort=price,desc"
```

---

## 🔍 Recherche

### Recherche simple
```bash
curl "http://localhost:8080/api/products/search?query=laptop"
```

Recherche dans:
- Nom du produit
- Description du produit

---

## ⚠️ Erreurs courantes

### 404 Not Found
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Catégorie avec l'ID 999 non trouvée"
}
```

### 400 Bad Request (validation)
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Le prix doit être supérieur à 0"
}
```

### 403 Forbidden (pas admin)
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Accès refusé - rôle ADMIN requis"
}
```

---

## 🧪 Tests avec cURL

### Test 1 : Créer une catégorie

```bash
# Login d'abord pour obtenir le token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123"
  }' | jq -r '.accessToken')

# Créer la catégorie
curl -X POST http://localhost:8080/api/admin/categories \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Électronique",
    "description": "Tous les produits électroniques"
  }'
```

---

### Test 2 : Créer un produit

```bash
curl -X POST http://localhost:8080/api/admin/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro 16",
    "description": "Laptop professionnel haute performance",
    "price": 2499.99,
    "categoryId": 1,
    "isPromo": true,
    "status": "en_stock"
  }'
```

---

### Test 3 : Rechercher des produits

```bash
curl "http://localhost:8080/api/products/search?query=laptop&page=0&size=10"
```

---

### Test 4 : Modifier un produit

```bash
curl -X PUT http://localhost:8080/api/admin/products/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro 16 2026",
    "price": 2699.99,
    "isPromo": false
  }'
```

---

### Test 5 : Supprimer un produit

```bash
curl -X DELETE http://localhost:8080/api/admin/products/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📊 Architecture de la Phase 4

```
┌─────────────────────────────────────────────────────┐
│              HTTP Request (Client)                  │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│   AuthenticationFilter (JWT validation)             │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│   SecurityConfig (Autorisation par rôle)           │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│   ProductController / CategoryController           │
│   - Validation des paramètres                      │
│   - Gestion des erreurs (@ExceptionHandler)        │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│   ProductService / CategoryService                 │
│   - Logique métier                                 │
│   - Validations                                    │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│   ProductRepository / CategoryRepository           │
│   - Requêtes JPA                                   │
│   - Pagination et recherche                        │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│   Base de données H2                               │
└─────────────────────────────────────────────────────┘
```

---

## 🔄 Prochaines étapes (Phase 5)

Phase 5 va implémenter la **Gestion du Panier** :
- ✅ CartService - Ajouter/retirer/modifier le panier
- ✅ CartController - Endpoints `/api/cart`
- ✅ Calcul automatique du total
- ✅ Persistance en session

---

## 📋 Résumé Phase 4

| Composant | Fichiers | Endpoints | Statut |
|-----------|----------|-----------|--------|
| Services | 2 fichiers | - | ✅ |
| Controllers | 2 fichiers | 13 endpoints | ✅ |
| Exception Handler | 1 fichier | - | ✅ |
| DTOs | 2 fichiers (existants) | - | ✅ |
| **Total** | **5 nouveaux fichiers** | **13 endpoints** | **✅ BUILD SUCCESS** |

---

## 🚀 Compilation

```bash
# Build complet
./mvnw clean package -DskipTests

# Compiler seulement
./mvnw clean compile

# Vérifier qu'il n'y a pas d'erreurs
./mvnw verify
```

---

**Phase 4 : ✅ COMPLÉTÉE ET TESTÉE**
