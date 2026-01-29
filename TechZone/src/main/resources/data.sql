-- ============================================
-- TechZone - Test Data Initialization
-- ============================================
-- This file is automatically loaded after schema.sql
-- Passwords are BCrypt hashed (all passwords: "password")
-- ============================================

-- ============================================
-- USERS (2 ADMIN + 3 USER = 5 total)
-- ============================================
-- NOTE: Users are NOT pre-loaded to avoid password hash issues
-- But you can run the setup-test-users.sh script to do the following steps automatically :
-- Register users via /api/auth/register on first startup:
--
-- ADMIN accounts to create:
-- 1. Email: admin@techzone.com | Password: Admin123! | Then update role_id to 1 in H2 console
-- 2. Email: admin2@techzone.com | Password: Admin123! | Then update role_id to 1 in H2 console
--
-- USER accounts to create:
-- 3. Email: user1@techzone.com | Password: User123!
-- 4. Email: user2@techzone.com | Password: User123!
-- 5. Email: user3@techzone.com | Password: User123!
--
-- To make a user ADMIN after registration:
-- 1. Access H2 Console: http://localhost:8080/api/h2-console
-- 2. Run: UPDATE users SET role_id = 1 WHERE email = 'admin@techzone.com';

-- ============================================
-- CATEGORIES
-- ============================================
INSERT INTO categories (name, description) VALUES 
('Électronique', 'Smartphones, tablettes, accessoires électroniques'),
('Informatique', 'Ordinateurs, périphériques, composants PC'),
('Audio & Vidéo', 'Écouteurs, enceintes, casques audio, caméras'),
('Gaming', 'Consoles, jeux vidéo, accessoires gaming'),
('Maison Connectée', 'Domotique, objets connectés, smart home');

-- ============================================
-- PRODUCTS (15 produits répartis dans les catégories)
-- ============================================

-- Catégorie Électronique (5 produits)
INSERT INTO products (name, description, price, status, is_promo, category_id, created_at) VALUES
('iPhone 15 Pro', 'Smartphone Apple dernière génération avec puce A17 Pro', 1199.99, 'en_stock', true, 1, CURRENT_TIMESTAMP),
('Samsung Galaxy S24 Ultra', 'Flagship Android avec stylet S Pen intégré', 1299.99, 'en_stock', false, 1, CURRENT_TIMESTAMP),
('iPad Air M2', 'Tablette Apple avec puce M2 et écran Liquid Retina', 699.99, 'en_stock', true, 1, CURRENT_TIMESTAMP),
('Google Pixel 8 Pro', 'Smartphone Google avec IA avancée et photo exceptionnelle', 999.99, 'en_stock', false, 1, CURRENT_TIMESTAMP),
('OnePlus 12', 'Smartphone haut de gamme avec charge rapide 100W', 849.99, 'en_rupture', false, 1, CURRENT_TIMESTAMP);

-- Catégorie Informatique (4 produits)
INSERT INTO products (name, description, price, status, is_promo, category_id, created_at) VALUES
('MacBook Pro 16" M3', 'Ordinateur portable professionnel Apple avec puce M3 Max', 2899.99, 'en_stock', false, 2, CURRENT_TIMESTAMP),
('Dell XPS 15', 'PC portable premium avec écran OLED 4K', 1899.99, 'en_stock', true, 2, CURRENT_TIMESTAMP),
('Logitech MX Master 3S', 'Souris ergonomique sans fil pour professionnels', 109.99, 'en_stock', false, 2, CURRENT_TIMESTAMP),
('Keychron K8 Pro', 'Clavier mécanique sans fil QMK/VIA programmable', 119.99, 'en_stock', false, 2, CURRENT_TIMESTAMP);

-- Catégorie Audio & Vidéo (3 produits)
INSERT INTO products (name, description, price, status, is_promo, category_id, created_at) VALUES
('Sony WH-1000XM5', 'Casque audio avec réduction de bruit active premium', 399.99, 'en_stock', true, 3, CURRENT_TIMESTAMP),
('AirPods Pro (2ème génération)', 'Écouteurs Apple avec réduction de bruit et audio spatial', 279.99, 'en_stock', false, 3, CURRENT_TIMESTAMP),
('JBL Flip 6', 'Enceinte Bluetooth portable étanche IP67', 129.99, 'en_stock', false, 3, CURRENT_TIMESTAMP);

-- Catégorie Gaming (2 produits)
INSERT INTO products (name, description, price, status, is_promo, category_id, created_at) VALUES
('PlayStation 5', 'Console de jeu nouvelle génération Sony', 549.99, 'en_stock', false, 4, CURRENT_TIMESTAMP),
('Xbox Series X', 'Console Microsoft 4K avec SSD ultra-rapide', 499.99, 'en_stock', true, 4, CURRENT_TIMESTAMP);

-- Catégorie Maison Connectée (1 produit)
INSERT INTO products (name, description, price, status, is_promo, category_id, created_at) VALUES
('Amazon Echo Dot (5ème gen)', 'Enceinte intelligente Alexa compacte', 59.99, 'en_stock', false, 5, CURRENT_TIMESTAMP);

