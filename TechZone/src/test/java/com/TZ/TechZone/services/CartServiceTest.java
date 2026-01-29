package com.TZ.TechZone.services;

import com.TZ.TechZone.dto.CartDTO;
import com.TZ.TechZone.entities.Product;
import com.TZ.TechZone.exceptions.ResourceNotFoundException;
import com.TZ.TechZone.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(1);
        product1.setName("Laptop");
        product1.setPrice(new BigDecimal("999.99"));
        product1.setStatus(Product.ProductStatus.en_stock);

        product2 = new Product();
        product2.setId(2);
        product2.setName("Mouse");
        product2.setPrice(new BigDecimal("29.99"));
        product2.setStatus(Product.ProductStatus.en_stock);
    }

    @Test
    void addItem_whenNewProduct_addsSuccessfully() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));

        CartDTO cart = cartService.addItem(100, 1, 2);

        assertThat(cart).isNotNull();
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getProductId()).isEqualTo(1);
        assertThat(cart.getItems().get(0).getProductName()).isEqualTo("Laptop");
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(cart.getItems().get(0).getLineTotal()).isEqualByComparingTo(new BigDecimal("1999.98"));
        assertThat(cart.getTotal()).isEqualByComparingTo(new BigDecimal("1999.98"));
    }

    @Test
    void addItem_whenProductAlreadyInCart_incrementsQuantity() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));

        cartService.addItem(100, 1, 2);
        CartDTO cart = cartService.addItem(100, 1, 3);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(5);
        assertThat(cart.getItems().get(0).getLineTotal()).isEqualByComparingTo(new BigDecimal("4999.95"));
    }

    @Test
    void addItem_whenProductNotFound_throwsResourceNotFoundException() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addItem(100, 999, 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Produit non trouvé");
    }

    @Test
    void addItem_whenQuantityIsZero_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> cartService.addItem(100, 1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La quantité doit être au moins 1");
    }

    @Test
    void addItem_whenQuantityIsNegative_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> cartService.addItem(100, 1, -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La quantité doit être au moins 1");
    }

    @Test
    void addItem_whenMultipleProducts_calculatesCorrectTotal() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2)).thenReturn(Optional.of(product2));

        cartService.addItem(100, 1, 1);
        CartDTO cart = cartService.addItem(100, 2, 2);

        assertThat(cart.getItems()).hasSize(2);
        assertThat(cart.getTotal()).isEqualByComparingTo(new BigDecimal("1059.97"));
    }

    @Test
    void updateItemQuantity_whenItemExists_updatesSuccessfully() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        cartService.addItem(100, 1, 2);

        CartDTO cart = cartService.updateItemQuantity(100, 1, 5);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(5);
        assertThat(cart.getItems().get(0).getLineTotal()).isEqualByComparingTo(new BigDecimal("4999.95"));
    }

    @Test
    void updateItemQuantity_whenItemNotInCart_throwsResourceNotFoundException() {
        assertThatThrownBy(() -> cartService.updateItemQuantity(100, 1, 5))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Article non trouvé dans le panier");
    }

    @Test
    void updateItemQuantity_whenQuantityIsZero_throwsIllegalArgumentException() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        cartService.addItem(100, 1, 2);

        assertThatThrownBy(() -> cartService.updateItemQuantity(100, 1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La quantité doit être au moins 1");
    }

    @Test
    void removeItem_whenItemExists_removesSuccessfully() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2)).thenReturn(Optional.of(product2));
        cartService.addItem(100, 1, 1);
        cartService.addItem(100, 2, 1);

        CartDTO cart = cartService.removeItem(100, 1);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getProductId()).isEqualTo(2);
        assertThat(cart.getTotal()).isEqualByComparingTo(new BigDecimal("29.99"));
    }

    @Test
    void removeItem_whenItemNotInCart_throwsResourceNotFoundException() {
        assertThatThrownBy(() -> cartService.removeItem(100, 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Article non trouvé dans le panier");
    }

    @Test
    void clearCart_whenCartHasItems_clearsSuccessfully() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        cartService.addItem(100, 1, 2);

        cartService.clearCart(100);
        CartDTO cart = cartService.getCart(100);

        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void clearCart_whenCartEmpty_doesNotThrowException() {
        cartService.clearCart(100);
        CartDTO cart = cartService.getCart(100);

        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void getCart_whenEmpty_returnsEmptyCart() {
        CartDTO cart = cartService.getCart(100);

        assertThat(cart).isNotNull();
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getCart_whenHasItems_returnsCartWithCorrectData() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        cartService.addItem(100, 1, 3);

        CartDTO cart = cartService.getCart(100);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getTotal()).isEqualByComparingTo(new BigDecimal("2999.97"));
    }

    @Test
    void multipleCarts_whenDifferentUsers_areIndependent() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2)).thenReturn(Optional.of(product2));

        cartService.addItem(100, 1, 2);
        cartService.addItem(200, 2, 3);

        CartDTO cart1 = cartService.getCart(100);
        CartDTO cart2 = cartService.getCart(200);

        assertThat(cart1.getItems()).hasSize(1);
        assertThat(cart1.getItems().get(0).getProductId()).isEqualTo(1);
        assertThat(cart2.getItems()).hasSize(1);
        assertThat(cart2.getItems().get(0).getProductId()).isEqualTo(2);
    }
}
