package com.TZ.TechZone.config;

import com.TZ.TechZone.dto.RoleDTO;
import com.TZ.TechZone.dto.UserDTO;
import com.TZ.TechZone.entities.User;
import com.TZ.TechZone.repositories.UserRepository;
import com.TZ.TechZone.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Ajoute l'utilisateur courant et isAdmin au modèle pour toutes les vues Thymeleaf.
 */
@ControllerAdvice
public class ThymeleafModelAdvice {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addGlobalModelAttributes(Model model, Authentication authentication) {
        model.addAttribute("user", (Object) null);
        model.addAttribute("isAdmin", false);
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return;
        }
        User user = userRepository.findById(principal.getId()).orElse(null);
        if (user == null) return;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        if (user.getRole() != null) {
            dto.setRole(new RoleDTO(user.getRole().getId(), user.getRole().getName()));
        }
        model.addAttribute("user", dto);
        model.addAttribute("isAdmin", "ADMIN".equals(user.getRole() != null ? user.getRole().getName() : null));
    }
}
