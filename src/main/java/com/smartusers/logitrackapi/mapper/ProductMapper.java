package com.smartusers.logitrackapi.mapper;


import com.smartusers.logitrackapi.dto.Product.ProductRequest;
import com.smartusers.logitrackapi.dto.Product.ProductResponse;
import com.smartusers.logitrackapi.entity.Category;
import com.smartusers.logitrackapi.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {


    @Mapping(target = "categoryId", source = "category.id")
    ProductResponse toResponse(Product product);

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


    default void updateEntityFromRequest(ProductRequest request, @MappingTarget Product product) {
        if (request == null) return;

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
    }
}
