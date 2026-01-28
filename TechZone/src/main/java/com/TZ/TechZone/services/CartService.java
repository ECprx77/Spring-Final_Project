package com.TZ.TechZone.services;

import com.TZ.TechZone.dto.CartDTO;
import com.TZ.TechZone.dto.CartItemDTO;
import com.TZ.TechZone.entities.Product;
import com.TZ.TechZone.exceptions.ResourceNotFoundException;
import com.TZ.TechZone.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service de gestion du panier.
 *
 * Implémentation simple en mémoire, indexée par identifiant utilisateur.
 * Cela simule une "session" côté backend tout en restant compatible avec JWT stateless.
 */
@Service
@Transactional
public class CartService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Map des paniers par identifiant utilisateur.
     * userId -> (productId -> CartItemDTO)
     */
    private final Map<Integer, Map<Integer, CartItemDTO>> carts = new HashMap<>();

    /**
     * Ajoute un produit au panier (ou incrémente la quantité).
     */
    public CartDTO addItem(Integer userId, Integer productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité doit être au moins 1");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID: " + productId));

        Map<Integer, CartItemDTO> userCart = carts.computeIfAbsent(userId, k -> new HashMap<>());

        CartItemDTO item = userCart.get(productId);
        if (item == null) {
            item = new CartItemDTO();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(quantity);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }

        item.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        userCart.put(productId, item);

        return buildCartDTO(userCart);
    }

    /**
     * Met à jour la quantité d'un article du panier.
     */
    public CartDTO updateItemQuantity(Integer userId, Integer productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité doit être au moins 1");
        }

        Map<Integer, CartItemDTO> userCart = carts.get(userId);
        if (userCart == null || !userCart.containsKey(productId)) {
            throw new ResourceNotFoundException("Article non trouvé dans le panier pour le produit ID: " + productId);
        }

        CartItemDTO item = userCart.get(productId);
        item.setQuantity(quantity);
        item.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));

        return buildCartDTO(userCart);
    }

    /**
     * Supprime un article du panier.
     */
    public CartDTO removeItem(Integer userId, Integer productId) {
        Map<Integer, CartItemDTO> userCart = carts.get(userId);
        if (userCart == null || !userCart.containsKey(productId)) {
            throw new ResourceNotFoundException("Article non trouvé dans le panier pour le produit ID: " + productId);
        }

        userCart.remove(productId);
        return buildCartDTO(userCart);
    }

    /**
     * Vide complètement le panier de l'utilisateur.
     */
    public void clearCart(Integer userId) {
        Map<Integer, CartItemDTO> userCart = carts.get(userId);
        if (userCart != null) {
            userCart.clear();
        }
    }

    /**
     * Récupère le panier de l'utilisateur.
     */
    public CartDTO getCart(Integer userId) {
        Map<Integer, CartItemDTO> userCart = carts.getOrDefault(userId, new HashMap<>());
        return buildCartDTO(userCart);
    }

    /**
     * Construit un CartDTO à partir de la map d'articles.
     */
    private CartDTO buildCartDTO(Map<Integer, CartItemDTO> userCart) {
        List<CartItemDTO> items = new ArrayList<>(userCart.values());
        BigDecimal total = items.stream()
                .map(CartItemDTO::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartDTO(items, total);
    }
}

