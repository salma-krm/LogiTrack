package com.smartusers.logitrackapi.mapper;


import com.smartusers.logitrackapi.dto.Product.ProductRequest;
import com.smartusers.logitrackapi.dto.Product.ProductResponse;
import com.smartusers.logitrackapi.entity.Category;
import com.smartusers.logitrackapi.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Supprimer les mappings automatiques et utiliser une méthode personnalisée
    default ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setSku(product.getSku());
        response.setName(product.getName());
        response.setOriginalPrice(product.getOriginalPrice());
        response.setProfit(product.getProfit());
        response.setUnit(product.getUnit());
        response.setPhoto(product.getPhoto());
        response.setActive(product.getActive());

        // Gérer les catégories nulles
        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
        } else {
            response.setCategoryId(null);
            response.setCategoryName(null);
        }

        return response;
    }

    default Product toEntity(ProductRequest request) {
        if (request == null) return null;

        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setProfit(request.getProfit());
        product.setUnit(request.getUnit());
        product.setPhoto(request.getPhoto());
        product.setActive(request.getActive());

        if (request.getCategoryId() != null) {
            Category category = new Category();
            category.setId(request.getCategoryId());
            product.setCategory(category);
        }

        return product;
    }


    default void updateEntityFromRequest(ProductRequest request, @MappingTarget Product product) {
        if (request == null) return;

        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setProfit(request.getProfit());
        product.setUnit(request.getUnit());
        product.setPhoto(request.getPhoto());
        product.setActive(request.getActive());

        if (request.getCategoryId() != null) {
            Category category = new Category();
            category.setId(request.getCategoryId());
            product.setCategory(category);
        }
    }
}
