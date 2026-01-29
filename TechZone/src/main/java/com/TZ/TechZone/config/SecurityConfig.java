package com.TZ.TechZone.config;

import com.TZ.TechZone.security.JwtAuthenticationEntryPoint;
import com.TZ.TechZone.security.JwtAuthenticationFilter;
import com.TZ.TechZone.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * Encodeur de mot de passe BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Filtre JWT pour valider les tokens dans les requêtes
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    /**
     * AuthenticationManager pour la gestion de l'authentification
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    /**
     * Configuration de la chaîne de filtres de sécurité
     * Fusion : JWT authentication + H2 Console support
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            .sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    // Ressources publiques - Static files
                    .requestMatchers("/", "/favicon.ico", "/**/*.png", "/**/*.gif", "/**/*.svg", 
                                    "/**/*.jpg", "/**/*.jpeg", "/**/*.webp", "/**/*.html", "/**/*.css", "/**/*.js", 
                                    "/**/*.woff", "/**/*.woff2", "/**/*.ttf", "/**/*.eot").permitAll()
                    // Uploaded files (public read)
                    .requestMatchers("/uploads/**").permitAll()
                    // API d'authentification
                    .requestMatchers("/api/auth/**").permitAll()
                    // Swagger/OpenAPI
                    .requestMatchers("/api/swagger-ui**", "/api/swagger-ui/**", "/api/swagger-resources/**",
                                    "/api/v3/api-docs**", "/api/v3/api-docs/**").permitAll()
                    // H2 Console - Accès public (développement uniquement)
                    .requestMatchers("/api/h2-console/**").permitAll()
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/error").permitAll()
                    // GET produits et catégories (public)
                    .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                    // Pages Thymeleaf (préfixe /app) - Frontend at root
                    .requestMatchers("/", "/app", "/app/").permitAll()
                    .requestMatchers("/app/login", "/app/register", "/app/logout").permitAll()
                    .requestMatchers("/app/shop", "/app/shop/", "/app/shop/products", "/app/shop/products/**").permitAll()
                    .requestMatchers("/app/admin/**").hasRole("ADMIN")
                    // API REST admin - Nécessite ROLE_ADMIN
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    // Panier, commandes, checkout nécessitent authentification
                    .anyRequest().authenticated()
            )
            // Permettre les frames pour H2 Console (header X-Frame-Options: SAMEORIGIN)
            .headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
            );

        // Ajouter le filtre JWT avant le filtre d'authentification standard
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
