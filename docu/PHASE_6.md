# Phase 6 : Gestion des Commandes - Documentation Complète

**Statut :** ✅ **BUILD SUCCESS** - Implémenté et fonctionnel (testé via Swagger / curl)

---

## 🎯 Objectif

Mettre en place la **gestion des commandes** pour les utilisateurs authentifiés :

- Créer une commande à partir du **panier courant**
- Consulter l’**historique des commandes** utilisateur
- Consulter le **détail d’une commande**
- **Annuler** une commande (côté client)
- Permettre à l’**admin** de :
  - Lister toutes les commandes
  - Modifier le **statut** d’une commande

---

## 📁 Fichiers impactés

### Services (`services/`)

#### 1. `OrderService.java` ✅ (NOUVEAU)

Service métier pour la gestion des commandes.

**Dépendances injectées :**
- `OrderRepository`
- `OrderLineRepository`
- `ProductRepository`
- `UserRepository`
- `AuditLogRepository`
- `CartService`

**Statuts supportés :**

```text
PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
```

**Méthodes principales :**

- `OrderDTO createOrderFromCart(Integer userId, String ipAddress, String userAgent)`
  - Récupère le panier via `cartService.getCart(userId)`
  - Vérifie que le panier n’est pas vide
  - Charge l’utilisateur (`UserRepository`)
  - Crée une entité `Order` :
    - `user`
    - `total` = total du panier
    - `status` = `"PENDING"`
  - Crée les `OrderLine` :
    - Charge chaque `Product` par `productId`
    - `quantity` = quantité du panier
    - `unitPrice` = `product.getPrice()`
    - `lineTotal` = `unitPrice × quantity`
  - Sauvegarde la commande via `orderRepository.save(order)` (cascade vers les lignes)
  - Vide le panier (`cartService.clearCart(userId)`)
  - Écrit un `AuditLog` avec :
    - `action = ORDER_CREATED`
    - `entityType = ORDER`
    - `entityId = orderId`
    - `user` + `ipAddress` + `userAgent`
  - Retourne un `OrderDTO`

- `Page<OrderDTO> getUserOrders(Integer userId, Pageable pageable)`
  - Utilise `orderRepository.findByUser_Id(userId, pageable)`
  - Pour l’historique client (`GET /api/orders`)

- `OrderDTO getUserOrderById(Integer userId, Integer orderId)`
  - Utilise `orderRepository.findByIdAndUser_Id(orderId, userId)`
  - Vérifie que l’utilisateur ne lit que ses propres commandes

- `OrderDTO cancelUserOrder(Integer userId, Integer orderId)`
  - Récupère la commande pour l’utilisateur
  - Vérifie que le statut n’est pas déjà `"CANCELLED"`
  - Met à jour `status = "CANCELLED"`
  - Sauvegarde la commande
  - Ajoute un `AuditLog` avec `ORDER_CANCELLED`

- `Page<OrderDTO> getAllOrders(Pageable pageable)` (ADMIN)
  - `orderRepository.findAll(pageable)`

- `OrderDTO getOrderById(Integer orderId)` (ADMIN)

- `OrderDTO updateOrderStatus(Integer orderId, String newStatus)` (ADMIN)
  - Valide que `newStatus` ∈ {`PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`}
  - Met à jour le statut
  - Crée un `AuditLog` :
    - `ORDER_CANCELLED` si statut `CANCELLED`
    - `ORDER_PAID` si statut `CONFIRMED` ou `DELIVERED`

**Conversions :**

- `Order -> OrderDTO`:
  - `id`, `orderDate`, `status`, `total`
  - `userId` + `UserDTO` (id, email, fullName, role)
  - Liste de `OrderLineDTO`

- `OrderLine -> OrderLineDTO`:
  - `id`, `quantity`, `unitPrice`, `lineTotal`
  - `productId` + `ProductDTO` minimal (id, name, description, price)

---

### Contrôleur (`controllers/`)

#### 2. `OrderController.java` ✅ (NOUVEAU)

Contrôleur REST des commandes.

Classe annotée :

```java
@RestController
@RequestMapping("")
public class OrderController { ... }
```

Avec `server.servlet.context-path=/api`, les URLs externes sont bien :

- `/api/orders/**`
- `/api/admin/orders/**`

---

## 🌐 Endpoints API - Côté Client

