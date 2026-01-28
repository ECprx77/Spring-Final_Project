package com.TZ.TechZone.controllers;

import com.TZ.TechZone.dto.CategoryDTO;
import com.TZ.TechZone.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des catégories
 * Endpoints: /api/categories (public), /api/admin/categories (admin)
 */
@RestController
@RequestMapping("")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Récupère toutes les catégories (public)
     * GET /api/categories?page=0&size=10&sort=name,asc
     */
    @GetMapping("/categories")
    public ResponseEntity<Page<CategoryDTO>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<CategoryDTO> categories = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(categories);
    }

    /**
     * Récupère une catégorie par ID (public)
     * GET /api/categories/{id}
     */
    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Integer id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Récupère une catégorie par nom (public)
     * GET /api/categories/by-name?name=Électronique
     */
    @GetMapping("/categories/by-name")
    public ResponseEntity<CategoryDTO> getCategoryByName(@RequestParam String name) {
        CategoryDTO category = categoryService.getCategoryByName(name);
        return ResponseEntity.ok(category);
    }

    /**
     * Récupère toutes les catégories sans pagination (public)
     * GET /api/categories/all
     */
    @GetMapping("/categories/all")
    public ResponseEntity<List<CategoryDTO>> getAllCategoriesSimple() {
        List<CategoryDTO> categories = categoryService.getAllCategoriesList();
        return ResponseEntity.ok(categories);
    }

    // ========== ADMIN ENDPOINTS ==========

    /**
     * Crée une nouvelle catégorie (admin)
     * POST /api/admin/categories
     */
    @PostMapping("/admin/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    /**
     * Modifie une catégorie (admin)
     * PUT /api/admin/categories/{id}
     */
    @PutMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Supprime une catégorie (admin)
     * DELETE /api/admin/categories/{id}
     */
    @DeleteMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(new com.TZ.TechZone.controllers.AuthController.ApiResponse(true, "Catégorie supprimée avec succès"));
    }
}
