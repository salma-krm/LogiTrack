package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.entity.Category;
import com.smartusers.logitrackapi.repository.CategoryRepository;
import com.smartusers.logitrackapi.service.interfaces.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCategory() {
        Category category = new Category();
        category.setName("Electronics");

        when(categoryRepository.save(category)).thenReturn(category);

        Category created = categoryService.createCategory(category);

        assertNotNull(created);
        assertEquals("Electronics", created.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void testGetAllCategories() {
        Category c1 = new Category(); c1.setName("Electronics");
        Category c2 = new Category(); c2.setName("Books");

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<Category> categories = categoryService.getAllCategories();

        assertEquals(2, categories.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testGetCategoryByIdFound() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category found = categoryService.getCategoryById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCategoryByIdNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoryById(1L);
        });

        assertEquals("Category not found", exception.getMessage());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateCategory() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setName("Old Name");

        Category updated = new Category();
        updated.setName("New Name");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(existing);

        Category result = categoryService.updateCategory(1L, updated);

        assertEquals("New Name", result.getName());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(existing);
    }

    @Test
    void testDeleteCategory() {
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }
}
