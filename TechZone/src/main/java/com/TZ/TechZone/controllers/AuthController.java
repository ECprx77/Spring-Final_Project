package com.TZ.TechZone.controllers;

import com.TZ.TechZone.dto.UserDTO;
import com.TZ.TechZone.entities.Role;
import com.TZ.TechZone.entities.User;
import com.TZ.TechZone.exceptions.ResourceNotFoundException;
import com.TZ.TechZone.payload.JwtAuthenticationResponse;
import com.TZ.TechZone.payload.LoginRequest;
import com.TZ.TechZone.payload.SignUpRequest;
import com.TZ.TechZone.repositories.RoleRepository;
import com.TZ.TechZone.repositories.UserRepository;
import com.TZ.TechZone.security.JwtTokenProvider;
import com.TZ.TechZone.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour la gestion de l'authentification
 * Endpoints: /api/auth/register, /api/auth/login, /api/auth/me, etc.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * Endpoint d'inscription
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        // Vérifier que l'email n'existe pas déjà
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Cet email est déjà utilisé"));
        }

        // Créer une nouvelle entité User
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setFullName(signUpRequest.getFullName());
        user.setPasswordHash(passwordEncoder.encode(signUpRequest.getPassword()));

        // Assigner le rôle USER par défaut
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Rôle USER non trouvé"));
        user.setRole(userRole);

        // Sauvegarder l'utilisateur
        User savedUser = userRepository.save(user);

        // Retourner la réponse
        return new ResponseEntity<>(
                new ApiResponse(true, "Utilisateur enregistré avec succès. Vous pouvez maintenant vous connecter."),
                HttpStatus.CREATED
        );
    }

    /**
     * Endpoint de connexion
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authentifier l'utilisateur
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Définir le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Générer le JWT token
            String jwt = tokenProvider.generateToken(authentication);

            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Email ou mot de passe incorrect"));
        }
    }

    /**
     * Endpoint pour récupérer le profil utilisateur actuel
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Utilisateur non authentifié"));
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        return ResponseEntity.ok(convertToDTO(user));
    }

    /**
     * Endpoint de déconnexion
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new ApiResponse(true, "Vous avez été déconnecté avec succès"));
    }

    /**
     * Endpoint pour renouveler le token JWT
     * POST /api/auth/refresh-token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Utilisateur non authentifié"));
        }

        String newToken = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(newToken));
    }

    /**
     * Convertit une entité User en UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        if (user.getRole() != null) {
            dto.setRole(new com.TZ.TechZone.dto.RoleDTO(user.getRole().getId(), user.getRole().getName()));
        }
        return dto;
    }

    /**
     * Classe générique pour les réponses API
     */
    public static class ApiResponse {
        private boolean success;
        private String message;

        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
