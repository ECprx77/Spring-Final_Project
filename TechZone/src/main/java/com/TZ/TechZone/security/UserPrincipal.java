package com.TZ.TechZone.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.TZ.TechZone.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Implémentation de UserDetails pour Spring Security
 * Représente l'utilisateur authentifié avec ses autorités
 */
public class UserPrincipal implements UserDetails {

    private Integer id;
    private String email;

    @JsonIgnore
    private String passwordHash;

    private String fullName;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Integer id, String email, String passwordHash, String fullName,
                         Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.authorities = authorities;
    }

    /**
     * Crée un UserPrincipal à partir d'une entité User
     */
    public static UserPrincipal create(User user) {
        if (user.getRole() == null) {
            throw new IllegalArgumentException("L'utilisateur doit avoir un rôle assigné");
        }
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().getName());
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getFullName(),
                Collections.singletonList(authority)
        );
    }

    // Getters

    public Integer getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    // Implémentation de UserDetails

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
