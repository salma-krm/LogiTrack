package com.smartusers.logitrackapi.controllers;



import com.smartusers.logitrackapi.annotation.RequireAuth;
import com.smartusers.logitrackapi.annotation.RequireRole;
import com.smartusers.logitrackapi.entity.Category;

import com.smartusers.logitrackapi.service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CategoryController {

    private final CategoryService categoryService;

    @RequireAuth
    @RequireRole("ADMIN")
    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }
    @RequireAuth
    @RequireRole("ADMIN")
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @RequireAuth
    @RequireRole("ADMIN")
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @RequireAuth
    @RequireRole("ADMIN")
    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return categoryService.updateCategory(id, category);

    }

    @RequireAuth
    @RequireRole("ADMIN")
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
