package com.smartusers.logitrackapi.mapper;

import com.smartusers.logitrackapi.dto.Product.ProductRequest;
import com.smartusers.logitrackapi.dto.Product.ProductResponse;
import com.smartusers.logitrackapi.entity.Category;
import com.smartusers.logitrackapi.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {


    default ProductResponse toResponse(Product product) {
        if (product == null) return null;

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setSku(product.getSku());
        response.setName(product.getName());
        response.setOriginalPrice(product.getOriginalPrice());
        response.setProfit(product.getProfit());
        response.setUnit(product.getUnit());
        response.setActive(product.getActive());

        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
        }

        return response;
    }

    // DTO -> Entity
    default Product toEntity(ProductRequest request) {
        if (request == null) return null;

        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setProfit(request.getProfit());
        product.setUnit(request.getUnit());
        product.setActive(request.getActive());

        if (request.getCategoryId() != null) {
            Category category = new Category();
            category.setId(request.getCategoryId());
            product.setCategory(category);
        }

        return product;
    }
}
