package com.TZ.TechZone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redirige la racine du contexte vers l'application Thymeleaf (/app).
 */
@Controller
public class RootController {

    @GetMapping("/")
    public String root() {
        return "redirect:/app/";
    }
}