Tous ces endpoints exigent un **JWT valide** (utilisateur authentifié).

### 1️⃣ Créer une commande depuis le panier

- **Méthode :** `POST`
- **URL :** `/api/orders`
- **Body :** _aucun_ (la commande est créée à partir du panier courant)

**Flux :**
- Récupère `userId` depuis `UserPrincipal`
- Appelle `OrderService.createOrderFromCart(userId, ip, userAgent)`
- Vide le panier

**Réponse (exemple) :**

```json
{
  "id": 1,
  "userId": 2,
  "status": "PENDING",
  "total": 59.98,
  "orderLines": [
    {
      "id": 10,
      "productId": 1,
      "quantity": 2,
      "unitPrice": 29.99,
      "lineTotal": 59.98,
      "product": {
        "id": 1,
        "name": "Produit Panier"
      }
    }
  ]
}
```

---

### 2️⃣ Historique des commandes utilisateur

- **Méthode :** `GET`
- **URL :** `/api/orders?page=0&size=10&sort=orderDate,desc`

Retourne une `Page<OrderDTO>` (pagination Spring).

---

### 3️⃣ Détail d’une commande utilisateur

- **Méthode :** `GET`
- **URL :** `/api/orders/{id}`
- Vérifie que la commande appartient bien à l’utilisateur courant.

---

### 4️⃣ Annuler une commande

- **Méthode :** `PUT`
- **URL :** `/api/orders/{id}/cancel`
- Annule la commande si elle n’est pas déjà `CANCELLED`.

Réponse : `OrderDTO` avec `status = "CANCELLED"`.

---

## 🌐 Endpoints API - Admin

Ces endpoints exigent le rôle **`ROLE_ADMIN`** (gérés par `SecurityConfig` avec `.requestMatchers("/admin/**").hasRole("ADMIN")`).

### 5️⃣ Lister toutes les commandes

- **Méthode :** `GET`
- **URL :** `/api/admin/orders?page=0&size=10&sort=orderDate,desc`
- Réponse : `Page<OrderDTO>`

---

### 6️⃣ Détail d’une commande (admin)

- **Méthode :** `GET`
- **URL :** `/api/admin/orders/{id}`

---

### 7️⃣ Mettre à jour le statut d’une commande

- **Méthode :** `PUT`
- **URL :** `/api/admin/orders/{id}/status`
- **Body :**

```json
{
  "status": "CONFIRMED"
}
```

Statuts acceptés :

```text
PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
```

---

## 🔐 Sécurité & Audit

- Tous les endpoints `/api/orders/**` exigent un **token JWT valide** (auth obligatoire).
- Les endpoints `/api/admin/orders/**` exigent en plus le rôle **ADMIN**.
- Les actions importantes sont loguées dans `audit_logs` :
  - `ORDER_CREATED`
  - `ORDER_CANCELLED`
  - `ORDER_PAID` (pour certains changements de statut admin)

---

## 🧪 Tests manuels (exemples curl)

En supposant que tu as déjà :
- un utilisateur connecté (`$TOKEN`)
- un panier non vide (`/api/cart/add` utilisé avant)

```bash
# Créer une commande depuis le panier
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN"

# Historique commandes utilisateur
curl -X GET "http://localhost:8080/api/orders?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"

# Détail d'une commande
curl -X GET http://localhost:8080/api/orders/1 \
  -H "Authorization: Bearer $TOKEN"

# Annuler une commande
curl -X PUT http://localhost:8080/api/orders/1/cancel \
  -H "Authorization: Bearer $TOKEN"
```

Pour les endpoints admin (avec `$ADMIN_TOKEN`) :

```bash
# Lister toutes les commandes
curl -X GET "http://localhost:8080/api/admin/orders?page=0&size=10" \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# Modifier le statut d'une commande
curl -X PUT http://localhost:8080/api/admin/orders/1/status \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status":"CONFIRMED"}'
```

---

## 📋 Résumé Phase 6

| Composant      | Fichier              | Statut |
|----------------|----------------------|--------|
| Service        | `OrderService`       | ✅     |
| Controller     | `OrderController`    | ✅     |
| Repositories   | `OrderRepository`, `OrderLineRepository` (existants) | ✅ |
| Audit          | `AuditLog` + logs commandes | ✅ |

**Phase 6 : ✅ COMPLÉTÉE (Gestion complète des commandes côté backend)**