-- ============================================
-- ============================================
-- PRODUCT IMAGES (exemples pour quelques produits)
-- ============================================
-- Using placeholder images from picsum.photos (free service with random images)
-- These will work immediately without needing to upload files
--
-- ALTERNATIVES FOR PRODUCTION:
-- 1. Local files: '/uploads/products/filename.jpg' (upload via API or place manually)
-- 2. CDN URLs: 'https://your-cdn.com/images/product.jpg'
-- 3. Placeholder services: 'https://via.placeholder.com/400x400/007bff/ffffff?text=Product+Name'
-- 4. Use the API: POST /api/admin/products/{id}/images (requires ADMIN token)
--
INSERT INTO product_images (product_id, image_url, is_primary, display_order, created_at) VALUES
-- iPhone 15 Pro (product_id=1) - 2 images
(1, 'https://picsum.photos/seed/iphone15pro1/400/400', true, 1, CURRENT_TIMESTAMP),
(1, 'https://picsum.photos/seed/iphone15pro2/400/400', false, 2, CURRENT_TIMESTAMP),
-- Samsung Galaxy S24 Ultra (product_id=2)
(2, 'https://picsum.photos/seed/galaxys24/400/400', true, 1, CURRENT_TIMESTAMP),
-- iPad Air M2 (product_id=3)
(3, 'https://picsum.photos/seed/ipadair/400/400', true, 1, CURRENT_TIMESTAMP),
-- Google Pixel 8 Pro (product_id=4)
(4, 'https://picsum.photos/seed/pixel8pro/400/400', true, 1, CURRENT_TIMESTAMP),
-- OnePlus 12 (product_id=5)
(5, 'https://picsum.photos/seed/oneplus12/400/400', true, 1, CURRENT_TIMESTAMP),
-- MacBook Pro M3 (product_id=6)
(6, 'https://picsum.photos/seed/macbookpro/400/400', true, 1, CURRENT_TIMESTAMP),
-- Dell XPS 15 (product_id=7)
(7, 'https://picsum.photos/seed/dellxps15/400/400', true, 1, CURRENT_TIMESTAMP),
-- Logitech MX Master 3S (product_id=8)
(8, 'https://picsum.photos/seed/logitechmx/400/400', true, 1, CURRENT_TIMESTAMP),
-- Keychron K8 Pro (product_id=9)
(9, 'https://picsum.photos/seed/keychron/400/400', true, 1, CURRENT_TIMESTAMP),
-- Sony WH-1000XM5 (product_id=10)
(10, 'https://picsum.photos/seed/sonywh1000xm5/400/400', true, 1, CURRENT_TIMESTAMP),
-- AirPods Pro (product_id=11)
(11, 'https://picsum.photos/seed/airpodspro/400/400', true, 1, CURRENT_TIMESTAMP),
-- JBL Flip 6 (product_id=12)
(12, 'https://picsum.photos/seed/jblflip6/400/400', true, 1, CURRENT_TIMESTAMP),
-- PlayStation 5 (product_id=13)
(13, 'https://picsum.photos/seed/ps5/400/400', true, 1, CURRENT_TIMESTAMP),
-- Xbox Series X (product_id=14)
(14, 'https://picsum.photos/seed/xbox/400/400', true, 1, CURRENT_TIMESTAMP),
-- Amazon Echo Dot (product_id=15)
(15, 'https://picsum.photos/seed/echodot/400/400', true, 1, CURRENT_TIMESTAMP);

-- ============================================
-- SAMPLE ORDERS (2 commandes pour tester)
-- ============================================
-- NOTE: These are commented out because users are not pre-loaded
-- After creating users via registration, you can manually create orders via the API
-- or uncomment these and update user_id values to match your registered users

-- Example: Commande 1 - 2 produits (iPhone + Logitech Mouse)
-- INSERT INTO orders (user_id, status, total, order_date) VALUES (1, 'PENDING', 1309.98, CURRENT_TIMESTAMP);
-- INSERT INTO order_lines (order_id, product_id, quantity, unit_price, line_total) VALUES
-- (1, 1, 1, 1199.99, 1199.99),
-- (1, 8, 1, 109.99, 109.99);

-- Example: Commande 2 - 3 produits (iPad + Echo Dot + AirPods)
-- INSERT INTO orders (user_id, status, total, order_date) VALUES (2, 'CONFIRMED', 1039.97, CURRENT_TIMESTAMP);
-- INSERT INTO order_lines (order_id, product_id, quantity, unit_price, line_total) VALUES
-- (2, 3, 1, 699.99, 699.99),
-- (2, 15, 1, 59.99, 59.99),
-- (2, 11, 1, 279.99, 279.99);

-- ============================================
-- AUDIT LOGS (quelques exemples)
-- ============================================
-- NOTE: Commented out since users are not pre-loaded
-- After creating users, audit logs will be generated automatically by the application

-- Example audit log entries (uncomment and update user_id/entity_id after user registration):
-- INSERT INTO audit_logs (action, entity_type, entity_id, user_id, ip_address, user_agent, created_at) VALUES
-- ('LOGIN_SUCCESS', 'USER', 1, 1, '192.168.1.10', 'Mozilla/5.0', CURRENT_TIMESTAMP),
-- ('ORDER_CREATED', 'ORDER', 1, 1, '192.168.1.10', 'Mozilla/5.0', CURRENT_TIMESTAMP);

-- ============================================
-- End of data initialization
-- ============================================
