package com.smartusers.logitrackapi.controllers;
import com.smartusers.logitrackapi.annotation.RequireAuth;
import com.smartusers.logitrackapi.annotation.RequireRole;
import com.smartusers.logitrackapi.entity.Product;
import com.smartusers.logitrackapi.service.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductController {

    private final ProductService productService;
    @RequireAuth
    @RequireRole("ADMIN")
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable("id")  Long id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    @RequireAuth
    @RequireRole("ADMIN")
    public Product updateProduct(@PathVariable("id")  Long id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }
    @RequireAuth
    @RequireRole("ADMIN")
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
    @GetMapping("/find")
    public List<Product> searchProductsByName(@RequestParam("name") String name) {
        return productService.searchProductsByName(name);
    }

}
