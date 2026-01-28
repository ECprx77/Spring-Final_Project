package com.TZ.TechZone.controllers;

import com.TZ.TechZone.dto.ProductImageDTO;
import com.TZ.TechZone.services.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Product Images", description = "Gestion des images de produits")
public class ProductImageController {

    @Autowired
    private ProductImageService productImageService;

    @PostMapping("/{productId}/images")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload une image pour un produit", description = "Télécharge une image et l'associe à un produit (ADMIN uniquement)")
    public ResponseEntity<ProductImageDTO> uploadImage(
            @PathVariable Integer productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPrimary", required = false) Boolean isPrimary) throws IOException {
        ProductImageDTO image = productImageService.uploadImage(productId, file, isPrimary);
        return ResponseEntity.status(HttpStatus.CREATED).body(image);
    }

    @GetMapping("/{productId}/images")
    @Operation(summary = "Liste les images d'un produit", description = "Récupère toutes les images d'un produit triées par ordre d'affichage")
    public ResponseEntity<List<ProductImageDTO>> getProductImages(@PathVariable Integer productId) {
        List<ProductImageDTO> images = productImageService.getProductImages(productId);
        return ResponseEntity.ok(images);
    }

    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Supprime une image", description = "Supprime une image d'un produit (ADMIN uniquement)")
    public ResponseEntity<Void> deleteImage(@PathVariable Integer imageId) throws IOException {
        productImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/images/{imageId}/primary")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Définit l'image principale", description = "Définit cette image comme image principale du produit (ADMIN uniquement)")
    public ResponseEntity<ProductImageDTO> setPrimaryImage(@PathVariable Integer imageId) {
        ProductImageDTO image = productImageService.setPrimaryImage(imageId);
        return ResponseEntity.ok(image);
    }
}
