package com.TZ.TechZone.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Integer id;
    
    @NotNull(message = "Un utilisateur doit être assigné")
    private Integer userId;
    
    private UserDTO user;
    
    private LocalDateTime orderDate;
    
    private String status;
    
    @NotNull(message = "Le total ne peut pas être null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le total ne peut pas être négatif")
    private BigDecimal total;
    
    private List<OrderLineDTO> orderLines;
}
