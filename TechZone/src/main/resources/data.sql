-- ============================================
-- TechZone - Test Data Initialization
-- ============================================
-- This file is automatically loaded after schema.sql
-- Passwords are BCrypt hashed (all passwords: "password")
-- ============================================

-- ============================================
-- USERS (2 ADMIN + 3 USER = 5 total)
-- ============================================
-- All ADMIN passwords: "Admin123!"
-- All USER passwords: "User123!"
-- BCrypt hashes generated with strength 10

-- ADMIN 1: admin@techzone.com / Admin123!
INSERT INTO users (email, password_hash, full_name, role_id, created_at) 
VALUES ('admin@techzone.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J4xJ4i8aN5qvz9dZHjP6Z9wJ.xK8qa', 'Admin Principal', 1, CURRENT_TIMESTAMP);

-- ADMIN 2: admin2@techzone.com / Admin123!
INSERT INTO users (email, password_hash, full_name, role_id, created_at) 
VALUES ('admin2@techzone.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J4xJ4i8aN5qvz9dZHjP6Z9wJ.xK8qa', 'Admin Secondaire', 1, CURRENT_TIMESTAMP);

-- USER 1: user1@techzone.com / User123!
INSERT INTO users (email, password_hash, full_name, role_id, created_at) 
VALUES ('user1@techzone.com', '$2a$10$9Y8hJEVXq7jmQe/SqK2u8e1VHZj0HjQ7xJ3vZ8hJ8eN9qo8uLOick', 'Jean Dupont', 2, CURRENT_TIMESTAMP);

-- USER 2: user2@techzone.com / User123!
INSERT INTO users (email, password_hash, full_name, role_id, created_at) 
VALUES ('user2@techzone.com', '$2a$10$9Y8hJEVXq7jmQe/SqK2u8e1VHZj0HjQ7xJ3vZ8hJ8eN9qo8uLOick', 'Marie Martin', 2, CURRENT_TIMESTAMP);

-- USER 3: user3@techzone.com / User123!
INSERT INTO users (email, password_hash, full_name, role_id, created_at) 
VALUES ('user3@techzone.com', '$2a$10$9Y8hJEVXq7jmQe/SqK2u8e1VHZj0HjQ7xJ3vZ8hJ8eN9qo8uLOick', 'Pierre Bernard', 2, CURRENT_TIMESTAMP);

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
-- PRODUCT IMAGES (exemples pour quelques produits)
-- ============================================
-- Note: Ces URLs sont des exemples. En production, utilisez de vraies images.
INSERT INTO product_images (product_id, image_url, is_primary, display_order, created_at) VALUES
(1, '/uploads/products/iphone-15-pro-1.jpg', true, 1, CURRENT_TIMESTAMP),
(1, '/uploads/products/iphone-15-pro-2.jpg', false, 2, CURRENT_TIMESTAMP),
(2, '/uploads/products/samsung-s24-ultra-1.jpg', true, 1, CURRENT_TIMESTAMP),
(3, '/uploads/products/ipad-air-m2-1.jpg', true, 1, CURRENT_TIMESTAMP),
(6, '/uploads/products/macbook-pro-m3-1.jpg', true, 1, CURRENT_TIMESTAMP),
(10, '/uploads/products/sony-wh1000xm5-1.jpg', true, 1, CURRENT_TIMESTAMP),
(13, '/uploads/products/ps5-1.jpg', true, 1, CURRENT_TIMESTAMP);

-- ============================================
-- SAMPLE ORDERS (2 commandes pour tester)
-- ============================================
-- Commande 1: Jean Dupont (user1) - 2 produits
INSERT INTO orders (user_id, status, total, order_date) VALUES (3, 'PENDING', 1309.98, CURRENT_TIMESTAMP);
INSERT INTO order_lines (order_id, product_id, quantity, unit_price, line_total) VALUES
(1, 1, 1, 1199.99, 1199.99),
(1, 8, 1, 109.99, 109.99);

-- Commande 2: Marie Martin (user2) - 3 produits  
INSERT INTO orders (user_id, status, total, order_date) VALUES (4, 'CONFIRMED', 729.97, CURRENT_TIMESTAMP);
INSERT INTO order_lines (order_id, product_id, quantity, unit_price, line_total) VALUES
(2, 3, 1, 699.99, 699.99),
(2, 15, 1, 59.99, 59.99),
(2, 11, 1, 279.99, 279.99);

-- ============================================
-- AUDIT LOGS (quelques exemples)
-- ============================================
INSERT INTO audit_logs (action, entity_type, entity_id, user_id, ip_address, user_agent, created_at) VALUES
('LOGIN_SUCCESS', 'USER', 3, 3, '192.168.1.10', 'Mozilla/5.0', CURRENT_TIMESTAMP),
('ORDER_CREATED', 'ORDER', 1, 3, '192.168.1.10', 'Mozilla/5.0', CURRENT_TIMESTAMP),
('LOGIN_SUCCESS', 'USER', 4, 4, '192.168.1.20', 'Mozilla/5.0', CURRENT_TIMESTAMP),
('ORDER_CREATED', 'ORDER', 2, 4, '192.168.1.20', 'Mozilla/5.0', CURRENT_TIMESTAMP);

-- ============================================
-- End of data initialization
-- ============================================
