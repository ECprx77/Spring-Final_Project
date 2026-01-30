package com.TZ.TechZone.config;

import com.TZ.TechZone.entities.Role;
import com.TZ.TechZone.entities.User;
import com.TZ.TechZone.repositories.RoleRepository;
import com.TZ.TechZone.repositories.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crée des comptes admin de test au démarrage si absents :
 * - admin@test.com / admin123 (tests manuels)
 * - postman.admin@techzone-test.com / PostmanAdmin123! (Collection Postman Runner)
 */
@Component
public class AdminDataLoader implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDataLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Rôle ADMIN introuvable"));

        if (!userRepository.existsByEmail("admin@test.com")) {
            User admin = new User();
            admin.setEmail("admin@test.com");
            admin.setFullName("Admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(adminRole);
            userRepository.save(admin);
        }

        if (!userRepository.existsByEmail("postman.admin@techzone-test.com")) {
            User postmanAdmin = new User();
            postmanAdmin.setEmail("postman.admin@techzone-test.com");
            postmanAdmin.setFullName("Postman Test Admin");
            postmanAdmin.setPasswordHash(passwordEncoder.encode("PostmanAdmin123!"));
            postmanAdmin.setRole(adminRole);
            userRepository.save(postmanAdmin);
        }
    }
}
