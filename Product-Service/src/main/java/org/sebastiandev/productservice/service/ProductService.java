package org.sebastiandev.productservice.service;

import org.sebastiandev.productservice.dto.ProductRequest;
import org.sebastiandev.productservice.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    void createProduct(ProductRequest productRequest);
    List<ProductResponse> getAllProducts();
    ProductResponse getProduct(String productId);
    void updateProduct(Long productId, ProductRequest productRequest);
    void deleteAllProducts();
}
