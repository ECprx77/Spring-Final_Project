package com.TZ.TechZone.services;

import com.TZ.TechZone.dto.ProductDTO;
import com.TZ.TechZone.entities.Category;
import com.TZ.TechZone.entities.Product;
import com.TZ.TechZone.exceptions.ResourceNotFoundException;
import com.TZ.TechZone.repositories.CategoryRepository;
import com.TZ.TechZone.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service métier pour la gestion des produits
 */
@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Crée un nouveau produit
     */
    public ProductDTO createProduct(ProductDTO productDTO) {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + productDTO.getCategoryId()));

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStatus(Product.ProductStatus.valueOf(productDTO.getStatus() != null ? productDTO.getStatus() : "en_stock"));
        product.setIsPromo(productDTO.getIsPromo() != null ? productDTO.getIsPromo() : false);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    /**
     * Récupère un produit par son ID
     */
    public ProductDTO getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID: " + id));
        return convertToDTO(product);
    }

    /**
     * Récupère tous les produits avec pagination (pour clients)
     */
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    /**
     * Récupère les produits d'une catégorie avec pagination
     */
    public Page<ProductDTO> getProductsByCategory(Integer categoryId, Pageable pageable) {
        // Vérifier que la catégorie existe
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + categoryId);
        }

        return productRepository.findByCategory_Id(categoryId, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Recherche des produits par nom ou description
     */
    public Page<ProductDTO> searchProducts(String query, Pageable pageable) {
        return productRepository.searchByNameOrDescription(query, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Récupère les produits en promotion
     */
    public List<ProductDTO> getPromoProducts() {
        return productRepository.findByIsPromoTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Met à jour un produit
     */
    public ProductDTO updateProduct(Integer id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID: " + id));

        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + productDTO.getCategoryId()));
            product.setCategory(category);
        }

        if (productDTO.getName() != null) {
            product.setName(productDTO.getName());
        }
        if (productDTO.getDescription() != null) {
            product.setDescription(productDTO.getDescription());
        }
        if (productDTO.getPrice() != null) {
            product.setPrice(productDTO.getPrice());
        }
        if (productDTO.getStatus() != null) {
            product.setStatus(Product.ProductStatus.valueOf(productDTO.getStatus()));
        }
        if (productDTO.getIsPromo() != null) {
            product.setIsPromo(productDTO.getIsPromo());
        }

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    /**
     * Supprime un produit
     */
    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produit non trouvé avec l'ID: " + id);
        }
        productRepository.deleteById(id);
    }

    /**
     * Convertit une entité Product en ProductDTO
     */
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStatus(product.getStatus().toString());
        dto.setIsPromo(product.getIsPromo());
        dto.setCategoryId(product.getCategory().getId());

        // Convertir la catégorie en DTO
        com.TZ.TechZone.dto.CategoryDTO categoryDTO = new com.TZ.TechZone.dto.CategoryDTO();
        categoryDTO.setId(product.getCategory().getId());
        categoryDTO.setName(product.getCategory().getName());
        categoryDTO.setDescription(product.getCategory().getDescription());
        dto.setCategory(categoryDTO);

        dto.setCreatedAt(product.getCreatedAt());
        return dto;
    }
}
