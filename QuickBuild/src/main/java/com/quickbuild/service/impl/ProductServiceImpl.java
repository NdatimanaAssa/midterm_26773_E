package com.quickbuild.service.impl;

import com.quickbuild.domain.Product;
import com.quickbuild.domain.Category;
import com.quickbuild.dto.response.ProductResponse;
import com.quickbuild.exception.ResourceNotFoundException;
import com.quickbuild.mapper.ProductMapper;
import com.quickbuild.repository.CategoryRepository;
import com.quickbuild.repository.ProductRepository;
import com.quickbuild.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductMapper::toResponse);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductMapper.toResponse(product);
    }

    @Override
    public ProductResponse createProduct(Product product, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        product.setCategory(category);

        // Link bidirectional mapping if images are provided
        if (product.getImages() != null) {
            product.getImages().forEach(img -> img.setProduct(product));
        }

        Product savedProduct = productRepository.save(product);
        return ProductMapper.toResponse(savedProduct);
    }
}
