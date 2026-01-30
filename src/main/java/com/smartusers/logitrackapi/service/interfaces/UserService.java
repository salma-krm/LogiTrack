package com.smartusers.logitrackapi.service.interfaces;

import com.smartusers.logitrackapi.dto.user.UserRequest;
import com.smartusers.logitrackapi.dto.user.UserResponse;
import com.smartusers.logitrackapi.dto.user.UserUpdateRequest;
import com.smartusers.logitrackapi.enums.Role;

import java.util.List;

public interface UserService {

    // Créer un nouvel utilisateur
    UserResponse createUser(UserRequest request);

    // Récupérer tous les utilisateurs
    List<UserResponse> getAllUsers();

    // Récupérer un utilisateur par ID
    UserResponse getUserById(Long id);

    // Récupérer un utilisateur par email
    UserResponse getUserByEmail(String email);

    // Mettre à jour un utilisateur
    UserResponse updateUser(Long id, UserUpdateRequest request);

    // Supprimer un utilisateur
    void deleteUser(Long id);

    // Activer/Désactiver un utilisateur
    UserResponse toggleUserStatus(Long id);

    // Changer le rôle d'un utilisateur
    UserResponse changeUserRole(Long id, Role newRole);

    // Récupérer les utilisateurs par rôle
    List<UserResponse> getUsersByRole(Role role);

    // Récupérer les utilisateurs actifs
    List<UserResponse> getActiveUsers();

    // Récupérer les utilisateurs inactifs
    List<UserResponse> getInactiveUsers();

    // Rechercher des utilisateurs par nom
    List<UserResponse> searchUsersByName(String name);

    // Compter les utilisateurs par rôle
    long countUsersByRole(Role role);
}
