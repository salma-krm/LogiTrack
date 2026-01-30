package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.entity.Category;
import com.smartusers.logitrackapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        log.info("Creating category: {}", category.getName());
        Category savedCategory = categoryRepository.save(category);
        return ResponseEntity.ok(savedCategory);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        log.info("Found {} categories", categories.size());
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/create-defaults")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createDefaultCategories() {
        log.info("Creating default categories");

        // Créer Beauty si elle n'existe pas
        if (!categoryRepository.existsByName("Beauty")) {
            Category beauty = new Category();
            beauty.setName("Beauty");
            categoryRepository.save(beauty);
            log.info("Created Beauty category");
        }

        // Créer Electronics si elle n'existe pas
        if (!categoryRepository.existsByName("Electronics")) {
            Category electronics = new Category();
            electronics.setName("Electronics");
            categoryRepository.save(electronics);
            log.info("Created Electronics category");
        }

        // Créer General si elle n'existe pas
        if (!categoryRepository.existsByName("General")) {
            Category general = new Category();
            general.setName("General");
            categoryRepository.save(general);
            log.info("Created General category");
        }

        return ResponseEntity.ok("Default categories created successfully");
    }
}
