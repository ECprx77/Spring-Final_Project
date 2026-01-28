package com.TZ.TechZone.controllers;

import com.TZ.TechZone.dto.CartDTO;
import com.TZ.TechZone.security.UserPrincipal;
import com.TZ.TechZone.services.CartService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour la gestion du panier.
 * Endpoints: /api/cart/**
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * Ajoute un produit au panier.
     * POST /api/cart/add
     */
    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(@RequestBody AddToCartRequest request) {
        Integer userId = getCurrentUserId();
        CartDTO cart = cartService.addItem(userId, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(cart);
    }

    /**
     * Met à jour la quantité d'un article du panier.
     * PUT /api/cart/{productId}
     */
    @PutMapping("/{productId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable Integer productId,
            @RequestBody UpdateQuantityRequest request) {

        Integer userId = getCurrentUserId();
        CartDTO cart = cartService.updateItemQuantity(userId, productId, request.getQuantity());
        return ResponseEntity.ok(cart);
    }

    /**
     * Supprime un article du panier.
     * DELETE /api/cart/{productId}
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<CartDTO> removeFromCart(@PathVariable Integer productId) {
        Integer userId = getCurrentUserId();
        CartDTO cart = cartService.removeItem(userId, productId);
        return ResponseEntity.ok(cart);
    }

    /**
     * Vide complètement le panier.
     * DELETE /api/cart
     */
    @DeleteMapping
    public ResponseEntity<?> clearCart() {
        Integer userId = getCurrentUserId();
        cartService.clearCart(userId);
        return ResponseEntity.ok(new AuthController.ApiResponse(true, "Panier vidé avec succès"));
    }

    /**
     * Récupère le panier courant.
     * GET /api/cart
     */
    @GetMapping
    public ResponseEntity<CartDTO> getCart() {
        Integer userId = getCurrentUserId();
        CartDTO cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    /**
     * Récupère l'identifiant de l'utilisateur courant depuis le contexte de sécurité.
     */
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication != null ? authentication.getPrincipal() : null;

        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }

        throw new IllegalStateException("Utilisateur non authentifié");
    }

    /**
     * Payload pour l'ajout au panier.
     */
    public static class AddToCartRequest {

        @NotNull(message = "L'identifiant du produit est requis")
        private Integer productId;

        @Min(value = 1, message = "La quantité doit être au moins 1")
        private int quantity = 1;

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    /**
     * Payload pour la mise à jour de quantité.
     */
    public static class UpdateQuantityRequest {

        @Min(value = 1, message = "La quantité doit être au moins 1")
        private int quantity;

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}

