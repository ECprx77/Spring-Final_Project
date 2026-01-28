package com.TZ.TechZone.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Integer id;
    
    @NotBlank(message = "Le nom du produit ne peut pas être vide")
    private String name;
    
    private String description;
    
    @NotNull(message = "Le prix ne peut pas être null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    private BigDecimal price;
    
    private String status;
    
    private Boolean isPromo;
    
    @NotNull(message = "Une catégorie doit être assignée")
    private Integer categoryId;
    
    private CategoryDTO category;
    
    private List<ProductImageDTO> images = new ArrayList<>();
    
    private LocalDateTime createdAt;
}
