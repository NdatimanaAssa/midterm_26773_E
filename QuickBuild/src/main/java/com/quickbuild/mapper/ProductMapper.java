package com.quickbuild.mapper;

import com.quickbuild.domain.Product;
import com.quickbuild.domain.ProductImage;
import com.quickbuild.dto.response.ProductImageResponse;
import com.quickbuild.dto.response.ProductResponse;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    public static ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSku(product.getSku());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setCreatedAt(product.getCreatedAt());

        if (product.getCategory() != null) {
            response.setCategoryName(product.getCategory().getName());
        }

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            List<ProductImageResponse> imageResponses = product.getImages().stream()
                    .map(ProductMapper::toImageResponse)
                    .collect(Collectors.toList());
            response.setImages(imageResponses);
        }

        return response;
    }

    public static ProductImageResponse toImageResponse(ProductImage image) {
        if (image == null) {
            return null;
        }
        ProductImageResponse response = new ProductImageResponse();
        response.setId(image.getId());
        response.setImageUrl(image.getImageUrl());
        return response;
    }
}
