package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.dto.user.UserRequest;
import com.smartusers.logitrackapi.dto.user.UserResponse;
import com.smartusers.logitrackapi.dto.user.UserUpdateRequest;
import com.smartusers.logitrackapi.entity.User;
import com.smartusers.logitrackapi.enums.Role;
import com.smartusers.logitrackapi.repository.UserRepository;
import com.smartusers.logitrackapi.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà: " + request.getEmail());
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setContactInfo(request.getContactInfo());
        user.setRole(request.getRole() != null ? request.getRole() : Role.CLIENT);
        user.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        return mapToResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));
        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + email));
        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        log.info("Updating user with ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));

        // Vérifier si le nouvel email est déjà utilisé par un autre utilisateur
        if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Un utilisateur avec cet email existe déjà: " + request.getEmail());
            }
            existingUser.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) {
            existingUser.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            existingUser.setLastName(request.getLastName());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getContactInfo() != null) {
            existingUser.setContactInfo(request.getContactInfo());
        }
        if (request.getRole() != null) {
            existingUser.setRole(request.getRole());
        }
        if (request.getIsActive() != null) {
            existingUser.setIsActive(request.getIsActive());
        }

        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully with ID: {}", updatedUser.getId());

        return mapToResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID: " + id);
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }

    @Override
    public UserResponse toggleUserStatus(Long id) {
        log.info("Toggling status for user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));

        user.setIsActive(!user.getIsActive());
        User updatedUser = userRepository.save(user);

        log.info("User status toggled to {} for ID: {}", updatedUser.getIsActive(), id);
        return mapToResponse(updatedUser);
    }

    @Override
    public UserResponse changeUserRole(Long id, Role newRole) {
        log.info("Changing role to {} for user with ID: {}", newRole, id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));

        user.setRole(newRole);
        User updatedUser = userRepository.save(user);

        log.info("User role changed to {} for ID: {}", newRole, id);
        return mapToResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(Role role) {
        log.info("Fetching users with role: {}", role);
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == role)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getActiveUsers() {
        log.info("Fetching active users");
        return userRepository.findAll().stream()
                .filter(User::getIsActive)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getInactiveUsers() {
        log.info("Fetching inactive users");
        return userRepository.findAll().stream()
                .filter(user -> !user.getIsActive())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> searchUsersByName(String name) {
        log.info("Searching users by name: {}", name);
        String searchTerm = name.toLowerCase();
        return userRepository.findAll().stream()
                .filter(user -> user.getFirstName().toLowerCase().contains(searchTerm) ||
                        user.getLastName().toLowerCase().contains(searchTerm))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsersByRole(Role role) {
        log.info("Counting users by role: {}", role);
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == role)
                .count();
    }

    // Méthode utilitaire pour mapper User vers UserResponse
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .contactInfo(user.getContactInfo())
                .build();
    }
}
