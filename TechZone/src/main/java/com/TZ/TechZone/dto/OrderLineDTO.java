package com.TZ.TechZone.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineDTO {
    private Integer id;
    
    @NotNull(message = "Un produit doit être assigné")
    private Integer productId;
    
    private ProductDTO product;
    
    @NotNull(message = "La quantité ne peut pas être null")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantity;
    
    @NotNull(message = "Le prix unitaire ne peut pas être null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix unitaire ne peut pas être négatif")
    private BigDecimal unitPrice;
    
    @NotNull(message = "Le total de la ligne ne peut pas être null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le total de la ligne ne peut pas être négatif")
    private BigDecimal lineTotal;
}
