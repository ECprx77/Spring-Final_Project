package com.TZ.TechZone.services;

import com.TZ.TechZone.dto.ProductDTO;
import com.TZ.TechZone.entities.Category;
import com.TZ.TechZone.entities.Product;
import com.TZ.TechZone.exceptions.ResourceNotFoundException;
import com.TZ.TechZone.repositories.CategoryRepository;
import com.TZ.TechZone.repositories.ProductImageRepository;
import com.TZ.TechZone.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1);
        category.setName("Test Category");
        category.setDescription("Description");

        product = new Product();
        product.setId(1);
        product.setName("Test Product");
        product.setDescription("Description");
        product.setPrice(new BigDecimal("29.99"));
        product.setStatus(Product.ProductStatus.en_stock);
        product.setIsPromo(false);
        product.setCategory(category);
    }

    @Test
    void getProductById_whenProductExists_returnsProductDTO() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productImageRepository.findByProductIdOrderByDisplayOrderAsc(1)).thenReturn(Collections.emptyList());

        ProductDTO result = productService.getProductById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("29.99"));
        assertThat(result.getStatus()).isEqualTo("en_stock");
        assertThat(result.getCategoryId()).isEqualTo(1);
    }

    @Test
    void getProductById_whenProductNotExists_throwsResourceNotFoundException() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Produit non trouvé");
    }

    @Test
    void getAllProducts_returnsPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(product)));
        when(productImageRepository.findByProductIdOrderByDisplayOrderAsc(1)).thenReturn(Collections.emptyList());

        Page<ProductDTO> result = productService.getAllProducts(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Product");
    }

    @Test
    void getProductsByCategory_whenCategoryExists_returnsPage() {
        when(categoryRepository.existsById(1)).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findByCategory_Id(1, pageable)).thenReturn(new PageImpl<>(List.of(product)));
        when(productImageRepository.findByProductIdOrderByDisplayOrderAsc(1)).thenReturn(Collections.emptyList());

        Page<ProductDTO> result = productService.getProductsByCategory(1, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getProductsByCategory_whenCategoryNotExists_throwsResourceNotFoundException() {
        when(categoryRepository.existsById(999)).thenReturn(false);

        assertThatThrownBy(() -> productService.getProductsByCategory(999, PageRequest.of(0, 10)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Catégorie non trouvée");
    }

    @Test
    void deleteProduct_whenProductExists_deletesSuccessfully() {
        when(productRepository.existsById(1)).thenReturn(true);

        productService.deleteProduct(1);

        verify(productRepository).deleteById(1);
    }

    @Test
    void deleteProduct_whenProductNotExists_throwsResourceNotFoundException() {
        when(productRepository.existsById(999)).thenReturn(false);

        assertThatThrownBy(() -> productService.deleteProduct(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Produit non trouvé");
    }

    @Test
    void searchProducts_returnsMatchingPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.searchByNameOrDescription("laptop", pageable)).thenReturn(new PageImpl<>(List.of(product)));
        when(productImageRepository.findByProductIdOrderByDisplayOrderAsc(1)).thenReturn(Collections.emptyList());

        Page<ProductDTO> result = productService.searchProducts("laptop", pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }
}
