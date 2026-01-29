package com.TZ.TechZone.controllers;

import com.TZ.TechZone.dto.OrderDTO;
import com.TZ.TechZone.security.UserPrincipal;
import com.TZ.TechZone.services.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour la gestion des commandes (Phase 6).
 *
 * Endpoints client:
 *  - POST /api/orders                (créer une commande depuis le panier)
 *  - GET  /api/orders                (historique utilisateur)
 *  - GET  /api/orders/{id}           (détails commande utilisateur)
 *  - PUT  /api/orders/{id}/cancel    (annuler sa propre commande)
 *
 * Endpoints admin:
 *  - GET  /api/admin/orders
 *  - PUT  /api/admin/orders/{id}/status
 */
@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // ========= Endpoints CLIENT =========

    /**
     * Crée une commande à partir du panier courant de l'utilisateur.
     * POST /api/orders
     */
    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> createOrder(HttpServletRequest request) {
        Integer userId = getCurrentUserId();
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        OrderDTO order = orderService.createOrderFromCart(userId, ipAddress, userAgent);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    /**
     * Récupère l'historique des commandes de l'utilisateur.
     * GET /api/orders?page=0&size=10&sort=orderDate,desc
     */
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderDTO>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Integer userId = getCurrentUserId();
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderDTO> orders = orderService.getUserOrders(userId, pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * Récupère le détail d'une commande pour l'utilisateur courant.
     * GET /api/orders/{id}
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> getUserOrderById(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        OrderDTO order = orderService.getUserOrderById(userId, id);
        return ResponseEntity.ok(order);
    }

    /**
     * Annule une commande (si possible) pour l'utilisateur courant.
     * PUT /api/orders/{id}/cancel
     */
    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<OrderDTO> cancelUserOrder(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        OrderDTO order = orderService.cancelUserOrder(userId, id);
        return ResponseEntity.ok(order);
    }

    // ========= Endpoints ADMIN =========

    /**
     * Liste toutes les commandes (admin).
     * GET /api/admin/orders?page=0&size=10&sort=orderDate,desc
     */
    @GetMapping("/admin/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderDTO> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * Détail d'une commande (admin).
     * GET /api/admin/orders/{id}
     */
    @GetMapping("/admin/orders/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> getOrderByIdAdmin(@PathVariable Integer id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Met à jour le statut d'une commande (admin).
     * PUT /api/admin/orders/{id}/status
     */
    @PutMapping("/admin/orders/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody UpdateStatusRequest request) {

        OrderDTO order = orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(order);
    }

    // ========= Utilitaires =========

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication != null ? authentication.getPrincipal() : null;

        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }

        throw new IllegalStateException("Utilisateur non authentifié");
    }

    /**
     * Payload pour la mise à jour de statut (admin).
     */
    public static class UpdateStatusRequest {

        @NotBlank(message = "Le statut est requis")
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}

