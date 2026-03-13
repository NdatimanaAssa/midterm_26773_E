package com.quickbuild.mapper;

import com.quickbuild.domain.Category;
import com.quickbuild.dto.response.CategoryResponse;

public class CategoryMapper {

    public static CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        return response;
    }
}
