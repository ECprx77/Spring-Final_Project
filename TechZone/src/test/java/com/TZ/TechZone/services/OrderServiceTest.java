package com.TZ.TechZone.services;

import com.TZ.TechZone.dto.CartDTO;
import com.TZ.TechZone.dto.CartItemDTO;
import com.TZ.TechZone.dto.OrderDTO;
import com.TZ.TechZone.entities.*;
import com.TZ.TechZone.exceptions.ResourceNotFoundException;
import com.TZ.TechZone.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderLineRepository orderLineRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product;
    private Order order;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(2);
        role.setName("USER");

        user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setRole(role);

        product = new Product();
        product.setId(1);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("999.99"));
        product.setStatus(Product.ProductStatus.en_stock);

        order = new Order();
        order.setId(1);
        order.setUser(user);
        order.setStatus("PENDING");
        order.setTotal(new BigDecimal("999.99"));

        OrderLine line = new OrderLine();
        line.setId(1);
        line.setOrder(order);
        line.setProduct(product);
        line.setQuantity(1);
        line.setUnitPrice(new BigDecimal("999.99"));
        line.setLineTotal(new BigDecimal("999.99"));
        order.setOrderLines(List.of(line));
    }

    @Test
    void createOrderFromCart_withValidCart_createsOrderSuccessfully() {
        CartItemDTO cartItem = new CartItemDTO();
        cartItem.setProductId(1);
        cartItem.setProductName("Laptop");
        cartItem.setQuantity(1);
        cartItem.setUnitPrice(new BigDecimal("999.99"));
        cartItem.setLineTotal(new BigDecimal("999.99"));

        CartDTO cart = new CartDTO(List.of(cartItem), new BigDecimal("999.99"));

        when(cartService.getCart(1)).thenReturn(cart);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDTO result = orderService.createOrderFromCart(1, "127.0.0.1", "Test-Agent");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getTotal()).isEqualByComparingTo(new BigDecimal("999.99"));
        verify(cartService).clearCart(1);
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void createOrderFromCart_whenCartEmpty_throwsIllegalArgumentException() {
        CartDTO emptyCart = new CartDTO(Collections.emptyList(), BigDecimal.ZERO);
        when(cartService.getCart(1)).thenReturn(emptyCart);

        assertThatThrownBy(() -> orderService.createOrderFromCart(1, "127.0.0.1", "Test-Agent"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Le panier est vide");
    }

    @Test
    void createOrderFromCart_whenUserNotFound_throwsResourceNotFoundException() {
        CartItemDTO cartItem = new CartItemDTO();
        cartItem.setProductId(1);
        cartItem.setQuantity(1);
        CartDTO cart = new CartDTO(List.of(cartItem), new BigDecimal("999.99"));

        when(cartService.getCart(999)).thenReturn(cart);
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrderFromCart(999, "127.0.0.1", "Test-Agent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Utilisateur non trouvé");
    }

    @Test
    void createOrderFromCart_whenProductNotFound_throwsResourceNotFoundException() {
        CartItemDTO cartItem = new CartItemDTO();
        cartItem.setProductId(999);
        cartItem.setQuantity(1);
        CartDTO cart = new CartDTO(List.of(cartItem), new BigDecimal("999.99"));

        when(cartService.getCart(1)).thenReturn(cart);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrderFromCart(1, "127.0.0.1", "Test-Agent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Produit non trouvé");
    }

    @Test
    void createOrderFromCart_logsAuditAction() {
        CartItemDTO cartItem = new CartItemDTO();
        cartItem.setProductId(1);
        cartItem.setQuantity(1);
        cartItem.setUnitPrice(new BigDecimal("999.99"));
        cartItem.setLineTotal(new BigDecimal("999.99"));
        CartDTO cart = new CartDTO(List.of(cartItem), new BigDecimal("999.99"));

        when(cartService.getCart(1)).thenReturn(cart);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.createOrderFromCart(1, "127.0.0.1", "Test-Agent");

        ArgumentCaptor<AuditLog> logCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(logCaptor.capture());

        AuditLog capturedLog = logCaptor.getValue();
        assertThat(capturedLog.getAction()).isEqualTo(AuditLog.AuditAction.ORDER_CREATED);
        assertThat(capturedLog.getEntityType()).isEqualTo(AuditLog.AuditEntity.ORDER);
        assertThat(capturedLog.getIpAddress()).isEqualTo("127.0.0.1");
    }

    @Test
    void getUserOrders_returnsPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        when(orderRepository.findByUser_Id(1, pageable))
                .thenReturn(new PageImpl<>(List.of(order)));

        Page<OrderDTO> result = orderService.getUserOrders(1, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1);
    }

    @Test
    void getUserOrderById_whenExists_returnsOrder() {
        when(orderRepository.findByIdAndUser_Id(1, 1)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getUserOrderById(1, 1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getUserId()).isEqualTo(1);
    }

    @Test
    void getUserOrderById_whenNotFound_throwsResourceNotFoundException() {
        when(orderRepository.findByIdAndUser_Id(999, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getUserOrderById(1, 999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Commande non trouvée");
    }

    @Test
    void cancelUserOrder_whenValid_cancelsSuccessfully() {
        when(orderRepository.findByIdAndUser_Id(1, 1)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDTO result = orderService.cancelUserOrder(1, 1);

        assertThat(result).isNotNull();
        verify(orderRepository).save(any(Order.class));
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void cancelUserOrder_whenAlreadyCancelled_throwsIllegalStateException() {
        order.setStatus("CANCELLED");
        when(orderRepository.findByIdAndUser_Id(1, 1)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelUserOrder(1, 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("déjà annulée");
    }

    @Test
    void getAllOrders_returnsPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        when(orderRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(order)));

        Page<OrderDTO> result = orderService.getAllOrders(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getOrderById_whenExists_returnsOrder() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    void getOrderById_whenNotFound_throwsResourceNotFoundException() {
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Commande non trouvée");
    }

    @Test
    void updateOrderStatus_withValidStatus_updatesSuccessfully() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(1);
            return saved;
        });

        OrderDTO result = orderService.updateOrderStatus(1, "CONFIRMED");

        assertThat(result).isNotNull();
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_withInvalidStatus_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> orderService.updateOrderStatus(1, "INVALID_STATUS"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Statut de commande invalide");
    }

    @Test
    void updateOrderStatus_withNullStatus_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> orderService.updateOrderStatus(1, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Statut de commande invalide");
    }

    @Test
    void updateOrderStatus_toConfirmed_logsAuditAction() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.updateOrderStatus(1, "CONFIRMED");

        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void updateOrderStatus_toCancelled_logsAuditAction() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.updateOrderStatus(1, "CANCELLED");

        verify(auditLogRepository).save(any(AuditLog.class));
    }
}
