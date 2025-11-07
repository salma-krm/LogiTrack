package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.entity.Product;
import com.smartusers.logitrackapi.repository.ProductRepository;
import com.smartusers.logitrackapi.service.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {

        if (productRepository.existsBySku(product.getSku())) {
            throw new RuntimeException("Le SKU existe déjà : " + product.getSku());
        }
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        Product existing = getProductById(id);

        // Vérifier si le nouveau SKU est utilisé par un autre produit
        if (!existing.getSku().equals(product.getSku()) && productRepository.existsBySku(product.getSku())) {
            throw new RuntimeException("Le SKU existe déjà : " + product.getSku());
        }

        existing.setName(product.getName());
        existing.setSku(product.getSku());
        existing.setCategory(product.getCategory());
        existing.setOriginalPrice(product.getOriginalPrice());
        existing.setProfit(product.getProfit());
        existing.setUnit(product.getUnit());
        existing.setActive(product.getActive());
        return productRepository.save(existing);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

}
