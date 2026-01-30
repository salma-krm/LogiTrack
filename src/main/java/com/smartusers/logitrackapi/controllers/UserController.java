package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.user.UserResponse;
import com.smartusers.logitrackapi.dto.user.UserUpdateRequest;
import com.smartusers.logitrackapi.enums.Role;
import com.smartusers.logitrackapi.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Récupérer tous les utilisateurs (Admin uniquement)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Fetching all users");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Récupérer un utilisateur par ID (Admin ou le propriétaire)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.isOwner(#id, authentication)")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Fetching user with ID: {}", id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Récupérer un utilisateur par email (Admin uniquement)
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        log.info("Fetching user with email: {}", email);
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Mettre à jour un utilisateur (Admin ou le propriétaire)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.isOwner(#id, authentication)")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        log.info("Updating user with ID: {}", id);
        UserResponse updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Supprimer un utilisateur (Admin uniquement)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès"));
    }

    /**
     * Activer/Désactiver un utilisateur (Admin uniquement)
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> toggleUserStatus(@PathVariable Long id) {
        log.info("Toggling status for user with ID: {}", id);
        UserResponse user = userService.toggleUserStatus(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Changer le rôle d'un utilisateur (Admin uniquement)
     */
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> changeUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> roleData) {
        log.info("Changing role for user with ID: {}", id);

        String roleString = roleData.get("role");
        if (roleString == null || roleString.trim().isEmpty()) {
            throw new RuntimeException("Le rôle est requis");
        }

        Role newRole;
        try {
            newRole = Role.valueOf(roleString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rôle invalide: " + roleString + ". Valeurs acceptées: ADMIN, MANAGER, CLIENT");
        }

        UserResponse user = userService.changeUserRole(id, newRole);
        return ResponseEntity.ok(user);
    }

    /**
     * Récupérer les utilisateurs par rôle (Admin uniquement)
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role) {
        log.info("Fetching users with role: {}", role);

        Role roleEnum;
        try {
            roleEnum = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rôle invalide: " + role + ". Valeurs acceptées: ADMIN, MANAGER, CLIENT");
        }

        List<UserResponse> users = userService.getUsersByRole(roleEnum);
        return ResponseEntity.ok(users);
    }

    /**
     * Récupérer les utilisateurs actifs (Admin uniquement)
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getActiveUsers() {
        log.info("Fetching active users");
        List<UserResponse> users = userService.getActiveUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Récupérer les utilisateurs inactifs (Admin uniquement)
     */
    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getInactiveUsers() {
        log.info("Fetching inactive users");
        List<UserResponse> users = userService.getInactiveUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Rechercher des utilisateurs par nom (Admin uniquement)
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> searchUsersByName(@RequestParam String name) {
        log.info("Searching users by name: {}", name);
        List<UserResponse> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }

    /**
     * Compter les utilisateurs par rôle (Admin uniquement)
     */
    @GetMapping("/count/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> countUsersByRole(@PathVariable String role) {
        log.info("Counting users by role: {}", role);

        Role roleEnum;
        try {
            roleEnum = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rôle invalide: " + role + ". Valeurs acceptées: ADMIN, MANAGER, CLIENT");
        }

        long count = userService.countUsersByRole(roleEnum);
        return ResponseEntity.ok(Map.of("role", roleEnum.name(), "count", count));
    }

    /**
     * Statistiques des utilisateurs (Admin uniquement)
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        log.info("Fetching user statistics");

        long totalUsers = userService.getAllUsers().size();
        long activeUsers = userService.getActiveUsers().size();
        long inactiveUsers = userService.getInactiveUsers().size();
        long adminCount = userService.countUsersByRole(Role.ADMIN);
        long managerCount = userService.countUsersByRole(Role.MANAGER);
        long clientCount = userService.countUsersByRole(Role.CLIENT);

        return ResponseEntity.ok(Map.of(
                "totalUsers", totalUsers,
                "activeUsers", activeUsers,
                "inactiveUsers", inactiveUsers,
                "adminCount", adminCount,
                "managerCount", managerCount,
                "clientCount", clientCount
        ));
    }
}
