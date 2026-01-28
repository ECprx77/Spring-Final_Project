package com.TZ.TechZone.services;

import com.TZ.TechZone.dto.OrderDTO;
import com.TZ.TechZone.dto.OrderLineDTO;
import com.TZ.TechZone.dto.ProductDTO;
import com.TZ.TechZone.entities.AuditLog;
import com.TZ.TechZone.entities.Order;
import com.TZ.TechZone.entities.OrderLine;
import com.TZ.TechZone.entities.Product;
import com.TZ.TechZone.entities.User;
import com.TZ.TechZone.exceptions.ResourceNotFoundException;
import com.TZ.TechZone.repositories.AuditLogRepository;
import com.TZ.TechZone.repositories.OrderLineRepository;
import com.TZ.TechZone.repositories.OrderRepository;
import com.TZ.TechZone.repositories.ProductRepository;
import com.TZ.TechZone.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service métier pour la gestion des commandes.
 */
@Service
@Transactional
public class OrderService {

    private static final Set<String> ALLOWED_STATUSES =
            Set.of("PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED");

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderLineRepository orderLineRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private CartService cartService;

    /**
     * Crée une commande à partir du panier courant de l'utilisateur.
     */
    public OrderDTO createOrderFromCart(Integer userId, String ipAddress, String userAgent) {
        var cart = cartService.getCart(userId);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Le panier est vide. Impossible de créer une commande.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));

        Order order = new Order();
        order.setUser(user);
        order.setTotal(cart.getTotal() != null ? cart.getTotal() : BigDecimal.ZERO);
        // Status initial aligné avec la roadmap (PENDING)
        order.setStatus("PENDING");

        // Créer les lignes de commande
        List<OrderLine> lines = cart.getItems().stream().map(item -> {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID: " + item.getProductId()));

            OrderLine line = new OrderLine();
            line.setOrder(order);
            line.setProduct(product);
            line.setQuantity(item.getQuantity());
            line.setUnitPrice(product.getPrice());
            line.setLineTotal(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            return line;
        }).collect(Collectors.toList());

        order.setOrderLines(lines);

        Order savedOrder = orderRepository.save(order);

        // Vider le panier une fois la commande créée
        cartService.clearCart(userId);

        // Log d'audit
        AuditLog log = new AuditLog();
        log.setAction(AuditLog.AuditAction.ORDER_CREATED);
        log.setEntityType(AuditLog.AuditEntity.ORDER);
        log.setEntityId(savedOrder.getId());
        log.setUser(user);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        auditLogRepository.save(log);

        return convertToDTO(savedOrder);
    }

    /**
     * Récupère les commandes d'un utilisateur (historique).
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> getUserOrders(Integer userId, Pageable pageable) {
        return orderRepository.findByUser_Id(userId, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Récupère le détail d'une commande pour un utilisateur donné (vérifie la propriété).
     */
    @Transactional(readOnly = true)
    public OrderDTO getUserOrderById(Integer userId, Integer orderId) {
        Order order = orderRepository.findByIdAndUser_Id(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée pour cet utilisateur avec l'ID: " + orderId));
        return convertToDTO(order);
    }

    /**
     * Annule une commande côté client si son statut le permet.
     */
    public OrderDTO cancelUserOrder(Integer userId, Integer orderId) {
        Order order = orderRepository.findByIdAndUser_Id(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée pour cet utilisateur avec l'ID: " + orderId));

        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalStateException("La commande est déjà annulée.");
        }

        order.setStatus("CANCELLED");
        Order saved = orderRepository.save(order);

        // Audit
        AuditLog log = new AuditLog();
        log.setAction(AuditLog.AuditAction.ORDER_CANCELLED);
        log.setEntityType(AuditLog.AuditEntity.ORDER);
        log.setEntityId(saved.getId());
        log.setUser(order.getUser());
        auditLogRepository.save(log);

        return convertToDTO(saved);
    }

    /**
     * Liste paginée de toutes les commandes (admin).
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    /**
     * Détail d'une commande spécifique (admin).
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'ID: " + orderId));
        return convertToDTO(order);
    }

    /**
     * Met à jour le statut d'une commande (admin).
     */
    public OrderDTO updateOrderStatus(Integer orderId, String newStatus) {
        if (newStatus == null || !ALLOWED_STATUSES.contains(newStatus)) {
            throw new IllegalArgumentException("Statut de commande invalide: " + newStatus);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'ID: " + orderId));

        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);

        if ("CANCELLED".equalsIgnoreCase(newStatus)) {
            AuditLog log = new AuditLog();
            log.setAction(AuditLog.AuditAction.ORDER_CANCELLED);
            log.setEntityType(AuditLog.AuditEntity.ORDER);
            log.setEntityId(saved.getId());
            log.setUser(order.getUser());
            auditLogRepository.save(log);
        } else if ("CONFIRMED".equalsIgnoreCase(newStatus) || "DELIVERED".equalsIgnoreCase(newStatus)) {
            AuditLog log = new AuditLog();
            log.setAction(AuditLog.AuditAction.ORDER_PAID);
            log.setEntityType(AuditLog.AuditEntity.ORDER);
            log.setEntityId(saved.getId());
            log.setUser(order.getUser());
            auditLogRepository.save(log);
        }

        return convertToDTO(saved);
    }

    // ========= Conversions =========

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotal(order.getTotal());

        User user = order.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            com.TZ.TechZone.dto.UserDTO userDTO = new com.TZ.TechZone.dto.UserDTO();
            userDTO.setId(user.getId());
            userDTO.setEmail(user.getEmail());
            userDTO.setFullName(user.getFullName());
            if (user.getRole() != null) {
                userDTO.setRole(new com.TZ.TechZone.dto.RoleDTO(user.getRole().getId(), user.getRole().getName()));
            }
            dto.setUser(userDTO);
        }

        if (order.getOrderLines() != null) {
            List<OrderLineDTO> lineDTOs = order.getOrderLines().stream()
                    .map(this::convertLineToDTO)
                    .collect(Collectors.toList());
            dto.setOrderLines(lineDTOs);
        }

        return dto;
    }

    private OrderLineDTO convertLineToDTO(OrderLine line) {
        OrderLineDTO dto = new OrderLineDTO();
        dto.setId(line.getId());
        dto.setQuantity(line.getQuantity());
        dto.setUnitPrice(line.getUnitPrice());
        dto.setLineTotal(line.getLineTotal());

        Product product = line.getProduct();
        if (product != null) {
            dto.setProductId(product.getId());
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(product.getId());
            productDTO.setName(product.getName());
            productDTO.setDescription(product.getDescription());
            productDTO.setPrice(product.getPrice());
            dto.setProduct(productDTO);
        }

        return dto;
    }
}

