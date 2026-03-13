package com.quickbuild.service;

import com.quickbuild.dto.response.CategoryResponse;
import com.quickbuild.domain.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();

    CategoryResponse createCategory(Category category);

    Category getCategoryById(Long id);
}
