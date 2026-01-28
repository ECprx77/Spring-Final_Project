package com.TZ.TechZone.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtre JWT pour valider et traiter les tokens JWT dans les requêtes HTTP
 * Exécuté une fois par requête
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Extraire le JWT du header Authorization
            String jwt = getJwtFromRequest(request);

            // Valider et traiter le token si present
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Integer userId = tokenProvider.getUserIdFromToken(jwt);

                // Charger les détails de l'utilisateur
                UserDetails userDetails = customUserDetailsService.loadUserById(userId);

                // Créer un token d'authentification
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Définir l'authentification dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                logger.debug("Utilisateur authentifié avec succès: {}", userId);
            }
        } catch (Exception ex) {
            logger.error("Impossible de définir l'authentification de l'utilisateur dans le contexte de sécurité", ex);
        }

        // Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // Skip le filtrage pour les URLs publiques
        if (path.contains("/auth/") ||
            path.contains("/swagger-ui") ||
            path.contains("/v3/api-docs") ||
            path.contains("/swagger-resources") ||
            path.contains("/h2-console") ||
            path.equals("/") ||
            path.equals("/favicon.ico")) {
            return true;
        }
        // GET /products et /categories sont publics : on peut skip. POST/PUT/DELETE (images, etc.) doivent être authentifiés.
        if ("GET".equalsIgnoreCase(request.getMethod()) &&
            (path.contains("/api/products") || path.contains("/api/categories"))) {
            return true;
        }
        return false;
    }

    /**
     * Extrait le token JWT du header Authorization
     * Format attendu: "Bearer <token>"
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
