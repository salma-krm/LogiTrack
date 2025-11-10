package com.smartusers.logitrackapi.service.interfaces;


import com.smartusers.logitrackapi.dto.Product.ProductResponse;
import com.smartusers.logitrackapi.entity.Product;
import java.util.List;

public interface ProductService {
    ProductResponse desactivitedProduct(String SKU);
    Product createProduct(Product product);
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    List<Product> searchProductsByName(String name);
}
