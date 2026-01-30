package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.Product.ProductResponse;

import com.smartusers.logitrackapi.entity.Category;
import com.smartusers.logitrackapi.entity.Product;
import com.smartusers.logitrackapi.mapper.ProductMapper;
import com.smartusers.logitrackapi.repository.CategoryRepository;
import com.smartusers.logitrackapi.service.impl.ProductImpl;
import com.smartusers.logitrackapi.service.interfaces.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse createProduct(@RequestBody Map<String, Object> productData) {
        log.info("Creating product with data: {}", productData);

        // Créer un produit à partir des données reçues
        Product product = new Product();
        product.setName((String) productData.get("name"));
        product.setSku((String) productData.get("sku"));
        product.setUnit((String) productData.get("unit"));
        product.setPhoto((String) productData.get("photo"));

        // Conversion des prix
        if (productData.get("originalPrice") != null) {
            product.setOriginalPrice(java.math.BigDecimal.valueOf(((Number) productData.get("originalPrice")).doubleValue()));
        }
        if (productData.get("profit") != null) {
            product.setProfit(java.math.BigDecimal.valueOf(((Number) productData.get("profit")).doubleValue()));
        }

        product.setActive(true);

        // Gérer la catégorie par nom si fournie
        if (productData.get("categoryName") != null) {
            String categoryName = (String) productData.get("categoryName");

            // Créer la catégorie si elle n'existe pas
            Category category = categoryRepository.findByName(categoryName)
                    .orElseGet(() -> {
                        log.info("Creating new category: {}", categoryName);
                        Category newCategory = new Category();
                        newCategory.setName(categoryName);
                        return categoryRepository.save(newCategory);
                    });

            product.setCategory(category);
            log.info("Assigned category {} (ID: {}) to product", category.getName(), category.getId());
        }

        Product createdProduct = productService.createProduct(product);
        log.info("Product created with ID: {} and category: {}",
                createdProduct.getId(),
                createdProduct.getCategory() != null ? createdProduct.getCategory().getName() : "null");

        return productMapper.toResponse(createdProduct);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CLIENT')")
    public List<ProductResponse> getAllProducts(Authentication auth) {
        log.info("Fetching all products");

        // Vérifier si l'utilisateur est un client (pour filtrer les produits actifs seulement)
        boolean isClient = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));

        List<Product> products;
        if (isClient) {
            products = productService.getAllProducts().stream()
                    .filter(Product::getActive)
                    .toList();
            log.info("CLIENT - Number of active products fetched: {}", products.size());
        } else {
            products = productService.getAllProducts();
            log.info("ADMIN/MANAGER - Number of products fetched: {}", products.size());
        }

        return products.stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable("id") Long id) {
        log.info("Fetching product with ID: {}", id);
        Product product = productService.getProductById(id);
        if (product != null) {
            log.info("Product found: {}", product.getName());
            return productMapper.toResponse(product);
        } else {
            log.warn("Product with ID {} not found", id);
            return null;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProduct(@PathVariable("id") Long id, @RequestBody Product product) {
        log.info("Updating product with ID: {}", id);
        Product updatedProduct = productService.updateProduct(id, product);
        log.info("Product updated: {}", updatedProduct.getName());
        return productMapper.toResponse(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(@PathVariable Long id) {
        log.info("Deleting product with ID: {}", id);
        productService.deleteProduct(id);
        log.info("Product deleted with ID: {}", id);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> deactivateProduct(@PathVariable Long id) {
        log.info("Deactivating product with ID: {}", id);

        // Récupérer le produit pour obtenir son SKU
        Product product = productService.getProductById(id);

        // Appeler la méthode de désactivation avec le SKU
        ProductResponse response = productService.desactivitedProduct(product.getSku());

        log.info("Product deactivated with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/category")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProductCategory(@PathVariable Long id, @RequestBody Map<String, String> categoryData) {
        log.info("Updating category for product with ID: {}", id);

        String categoryName = categoryData.get("categoryName");
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new RuntimeException("categoryName est requis");
        }

        // Récupérer le produit
        Product product = productService.getProductById(id);

        // Créer la catégorie si elle n'existe pas
        Category category = categoryRepository.findByName(categoryName)
                .orElseGet(() -> {
                    log.info("Creating new category: {}", categoryName);
                    Category newCategory = new Category();
                    newCategory.setName(categoryName);
                    return categoryRepository.save(newCategory);
                });

        // Mettre à jour la catégorie du produit
        product.setCategory(category);
        Product updatedProduct = productService.updateProduct(id, product);

        log.info("Product {} category updated to {} (ID: {})",
                updatedProduct.getName(), category.getName(), category.getId());

        return ResponseEntity.ok(productMapper.toResponse(updatedProduct));
    }

    @PatchMapping("/assign-default-category")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> assignDefaultCategoryToProducts() {
        log.info("Assigning default category to products without category");

        // Utiliser le service ProductImpl pour assigner les catégories
        ProductImpl productServiceImpl = (ProductImpl) productService;
        String result = productServiceImpl.assignDefaultCategoriesToProducts();

        log.info("Default categories assigned successfully: {}", result);
        return ResponseEntity.ok(result);
    }
}
