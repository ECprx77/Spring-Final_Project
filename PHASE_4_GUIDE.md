# Phase 4 - Guide rapide CRUD Produits & Catégories

## 🚀 Endpoints disponibles

### Catégories (publiques)
```bash
# Lister les catégories
curl http://localhost:8080/api/categories

# Détails d'une catégorie
curl http://localhost:8080/api/categories/1
```

### Catégories (admin)
```bash
# Créer une catégorie
curl -X POST http://localhost:8080/api/admin/categories \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Électronique","description":"Produits électroniques"}'

# Modifier une catégorie
curl -X PUT http://localhost:8080/api/admin/categories/1 \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Électronique Premium"}'

# Supprimer une catégorie
curl -X DELETE http://localhost:8080/api/admin/categories/1 \
  -H "Authorization: Bearer <TOKEN>"
```

---

## 📦 Produits

### Produits (publiques)
```bash
# Lister tous les produits (page 0, 10 par page)
curl "http://localhost:8080/api/products?page=0&size=10"

# Détails d'un produit
curl http://localhost:8080/api/products/1

# Produits d'une catégorie
curl "http://localhost:8080/api/products/category/1?page=0&size=10"

# Rechercher des produits
curl "http://localhost:8080/api/products/search?query=laptop"

# Produits en promotion
curl http://localhost:8080/api/products/promo/list

# Trier par prix (décroissant)
curl "http://localhost:8080/api/products?page=0&size=10&sort=price,desc"
```

### Produits (admin)
```bash
# Créer un produit
curl -X POST http://localhost:8080/api/admin/products \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name":"MacBook Pro",
    "description":"Laptop professionnel",
    "price":2499.99,
    "categoryId":1,
    "isPromo":false,
    "status":"en_stock"
  }'

# Modifier un produit
curl -X PUT http://localhost:8080/api/admin/products/1 \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"name":"MacBook Pro Max","price":2999.99,"isPromo":true}'

# Supprimer un produit
curl -X DELETE http://localhost:8080/api/admin/products/1 \
  -H "Authorization: Bearer <TOKEN>"
```

---

## 📊 Pagination & Tri

```bash
# Page 1, 20 éléments
curl "http://localhost:8080/api/products?page=1&size=20"

# Trier par nom (croissant)
curl "http://localhost:8080/api/products?sort=name,asc"

# Trier par prix (décroissant)
curl "http://localhost:8080/api/products?sort=price,desc"

# Trier par date de création
curl "http://localhost:8080/api/products?sort=createdAt,asc"
```

---

## ✨ Points clés Phase 4

- ✅ Services métier complets (CRUD)
- ✅ Pagination & recherche
- ✅ Endpoints publics et admin
- ✅ Gestion des erreurs centralisée
- ✅ Validation des données
- ✅ Autorisation par rôle (@PreAuthorize)

---

## 📋 Ressources

- **Services:** `CategoryService.java`, `ProductService.java`
- **Controllers:** `CategoryController.java`, `ProductController.java`
- **Exceptions:** `GlobalExceptionHandler.java`
- **DTOs:** `CategoryDTO.java`, `ProductDTO.java`
- **Repositories:** Déjà existants

---

**Phase 4 : ✅ BUILD SUCCESS**
