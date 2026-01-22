package com.TZ.TechZone.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "Une commande doit être assignée")
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "Un produit doit être assigné")
    private Product product;

    @Column(nullable = false)
    @NotNull(message = "La quantité ne peut pas être null")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Le prix unitaire ne peut pas être null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix unitaire ne peut pas être négatif")
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Le total de la ligne ne peut pas être null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le total de la ligne ne peut pas être négatif")
    private BigDecimal lineTotal;
}
