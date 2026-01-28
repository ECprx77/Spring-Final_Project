# Phase 5 : Gestion du Panier - Documentation Complète

**Statut :** ✅ **BUILD SUCCESS** - Implémenté et fonctionnel (testé via Swagger)

---

## 🎯 Objectif

Mettre en place un **panier côté backend** permettant à un utilisateur authentifié de :

- Ajouter des produits à son panier
- Modifier les quantités
- Supprimer des lignes
- Vider complètement le panier
- Récupérer le détail du panier avec **calcul automatique du total**

Le tout en respectant l’architecture existante (DTO, services, contrôleurs, sécurité JWT).

---

## 📁 Fichiers créés

### DTOs (`dto/`)

#### 1. `CartItemDTO.java`
Représente une **ligne du panier**.

- `Integer productId` – identifiant du produit
- `String productName` – nom du produit (pour l’affichage)
- `BigDecimal unitPrice` – prix unitaire
- `Integer quantity` – quantité
- `BigDecimal lineTotal` – total de la ligne (unitPrice × quantity)

Validations :
- `@NotNull` sur `productId`, `unitPrice`, `quantity`
- `@Min(1)` sur `quantity`

---

#### 2. `CartDTO.java`
Représente le **panier complet** d’un utilisateur.

- `List<CartItemDTO> items` – liste des lignes du panier
- `BigDecimal total` – total global du panier

---

### Service (`services/`)

#### 3. `CartService.java`
Service métier qui gère l’état du panier.

**Stockage :**

- Structure en mémoire :
  - `Map<Integer, Map<Integer, CartItemDTO>> carts`
  - Clé 1 : `userId`
  - Clé 2 : `productId`
  - Valeur : `CartItemDTO`

Cela simule une sorte de **session par utilisateur** tout en restant compatible avec l’authentification JWT (stateless côté HTTP).

**Méthodes principales :**

- `CartDTO addItem(Integer userId, Integer productId, int quantity)`
  - Vérifie que la quantité ≥ 1
  - Charge le `Product` depuis `ProductRepository`
  - Crée ou met à jour la ligne du panier
  - Met à jour `lineTotal`
  - Retourne le `CartDTO` complet (recalculé)

- `CartDTO updateItemQuantity(Integer userId, Integer productId, int quantity)`
  - Vérifie que l’article existe dans le panier
  - Met à jour la quantité et `lineTotal`
  - Retourne le `CartDTO` complet

- `CartDTO removeItem(Integer userId, Integer productId)`
  - Supprime la ligne correspondante
  - Retourne le panier restant

- `void clearCart(Integer userId)`
  - Vide complètement le panier de l’utilisateur

- `CartDTO getCart(Integer userId)`
  - Retourne le panier actuel (ou un panier vide si aucun article)

**Calcul du total :**

Le total est recalculé à chaque modification via :

```java
BigDecimal total = items.stream()
    .map(CartItemDTO::getLineTotal)
    .reduce(BigDecimal.ZERO, BigDecimal::add);
```

---

### Contrôleur (`controllers/`)

#### 4. `CartController.java`
Contrôleur REST exposant les endpoints `/api/cart/**`.

**Récupération de l’utilisateur courant :**

- Utilise `SecurityContextHolder` + `UserPrincipal` :
  - `Authentication authentication = SecurityContextHolder.getContext().getAuthentication();`
  - `UserPrincipal user = (UserPrincipal) authentication.getPrincipal();`
  - Utilise `user.getId()` comme clé de panier.

---

## 🌐 Endpoints API Panier

Tous les endpoints panier sont **protégés** (JWT requis), conformément à `SecurityConfig` (`/api/cart/**` n’est pas en `permitAll()`).

### 1️⃣ Ajouter au panier

- **Méthode :** `POST`
- **URL :** `/api/cart/add`
- **Body :**

```json
{
  "productId": 1,
  "quantity": 2
}
```

**Effet :**
- Si le produit n’est pas encore dans le panier → crée une nouvelle ligne
- Si la ligne existe déjà → incrémente la quantité
- Met à jour `lineTotal` et le `total` global

**Réponse (exemple) :**

```json
{
  "items": [
    {
      "productId": 1,
      "productName": "Produit Panier",
      "unitPrice": 5.00,
      "quantity": 2,
      "lineTotal": 10.00
    }
  ],
  "total": 10.00
}
```

---

### 2️⃣ Modifier la quantité d’un article

- **Méthode :** `PUT`
- **URL :** `/api/cart/{productId}`
- **Body :**

```json
{
  "quantity": 3
}
```

**Effet :**
- Met à jour la quantité pour `productId`
- Recalcule `lineTotal` et le total du panier

---

### 3️⃣ Retirer un article du panier

- **Méthode :** `DELETE`
- **URL :** `/api/cart/{productId}`

**Effet :**
- Supprime la ligne du panier correspondant au `productId`
- Retourne le panier mis à jour

---

### 4️⃣ Vider complètement le panier

- **Méthode :** `DELETE`
- **URL :** `/api/cart`

**Réponse :**

```json
{
  "success": true,
  "message": "Panier vidé avec succès"
}
```

---

### 5️⃣ Récupérer le panier

- **Méthode :** `GET`
- **URL :** `/api/cart`

**Réponse (exemple) :**

```json
{
  "items": [
    {
      "productId": 1,
      "productName": "Produit Panier",
      "unitPrice": 5.00,
      "quantity": 2,
      "lineTotal": 10.00
    }
  ],
  "total": 10.00
}
```

Si le panier est vide :

```json
{
  "items": [],
  "total": 0.00
}
```

---

## 🔐 Sécurité

- Accès aux endpoints `/api/cart/**` :
  - **JWT obligatoire** (Authorization: `Bearer <token>`)
  - L’ID utilisateur est extrait depuis `UserPrincipal` et sert de clé pour le panier.
- Pas de logique de rôle spécifique (USER/ADMIN) pour le panier : l’important est que l’utilisateur soit authentifié.

---

## 🧪 Tests & Vérifications

- Démarrage via :

```bash
./mvnw spring-boot:run
```

- Vérification via **Swagger UI** :
  - Login : `POST /api/auth/login`
  - Authorize avec `Bearer <accessToken>`
  - Utilisation des endpoints `/api/products` pour récupérer un `productId`
  - Tests des endpoints `/api/cart/**`

---

## 📋 Résumé Phase 5

| Composant      | Fichier                        | Statut |
|----------------|--------------------------------|--------|
| DTOs           | `CartDTO`, `CartItemDTO`       | ✅     |
| Service        | `CartService`                  | ✅     |
| Controller     | `CartController` (`/api/cart`) | ✅     |
| Calcul du total| Automatique côté service       | ✅     |

**Phase 5 : ✅ COMPLÉTÉE (Backend panier opérationnel)**

