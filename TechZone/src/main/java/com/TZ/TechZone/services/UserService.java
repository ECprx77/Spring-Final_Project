package com.TZ.TechZone.services;

import com.TZ.TechZone.dto.RoleDTO;
import com.TZ.TechZone.dto.UserDTO;
import com.TZ.TechZone.entities.Role;
import com.TZ.TechZone.entities.User;
import com.TZ.TechZone.exceptions.ResourceNotFoundException;
import com.TZ.TechZone.repositories.RoleRepository;
import com.TZ.TechZone.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service métier pour la gestion des utilisateurs (Phase 8 – Admin).
 * Liste, détail, modification, suppression, changement de rôle.
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Liste paginée de tous les utilisateurs (admin).
     */
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * Détail d'un utilisateur par ID (admin).
     */
    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
        return toDTO(user);
    }

    /**
     * Met à jour un utilisateur (email, fullName, mot de passe optionnel).
     * L'admin ne peut pas se supprimer lui-même ni changer son propre rôle (vérifié côté contrôleur).
     */
    public UserDTO updateUser(Integer id, String email, String fullName, String rawPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        if (email != null && !email.isBlank()) {
            User existing = userRepository.findByEmail(email).orElse(null);
            if (existing != null && !existing.getId().equals(id)) {
                throw new IllegalArgumentException("Cet email est déjà utilisé par un autre utilisateur");
            }
            user.setEmail(email.trim());
        }
        if (fullName != null && !fullName.isBlank()) {
            user.setFullName(fullName.trim());
        }
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(rawPassword));
        }

        User saved = userRepository.save(user);
        return toDTO(saved);
    }

    /**
     * Supprime un utilisateur par ID.
     * Vérifier avant appel que l'admin ne se supprime pas lui-même.
     */
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
        userRepository.delete(user);
    }

    /**
     * Change le rôle d'un utilisateur (USER ou ADMIN).
     * Vérifier avant appel que l'admin ne change pas son propre rôle.
     */
    public UserDTO updateUserRole(Integer id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        String name = roleName != null ? roleName.trim().toUpperCase() : "";
        if (!name.equals("USER") && !name.equals("ADMIN")) {
            throw new IllegalArgumentException("Rôle invalide. Utilisez USER ou ADMIN.");
        }

        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé: " + name));
        user.setRole(role);
        User saved = userRepository.save(user);
        return toDTO(saved);
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        if (user.getRole() != null) {
            dto.setRole(new RoleDTO(user.getRole().getId(), user.getRole().getName()));
        }
        return dto;
    }
}
