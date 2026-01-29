# 🚀 TechZone - Roadmap Complet

**Plateforme E-commerce - Cahier des charges**

---

## 📋 Table des matières

- [Backend](#backend)
- [Frontend](#frontend)

---

## 🔧 BACKEND

### Phase 1 : Configuration initiale et Architecture

- [x] **Mise en place du projet Spring Boot**
  - [x] Configuration Maven (pom.xml)
  - [x] Structure des packages (controllers, services, repositories, entities, **security**, dto) — *PDF*
  - [x] Configuration application.properties
  - [x] Dépendances Spring Data JPA, Spring Security, Spring Web

- [x] **Base de données**
  - [x] Conception MCD (Modèle Conceptuel de Données)
  - [x] Diagramme de classes UML
  - [x] Scripts de création tables
  - [x] Configuration H2/MySQL
  - [x] **Données de test** — *PDF : 2 ADMIN, 3 USER, plusieurs catégories, 10–15 produits*

### Phase 2 : Modèles et Entités JPA

- [x] **Entités métier** (MCD PDF : Utilisateur, Rôle, Produit, Catégorie, Commande, LigneCommande)
  - [x] User (Client/Admin)
  - [x] Product
  - [x] Category
  - [x] Cart / CartItem
  - [x] Order / OrderItem
  - [x] Review *(optionnel, hors PDF)*
  - [x] Relationships et contraintes

- [x] **Validations JPA**
  - [x] Annotations @NotNull, @NotBlank, @Email
  - [x] Constraints personnalisés si nécessaire
  - [x] Serialization/Deserialization

### Phase 3 : Sécurité et Authentification

- [x] **Spring Security**
  - [x] Configuration SecurityConfig
  - [x] Hachage des mots de passe (BCrypt)
  - [x] JWT (JSON Web Tokens) avec JJWT 0.12.3
  - [x] Authentication Provider personnalisé

- [x] **Endpoints d'authentification**
  - [x] POST /api/auth/register (inscription)
  - [x] POST /api/auth/login (connexion)
  - [x] POST /api/auth/logout (déconnexion)
  - [x] POST /api/auth/refresh-token (renouvellement token)
  - [x] GET /api/auth/me (profil utilisateur)

- [x] **Gestion des rôles**
  - [x] ROLE_ADMIN (accès aux endpoints /api/admin/**)
  - [x] ROLE_USER (utilisateur par défaut)
  - [x] Support pour ROLE_CUSTOMER (optionnel)

### Phase 4 : CRUD Produits (Catégories)

- [x] **Repository et Service**
  - [x] CategoryRepository (JpaRepository)
  - [x] CategoryService (métiers)
  - [x] ProductRepository
  - [x] ProductService

- [x] **Endpoints Admin**
  - [x] POST /api/admin/categories (créer)
  - [x] GET /api/admin/categories/{id} (détails)
  - [x] PUT /api/admin/categories/{id} (modifier)
  - [x] DELETE /api/admin/categories/{id} (supprimer)
  - [x] GET /api/admin/categories (liste avec pagination)

- [x] **Endpoints Admin Produits**
  - [x] POST /api/admin/products (créer)
  - [x] PUT /api/admin/products/{id} (modifier)
  - [x] DELETE /api/admin/products/{id} (supprimer)
  - [x] GET /api/admin/products (liste paginée)
  - [x] GET /api/admin/products/{id} (détails)

- [x] **Endpoints Client Produits**
  - [x] GET /api/products (catalogue, **pagination** — *PDF USER + ADMIN*)
  - [x] GET /api/products/{id} (détails)
  - [x] GET /api/products/category/{catId} (filtrage par catégorie)
  - [x] GET /api/products/search?q={query} (recherche)
  - [ ] Filtres *PDF* : **en promotion**, **en stock uniquement** | Produit : **statut en stock/rupture**, **promotion** (booléen)

### Phase 5 : Gestion du Panier

- [x] **Cart Service**
  - [x] Ajouter au panier (POST /api/cart/add)
  - [x] Retirer du panier (DELETE /api/cart/{itemId})
  - [x] Modifier quantité (PUT /api/cart/{itemId})
  - [x] Vider le panier (DELETE /api/cart)
  - [x] Récupérer le panier (GET /api/cart)
  - [x] Calcul total automatique

### Phase 6 : Gestion des Commandes

- [x] **Order Service**
  - [x] Créer une commande (POST /api/orders)
  - [x] Historique commandes (GET /api/orders)
  - [x] Détails commande (GET /api/orders/{id})
  - [x] Annuler commande (PUT /api/orders/{id}/cancel)
  - [x] Statuts : PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED

- [x] **Endpoints Admin**
  - [x] GET /api/admin/orders (toutes les commandes)
  - [x] PUT /api/admin/orders/{id}/status (modifier statut)
  - [x] Exports et statistiques


### Phase 7 : Avis et Commentaires *(optionnel, hors PDF)*

- [ ] **Review Service**
  - [ ] POST /api/reviews (créer avis)
  - [ ] GET /api/products/{id}/reviews (liste avis)
  - [ ] PUT /api/reviews/{id} (modifier)
  - [ ] DELETE /api/reviews/{id} (supprimer)
  - [ ] Système de notation (1-5 étoiles)

### Phase 8 : Gestion Utilisateurs (Admin)

- [x] **Endpoints Admin**
  - [x] GET /api/admin/users (liste utilisateurs)
  - [x] GET /api/admin/users/{id} (détails)
  - [x] PUT /api/admin/users/{id} (modifier)
  - [x] DELETE /api/admin/users/{id} (supprimer)
  - [x] PUT /api/admin/users/{id}/role (changer rôle)

### Phase 9 : Tests et Documentation

- [x] **Tests unitaires** ✅
  - [x] Services (CartService, OrderService, CategoryService, ProductService)
  - [x] Controllers (AuthController)
  - [x] 59 tests total, 0 failures
  - [x] Service layer coverage: ~95%

- [ ] **Tests d'intégration**
  - [ ] Full HTTP endpoint tests (optional - service tests provide strong coverage)
  - [ ] Authentication and authorization flows (partial - covered in AuthControllerTest)
  - [x] Error cases (covered in service unit tests)

- [x] **Documentation** ✅
  - [x] Swagger/Springdoc-OpenAPI (API docs) — *fonct. avancée PDF*
  - [x] README backend
  - [x] Guide d'installation et configuration
  - [x] TESTING.md - Comprehensive testing documentation
  - [x] **Collection Postman** (auth, admin, user, JWT) — *livrable PDF* ✅

**Testing Status**: 59 comprehensive unit tests covering all core business logic. See `TechZone/TESTING.md` for complete documentation.

**Postman Collection**: Complete API testing suite with 32 requests across 6 folders. Auto-saves JWT tokens. See `TechZone/POSTMAN_GUIDE.md` for usage.

---

## 🎨 FRONTEND

### Phase 1 : Configuration React

- [ ] **Structure du projet**
  - [ ] Create React App ou Vite
  - [ ] Dossiers : components, pages, services, utils, hooks
  - [ ] Structure de routing

- [ ] **Dépendances principales**
  - [ ] React Router (navigation)
  - [ ] Axios ou Fetch (API calls)
  - [ ] State management (Redux, Zustand, Context API)
  - [ ] UI Framework (Material-UI, Tailwind, ou Bootstrap)

### Phase 2 : Pages et Layouts

- [ ] **Layout général**
  - [ ] Header/Navbar (logo, menu, search, user account, cart)
  - [ ] Footer
  - [ ] Sidebar (si nécessaire)
  - [ ] Responsive design

- [ ] **Pages client**
  - [ ] Accueil / Homepage
  - [ ] Catalogue produits
  - [ ] Détails produit
  - [ ] Panier
  - [ ] Checkout
  - [ ] Confirmation commande
  - [ ] Historique commandes
  - [ ] Mon compte / Profil
  - [ ] Contact / FAQ

- [ ] **Pages admin**
  - [ ] Dashboard (statistiques)
  - [ ] Gestion produits (CRUD)
  - [ ] Gestion catégories (CRUD)
  - [ ] Gestion commandes
  - [ ] Gestion utilisateurs
  - [ ] Statistiques et rapports

### Phase 3 : Authentification

- [ ] **Pages d'authentification**
  - [ ] Inscription (register.jsx)
  - [ ] Connexion (login.jsx)
  - [ ] Oubli mot de passe (forget-password.jsx)
  - [ ] Réinitialisation mot de passe

- [ ] **Services d'authentification**
  - [ ] Service API (authService.js)
  - [ ] Gestion du token (localStorage)
  - [ ] Interceptors Axios (token dans headers)
  - [ ] Routes protégées (PrivateRoute)

### Phase 4 : Catalogue et Produits

- [ ] **Pages catalogue**
  - [ ] Liste produits avec pagination
  - [ ] Filtres (catégorie, prix, note)
  - [ ] Barre de recherche
  - [ ] Tri (récent, prix, populaire)

- [ ] **Page détails produit**
  - [ ] Images et galerie
  - [ ] Description
  - [ ] Avis clients
  - [ ] Prix et stock
  - [ ] Bouton "Ajouter au panier"
  - [ ] Produits similaires

### Phase 5 : Gestion du Panier

- [ ] **Panier (Cart component)**
  - [ ] Liste des articles
  - [ ] Modification quantité
  - [ ] Suppression articles
  - [ ] Calcul total/sous-total
  - [ ] Lien vers checkout
  - [ ] Panier vide (message)

- [ ] **État global du panier**
  - [ ] Redux Store ou Context API
  - [ ] Actions : ADD_ITEM, REMOVE_ITEM, UPDATE_QUANTITY
  - [ ] Persistent storage (localStorage)

### Phase 6 : Processus de Commande

- [ ] **Checkout** *(PDF : pas d’intégration paiement)*
  - [ ] Adresse de livraison (si nécessaire)
  - [ ] Récapitulatif panier
  - [ ] Validation → création commande (passage de commande)
  - [ ] Validation à chaque étape

- [ ] **Confirmation**
  - [ ] Page de confirmation
  - [ ] Email de confirmation (si possible)
  - [ ] Lien vers commande / historique

### Phase 7 : Espace Utilisateur

- [ ] **Mon Compte**
  - [ ] Profil (affichage/modification)
  - [ ] Adresses de livraison
  - [ ] Historique commandes
  - [ ] Suivi commande
  - [ ] Wishlist (optionnel)

- [ ] **Gestion profil**
  - [ ] Modification données personnelles
  - [ ] Changement mot de passe
  - [ ] Suppression de compte

### Phase 8 : Avis et Évaluations *(optionnel, hors PDF)*

- [ ] **Système d'avis**
  - [ ] Formulaire d'avis sur produit
  - [ ] Affichage avis (liste, moyenne note)
  - [ ] Filtrage par note
  - [ ] Modération admin (optionnel)

### Phase 9 : Interface Admin

- [ ] **Dashboard Admin**
  - [ ] Statistiques (ventes, utilisateurs, produits)
  - [ ] Graphiques (charts)
  - [ ] KPIs principaux

- [ ] **CRUD Produits**
  - [ ] Table produits (liste, pagination)
  - [ ] Formulaire ajout/édition
  - [ ] Upload images
  - [ ] Suppression avec confirmation

- [ ] **CRUD Catégories**
  - [ ] Gestion catégories
  - [ ] Formulaire ajout/édition
  - [ ] Suppression

- [ ] **Gestion Commandes**
  - [ ] Liste des commandes
  - [ ] Détails commande
  - [ ] Modification statut
  - [ ] Impression/export

- [ ] **Gestion Utilisateurs**
  - [ ] Liste des utilisateurs
  - [ ] Détails utilisateur
  - [ ] Modification rôle/permissions
  - [ ] Suppression

### Phase 10 : Expérience Utilisateur

- [ ] **Notifications**
  - [ ] Toast (messages succès/erreur)
  - [ ] Loading spinners
  - [ ] Modals de confirmation

- [ ] **Optimisations**
  - [ ] Lazy loading images
  - [ ] Code splitting (React.lazy)
  - [ ] Cache API
  - [ ] SEO de base (meta tags)

- [ ] **Accessibilité**
  - [ ] ARIA labels
  - [ ] Navigation au clavier
  - [ ] Contraste couleurs

### Phase 11 : Responsive Design

- [ ] **Mobile first**
  - [ ] Mobile (< 768px)
  - [ ] Tablet (768px - 1024px)
  - [ ] Desktop (> 1024px)

- [ ] **Navigation mobile**
  - [ ] Menu hamburger
  - [ ] Sticky cart
  - [ ] Touch-friendly buttons

### Phase 12 : Tests et Déploiement

- [ ] **Tests frontend**
  - [ ] Jest (tests unitaires)
  - [ ] React Testing Library
  - [ ] E2E (Cypress, Selenium)

- [ ] **Build et optimisation**
  - [ ] Production build
  - [ ] Minification
  - [ ] Compression images
  - [ ] Bundle analysis

- [ ] **Déploiement**
  - [ ] Vercel / Netlify
  - [ ] GitHub Pages
  - [ ] Serveur custom

---

## 📊 Priorités

### Must-Have (Obligatoire)
- ✅ Authentification (login/register)
- ✅ Catalogue produits
- ✅ Panier
- ✅ Commande *(sans intégration paiement — PDF)*
- ✅ Interface admin basique (CRUD produits)

### Should-Have (Important)
- ✅ Avis/évaluations
- ✅ Historique commandes
- ✅ Recherche/filtres
- ✅ Gestion utilisateurs (admin)
- ✅ Dashboard (stats)

### Nice-to-Have (Optionnel)
- ✅ Wishlist
- ✅ Notifications email
- ✅ Chat support
- ✅ Gamification (points, badges)
- ✅ Recommandations produits

---

## 🔗 Intégrations Externes

- [ ] **Email** : SendGrid / Gmail SMTP *(optionnel)*
- [ ] **Stockage images** : AWS S3 / Firebase Storage *(optionnel)*
- [ ] **Analytics** : Google Analytics *(optionnel)*
- [ ] **Monitoring** : Sentry *(optionnel)*

> **PDF :** Aucune intégration paiement (Stripe/PayPal) demandée.

---

## 📝 Notes Importantes

- **Branche Backend** : `backend` (pour vos développements backend)
- **Branche Frontend** : `frontend` (pour vos développements frontend)
- **Branche Main** : Pour la production stable
- **Commits réguliers** avec messages clairs
- **Documentation à jour** au fur et à mesure

**Fonctionnalités avancées PDF (2 à 5 au choix)** : Swagger/OpenAPI, **Docker** (Dockerfile), **Tests unitaires** (JUnit, Mockito), **Logs structurés** (connexion, commandes), **Design amélioré** (CSS, UX).

---

## 🎯 Dates Clés

- **Soutenance** : vendredi 30/01/2025 — *PDF : 20 min présentation + 15 min questions*
- **Livraison attendue** : Version stable et fonctionnelle

---

Bonne chance avec le développement ! 🚀
