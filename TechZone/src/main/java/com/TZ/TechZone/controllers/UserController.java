package com.TZ.TechZone.controllers;

import com.TZ.TechZone.controllers.AuthController.ApiResponse;
import com.TZ.TechZone.dto.UserDTO;
import com.TZ.TechZone.security.UserPrincipal;
import com.TZ.TechZone.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour la gestion des utilisateurs (Phase 8 – Admin).
 *
 * Endpoints admin:
 *  - GET    /api/admin/users              (liste paginée)
 *  - GET    /api/admin/users/{id}         (détails)
 *  - PUT    /api/admin/users/{id}         (modifier)
 *  - DELETE /api/admin/users/{id}         (supprimer)
 *  - PUT    /api/admin/users/{id}/role    (changer rôle)
 */
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Liste tous les utilisateurs (admin).
     * GET /api/admin/users?page=0&size=10&sort=id,asc
     */
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<UserDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Détail d'un utilisateur (admin).
     * GET /api/admin/users/{id}
     */
    @GetMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Modifie un utilisateur (admin).
     * PUT /api/admin/users/{id}
     * Body: { "email": "...", "fullName": "...", "password": "..." } (tous optionnels)
     */
    @PutMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody AdminUpdateUserRequest request) {

        UserDTO user = userService.updateUser(
                id,
                request.getEmail(),
                request.getFullName(),
                request.getPassword());
        return ResponseEntity.ok(user);
    }

    /**
     * Supprime un utilisateur (admin).
     * DELETE /api/admin/users/{id}
     * Un admin ne peut pas se supprimer lui-même.
     */
    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Integer id) {
        int currentId = getCurrentUserId();
        if (id.equals(currentId)) {
            throw new IllegalArgumentException("Un administrateur ne peut pas supprimer son propre compte");
        }
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse(true, "Utilisateur supprimé avec succès"));
    }

    /**
     * Change le rôle d'un utilisateur (admin).
     * PUT /api/admin/users/{id}/role
     * Body: { "role": "USER" } ou { "role": "ADMIN" }
     * Un admin ne peut pas modifier son propre rôle.
     */
    @PutMapping("/admin/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUserRole(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateRoleRequest request) {

        int currentId = getCurrentUserId();
        if (id.equals(currentId)) {
            throw new IllegalArgumentException("Un administrateur ne peut pas modifier son propre rôle");
        }
        UserDTO user = userService.updateUserRole(id, request.getRole());
        return ResponseEntity.ok(user);
    }

    private int getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth != null ? auth.getPrincipal() : null;
        if (principal instanceof UserPrincipal up) {
            return up.getId();
        }
        throw new IllegalStateException("Utilisateur non authentifié");
    }

    /**
     * Payload pour la modification d'un utilisateur (admin).
     */
    public static class AdminUpdateUserRequest {
        private String email;
        private String fullName;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * Payload pour le changement de rôle (admin).
     */
    public static class UpdateRoleRequest {
        @NotBlank(message = "Le rôle est requis (USER ou ADMIN)")
        private String role;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
