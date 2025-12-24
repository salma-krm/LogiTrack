package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.annotation.RequireAuth;
import com.smartusers.logitrackapi.annotation.RequireRole;
import com.smartusers.logitrackapi.dto.Product.ProductResponse;
import com.smartusers.logitrackapi.entity.Product;
import com.smartusers.logitrackapi.service.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        log.info("Creating product: {}", product.getName());
        Product createdProduct = productService.createProduct(product);
        log.info("Product created with ID: {}", createdProduct.getId());
        return createdProduct;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        List<Product> products = productService.getAllProducts();
        log.info("Number of products fetched: {}", products.size());
        return products;
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable("id") Long id) {
        log.info("Fetching product with ID: {}", id);
        Product product = productService.getProductById(id);
        if (product != null) {
            log.info("Product found: {}", product.getName());
        } else {
            log.warn("Product with ID {} not found", id);
        }
        return product;
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable("id") Long id, @RequestBody Product product) {
        log.info("Updating product with ID: {}", id);
        Product updatedProduct = productService.updateProduct(id, product);
        log.info("Product updated: {}", updatedProduct.getName());
        return updatedProduct;
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        log.info("Deleting product with ID: {}", id);
        productService.deleteProduct(id);
        log.info("Product with ID {} deleted", id);
    }

    @GetMapping("/find")
    public List<Product> searchProductsByName(@RequestParam("name") String name) {
        log.info("Searching products with name containing: {}", name);
        List<Product> products = productService.searchProductsByName(name);
        log.info("Number of products found: {}", products.size());
        return products;
    }

    @PatchMapping("/{sku}/desactivate")
    public ResponseEntity<ProductResponse> desactivateProduct(@PathVariable("sku") String sku) {
        log.info("Deactivating product with SKU: {}", sku);
        ProductResponse product = productService.desactivitedProduct(sku);
        log.info("Product with SKU {} deactivated", sku);
        return ResponseEntity.ok(product);
    }
}
