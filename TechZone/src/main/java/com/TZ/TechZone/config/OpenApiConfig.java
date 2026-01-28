package com.TZ.TechZone.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration OpenAPI/Swagger pour la documentation API
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TechZone API")
                        .description("API REST pour la gestion de produits, catégories, commandes et authentification")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("TechZone Support")
                                .email("support@techzone.com")));
    }
}

