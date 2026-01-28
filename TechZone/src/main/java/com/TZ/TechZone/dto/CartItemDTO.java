package com.TZ.TechZone.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Représente un article dans le panier côté API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {

    /**
     * Identifiant du produit (utilisé aussi comme identifiant de ligne dans le panier).
     */
    @NotNull(message = "L'identifiant du produit est requis")
    private Integer productId;

    private String productName;

    @NotNull(message = "Le prix du produit est requis")
    private BigDecimal unitPrice;

    @NotNull(message = "La quantité est requise")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantity;

    /**
     * Total de cette ligne (unitPrice * quantity).
     */
    private BigDecimal lineTotal;
}

