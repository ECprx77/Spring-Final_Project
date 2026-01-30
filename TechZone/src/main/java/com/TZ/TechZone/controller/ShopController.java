package com.TZ.TechZone.controller;

import com.TZ.TechZone.dto.CartDTO;
import com.TZ.TechZone.dto.CategoryDTO;
import com.TZ.TechZone.dto.OrderDTO;
import com.TZ.TechZone.dto.ProductDTO;
import com.TZ.TechZone.security.UserPrincipal;
import com.TZ.TechZone.services.CartService;
import com.TZ.TechZone.services.CategoryService;
import com.TZ.TechZone.services.OrderService;
import com.TZ.TechZone.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/app")
public class ShopController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getId();
        }
        return null;
    }

    @GetMapping("/shop")
    public String catalog(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Boolean promo,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            Model model) {
        model.addAttribute("pageTitle", "Catalogue");
        List<CategoryDTO> categories = categoryService.getAllCategoriesList();
        model.addAttribute("categories", categories);

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ProductDTO> productPage;
        if (q != null && !q.isBlank()) {
            productPage = productService.searchProducts(q.trim(), pageable);
            model.addAttribute("searchQuery", q.trim());
        } else if (Boolean.TRUE.equals(promo)) {
            productPage = productService.getPromoProducts(pageable);
            model.addAttribute("filterPromo", true);
        } else if (Boolean.TRUE.equals(inStock)) {
            productPage = productService.getProductsInStock(pageable);
            model.addAttribute("filterInStock", true);
        } else if (categoryId != null) {
            productPage = productService.getProductsByCategory(categoryId, pageable);
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            productPage = productService.getAllProducts(pageable);
        }
        model.addAttribute("productPage", productPage);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", sortDirection);
        return "shop/catalog";
    }

    @GetMapping("/shop/products/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        ProductDTO product = productService.getProductById(id);
        model.addAttribute("pageTitle", product.getName());
        model.addAttribute("product", product);
        return "shop/product-detail";
    }

    @PostMapping("/shop/cart/add")
    public String addToCart(
            @RequestParam Integer productId,
            @RequestParam(defaultValue = "1") int quantity,
            RedirectAttributes redirectAttributes) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "Connectez-vous pour ajouter au panier.");
            return "redirect:/app/login";
        }
        if (quantity < 1) quantity = 1;
        try {
            cartService.addItem(userId, productId, quantity);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Impossible d'ajouter ce produit au panier.");
            return "redirect:/app/shop";
        }
        return "redirect:/app/shop/cart?added=1";
    }

    @PostMapping("/shop/cart/remove/{productId}")
    public String removeFromCart(@PathVariable Integer productId, RedirectAttributes redirectAttributes) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "Connectez-vous pour modifier le panier.");
            return "redirect:/app/login";
        }
        try {
            cartService.removeItem(userId, productId);
        } catch (Exception ignored) { }
        return "redirect:/app/shop/cart";
    }

    @GetMapping("/shop/cart")
    public String cart(@RequestParam(required = false) String added, Model model, RedirectAttributes redirectAttributes) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "Connectez-vous pour accéder au panier.");
            return "redirect:/app/login";
        }
        if ("1".equals(added)) {
            model.addAttribute("orderSuccess", "Produit ajouté au panier.");
        }
        CartDTO cart = cartService.getCart(userId);
        model.addAttribute("pageTitle", "Panier");
        model.addAttribute("cart", cart);
        return "shop/cart";
    }

    @GetMapping("/shop/checkout")
    public String checkoutPage(Model model, RedirectAttributes redirectAttributes) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "Connectez-vous pour passer commande.");
            return "redirect:/app/login";
        }
        CartDTO cart = cartService.getCart(userId);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Votre panier est vide.");
            return "redirect:/app/shop/cart";
        }
        model.addAttribute("pageTitle", "Commander");
        model.addAttribute("cart", cart);
        return "shop/checkout";
    }

    @PostMapping("/shop/checkout")
    public String placeOrder(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "Connectez-vous pour passer commande.");
            return "redirect:/app/login";
        }
        try {
            String ipAddress = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");
            OrderDTO order = orderService.createOrderFromCart(userId, ipAddress, userAgent != null ? userAgent : "");
            redirectAttributes.addFlashAttribute("orderSuccess", "Commande #" + order.getId() + " enregistrée.");
            return "redirect:/app/shop/orders";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/app/shop/cart";
        }
    }

    @GetMapping("/shop/orders")
    public String orders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            RedirectAttributes redirectAttributes) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "Connectez-vous pour voir vos commandes.");
            return "redirect:/app/login";
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));
        Page<OrderDTO> orderPage = orderService.getUserOrders(userId, pageable);
        model.addAttribute("pageTitle", "Mes commandes");
        model.addAttribute("orderPage", orderPage);
        return "shop/orders";
    }

    @GetMapping("/shop/orders/{id}")
    public String orderDetail(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "Connectez-vous pour voir cette commande.");
            return "redirect:/app/login";
        }
        try {
            OrderDTO order = orderService.getUserOrderById(userId, id);
            model.addAttribute("pageTitle", "Commande #" + order.getId());
            model.addAttribute("order", order);
            return "shop/order-detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Commande non trouvée.");
            return "redirect:/app/shop/orders";
        }
    }
}
