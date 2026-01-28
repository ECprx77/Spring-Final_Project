package com.TZ.TechZone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente l'état complet du panier pour un utilisateur.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private List<CartItemDTO> items = new ArrayList<>();

    /**
     * Total global du panier.
     */
    private BigDecimal total = BigDecimal.ZERO;
}

