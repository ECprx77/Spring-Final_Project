package com.TZ.TechZone.services;

import com.TZ.TechZone.dto.ProductImageDTO;
import com.TZ.TechZone.entities.Product;
import com.TZ.TechZone.entities.ProductImage;
import com.TZ.TechZone.exceptions.ResourceNotFoundException;
import com.TZ.TechZone.repositories.ProductImageRepository;
import com.TZ.TechZone.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    @Transactional
    public ProductImageDTO uploadImage(Integer productId, MultipartFile file, Boolean isPrimary) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit avec l'ID " + productId + " non trouvé"));

        String filename = fileStorageService.storeFile(file);
        String imageUrl = contextPath + "/uploads/products/" + filename;

        if (isPrimary != null && isPrimary) {
            productImageRepository.resetPrimaryForProduct(productId);
        }

        List<ProductImage> existingImages = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        int nextOrder = existingImages.isEmpty() ? 0 : existingImages.size();

        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageUrl(imageUrl);
        productImage.setIsPrimary(isPrimary != null ? isPrimary : existingImages.isEmpty());
        productImage.setDisplayOrder(nextOrder);

        ProductImage saved = productImageRepository.save(productImage);
        return toDTO(saved);
    }

    public List<ProductImageDTO> getProductImages(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Produit avec l'ID " + productId + " non trouvé");
        }
        return productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteImage(Integer imageId) throws IOException {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image avec l'ID " + imageId + " non trouvée"));

        String filename = extractFilenameFromUrl(image.getImageUrl());
        fileStorageService.deleteFile(filename);
        
        productImageRepository.delete(image);
    }

    /**
     * Supprime toutes les images d'un produit (fichiers + en base).
     * Utile pour remplacer l'image principale lors d'une modification.
     */
    @Transactional
    public void deleteAllImagesForProduct(Integer productId) throws IOException {
        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        for (ProductImage image : images) {
            String filename = extractFilenameFromUrl(image.getImageUrl());
            try {
                fileStorageService.deleteFile(filename);
            } catch (IOException ignored) {
                // Fichier déjà absent ou autre erreur, on continue la suppression en base
            }
            productImageRepository.delete(image);
        }
    }

    @Transactional
    public ProductImageDTO setPrimaryImage(Integer imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image avec l'ID " + imageId + " non trouvée"));

        productImageRepository.resetPrimaryForProduct(image.getProduct().getId());
        image.setIsPrimary(true);
        ProductImage updated = productImageRepository.save(image);
        return toDTO(updated);
    }

    private ProductImageDTO toDTO(ProductImage image) {
        ProductImageDTO dto = new ProductImageDTO();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setIsPrimary(image.getIsPrimary());
        dto.setDisplayOrder(image.getDisplayOrder());
        dto.setCreatedAt(image.getCreatedAt());
        return dto;
    }

    private String extractFilenameFromUrl(String url) {
        int lastSlash = url.lastIndexOf('/');
        return lastSlash != -1 ? url.substring(lastSlash + 1) : url;
    }
}
