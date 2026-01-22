package com.TZ.TechZone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Integer id;
    
    @NotBlank(message = "Le nom de la catégorie ne peut pas être vide")
    private String name;
    
    private String description;
}
