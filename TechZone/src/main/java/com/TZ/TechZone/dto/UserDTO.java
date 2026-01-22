package com.TZ.TechZone.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    
    @Email(message = "Email doit être valide")
    private String email;
    
    @NotBlank(message = "Le nom complet ne peut pas être vide")
    private String fullName;
    
    private RoleDTO role;
}
