package com.TZ.TechZone.controller;

import com.TZ.TechZone.dto.CategoryDTO;
import com.TZ.TechZone.dto.OrderDTO;
import com.TZ.TechZone.dto.ProductDTO;
import com.TZ.TechZone.services.CategoryService;
import com.TZ.TechZone.services.OrderService;
import com.TZ.TechZone.services.ProductImageService;
import com.TZ.TechZone.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/app")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductImageService productImageService;

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Administration");
        return "admin/dashboard";
    }

    // ---------- Products ----------
    @GetMapping("/admin/products")
    public String productList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<ProductDTO> productPage = productService.getAllProducts(pageable);
        model.addAttribute("pageTitle", "Produits");
        model.addAttribute("productPage", productPage);
        return "admin/product-list";
    }

    @GetMapping("/admin/products/new")
    public String productFormNew(Model model) {
        model.addAttribute("pageTitle", "Nouveau produit");
        model.addAttribute("product", new ProductDTO());
        model.addAttribute("categories", categoryService.getAllCategoriesList());
        return "admin/product-form";
    }

    @GetMapping("/admin/products/{id}/edit")
    public String productFormEdit(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ProductDTO product = productService.getProductById(id);
            model.addAttribute("pageTitle", "Modifier " + product.getName());
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryService.getAllCategoriesList());
            return "admin/product-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Produit non trouvé.");
            return "redirect:/app/admin/products";
        }
    }

    @PostMapping("/admin/products")
    public String productCreate(@Valid @ModelAttribute("product") ProductDTO product, BindingResult bindingResult,
                                @RequestParam(value = "image", required = false) MultipartFile image,
                                Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategoriesList());
            return "admin/product-form";
        }
        try {
            product.setStatus(product.getStatus() != null ? product.getStatus() : "en_stock");
            product.setIsPromo(product.getIsPromo() != null ? product.getIsPromo() : false);
            ProductDTO savedProduct = productService.createProduct(product);
            if (image != null && !image.isEmpty()) {
                try {
                    productImageService.uploadImage(savedProduct.getId(), image, true);
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("message", "Produit créé mais l'image n'a pas pu être enregistrée : " + e.getMessage());
                    return "redirect:/app/admin/products";
                }
            }
            redirectAttributes.addFlashAttribute("message", "Produit créé.");
            return "redirect:/app/admin/products";
        } catch (Exception e) {
            model.addAttribute("categories", categoryService.getAllCategoriesList());
            model.addAttribute("message", e.getMessage());
            return "admin/product-form";
        }
    }

    @PostMapping("/admin/products/{id}")
    public String productUpdate(@PathVariable Integer id, @Valid @ModelAttribute("product") ProductDTO product,
                               BindingResult bindingResult,
                               @RequestParam(value = "image", required = false) MultipartFile image,
                               Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategoriesList());
            return "admin/product-form";
        }
        try {
            product.setId(id);
            product.setStatus(product.getStatus() != null ? product.getStatus() : "en_stock");
            product.setIsPromo(product.getIsPromo() != null ? product.getIsPromo() : false);
            productService.updateProduct(id, product);
            if (image != null && !image.isEmpty()) {
                try {
                    productImageService.deleteAllImagesForProduct(id);
                    productImageService.uploadImage(id, image, true);
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("message", "Produit mis à jour mais l'image n'a pas pu être remplacée : " + e.getMessage());
                    return "redirect:/app/admin/products";
                }
            }
            redirectAttributes.addFlashAttribute("message", "Produit mis à jour.");
            return "redirect:/app/admin/products";
        } catch (Exception e) {
            model.addAttribute("categories", categoryService.getAllCategoriesList());
            model.addAttribute("message", e.getMessage());
            return "admin/product-form";
        }
    }

    @PostMapping("/admin/products/{id}/delete")
    public String productDelete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "Produit supprimé.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Impossible de supprimer.");
        }
        return "redirect:/app/admin/products";
    }

    // ---------- Categories ----------
    @GetMapping("/admin/categories")
    public String categoryList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<CategoryDTO> categoryPage = categoryService.getAllCategories(pageable);
        model.addAttribute("pageTitle", "Catégories");
        model.addAttribute("categoryPage", categoryPage);
        return "admin/category-list";
    }

    @GetMapping("/admin/categories/new")
    public String categoryFormNew(Model model) {
        model.addAttribute("pageTitle", "Nouvelle catégorie");
        model.addAttribute("category", new CategoryDTO());
        return "admin/category-form";
    }

    @GetMapping("/admin/categories/{id}/edit")
    public String categoryFormEdit(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CategoryDTO category = categoryService.getCategoryById(id);
            model.addAttribute("pageTitle", "Modifier " + category.getName());
            model.addAttribute("category", category);
            return "admin/category-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Catégorie non trouvée.");
            return "redirect:/app/admin/categories";
        }
    }

    @PostMapping("/admin/categories")
    public String categoryCreate(@Valid @ModelAttribute("category") CategoryDTO category, BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/category-form";
        }
        try {
            categoryService.createCategory(category);
            redirectAttributes.addFlashAttribute("message", "Catégorie créée.");
            return "redirect:/app/admin/categories";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/app/admin/categories/new";
        }
    }

    @PostMapping("/admin/categories/{id}")
    public String categoryUpdate(@PathVariable Integer id, @Valid @ModelAttribute("category") CategoryDTO category,
                                 BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/category-form";
        }
        try {
            categoryService.updateCategory(id, category);
            redirectAttributes.addFlashAttribute("message", "Catégorie mise à jour.");
            return "redirect:/app/admin/categories";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/app/admin/categories/" + id + "/edit";
        }
    }

    @PostMapping("/admin/categories/{id}/delete")
    public String categoryDelete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("message", "Catégorie supprimée.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Impossible de supprimer.");
        }
        return "redirect:/app/admin/categories";
    }

    // ---------- Orders ----------
    @GetMapping("/admin/orders")
    public String orderList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));
        Page<OrderDTO> orderPage = orderService.getAllOrders(pageable);
        model.addAttribute("pageTitle", "Commandes");
        model.addAttribute("orderPage", orderPage);
        return "admin/order-list";
    }

    private static final java.util.List<String> ORDER_STATUSES = java.util.List.of("PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED");

    @GetMapping("/admin/orders/{id}")
    public String orderDetail(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            OrderDTO order = orderService.getOrderById(id);
            model.addAttribute("pageTitle", "Commande #" + order.getId());
            model.addAttribute("order", order);
            model.addAttribute("orderStatuses", ORDER_STATUSES);
            return "admin/order-detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Commande non trouvée.");
            return "redirect:/app/admin/orders";
        }
    }

    @PostMapping("/admin/orders/{id}/status")
    public String orderUpdateStatus(@PathVariable Integer id, @RequestParam String status,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("message", "Statut mis à jour.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/app/admin/orders/" + id;
    }
}
