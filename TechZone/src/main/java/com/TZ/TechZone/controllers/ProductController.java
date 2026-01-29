package com.TZ.TechZone.controllers;

import com.TZ.TechZone.dto.ProductDTO;
import com.TZ.TechZone.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des produits
 * Endpoints: /api/products (public), /api/admin/products (admin)
 */
@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Récupère tous les produits avec pagination (public)
     * GET /api/products?page=0&size=10&sort=name,asc
     */
    @GetMapping("/products")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ProductDTO> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Récupère un produit par ID (public)
     * GET /api/products/{id}
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Integer id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Récupère les produits d'une catégorie (public)
     * GET /api/products/by-category/{categoryId}?page=0&size=10
     */
    @GetMapping("/products/by-category/{categoryId}")
    public ResponseEntity<Page<ProductDTO>> getProductsByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> products = productService.getProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Recherche des produits (public)
     * GET /api/products/search?q=laptop&page=0&size=10
     */
    @GetMapping("/products/search")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> products = productService.searchProducts(q, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Récupère les produits en promotion (public)
     * GET /api/products/promo
     */
    @GetMapping("/products/promo")
    public ResponseEntity<List<ProductDTO>> getPromoProducts() {
        List<ProductDTO> products = productService.getPromoProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Récupère les produits en stock avec pagination (public)
     * GET /api/products/in-stock?page=0&size=10&sort=name,asc
     */
    @GetMapping("/products/in-stock")
    public ResponseEntity<Page<ProductDTO>> getProductsInStock(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ProductDTO> products = productService.getProductsInStock(pageable);
        return ResponseEntity.ok(products);
    }

    // ========== ADMIN ENDPOINTS ==========

    /**
     * Crée un nouveau produit (admin)
     * POST /api/admin/products
     */
    @PostMapping("/admin/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    /**
     * Récupère tous les produits pour l'admin avec pagination (admin)
     * GET /api/admin/products?page=0&size=10&sort=name,asc
     */
    @GetMapping("/admin/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ProductDTO>> getAllProductsAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ProductDTO> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Récupère un produit par ID pour l'admin (admin)
     * GET /api/admin/products/{id}
     */
    @GetMapping("/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> getProductByIdAdmin(@PathVariable Integer id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Modifie un produit (admin)
     * PUT /api/admin/products/{id}
     */
    @PutMapping("/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Supprime un produit (admin)
     * DELETE /api/admin/products/{id}
     */
    @DeleteMapping("/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new com.TZ.TechZone.controllers.AuthController.ApiResponse(true, "Produit supprimé avec succès"));
    }
}
