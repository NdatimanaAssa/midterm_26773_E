package com.quickbuild.service;

import com.quickbuild.domain.Product;
import com.quickbuild.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductResponse> getAllProducts(Pageable pageable);

    ProductResponse getProductById(Long id);

    ProductResponse createProduct(Product product, Long categoryId);
}
