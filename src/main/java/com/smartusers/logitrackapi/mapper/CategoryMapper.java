package com.smartusers.logitrackapi.mapper;
import com.smartusers.logitrackapi.dto.category.CategoryRequest;
import com.smartusers.logitrackapi.dto.category.CategoryResponse;
import com.smartusers.logitrackapi.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category entity);
}


