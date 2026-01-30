package com.smartusers.logitrackapi.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur de test pour les tests d'intégration des permissions
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnly() {
        return "ADMIN ACCESS GRANTED";
    }

    @GetMapping("/manager-only")
    @PreAuthorize("hasRole('MANAGER')")
    public String managerOnly() {
        return "MANAGER ACCESS GRANTED";
    }

    @GetMapping("/client-only")
    @PreAuthorize("hasRole('CLIENT')")
    public String clientOnly() {
        return "CLIENT ACCESS GRANTED";
    }

    @GetMapping("/authenticated")
    public String authenticated() {
        return "AUTHENTICATED ACCESS GRANTED";
    }
}

