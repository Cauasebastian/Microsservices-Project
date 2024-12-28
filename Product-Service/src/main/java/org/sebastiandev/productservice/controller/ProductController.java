package org.sebastiandev.productservice.controller;

import lombok.RequiredArgsConstructor;
import org.sebastiandev.productservice.dto.ProductRequest;
import org.sebastiandev.productservice.dto.ProductResponse;
import org.sebastiandev.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest) {
        productService.createProduct(productRequest);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }
    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProduct(@PathVariable String productId) {
        return productService.getProduct(productId);
    }

}
