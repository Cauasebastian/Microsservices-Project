package org.sebastiandev.productservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sebastiandev.productservice.dto.InventoryRequest;
import org.sebastiandev.productservice.dto.ProductRequest;
import org.sebastiandev.productservice.dto.ProductResponse;
import org.sebastiandev.productservice.model.Product;
import org.sebastiandev.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final WebClient.Builder webClientBuilder;

    @Override
    public void createProduct(ProductRequest productRequest) {
        log.info("Creating product with name: {}", productRequest.name());
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .build();
        productRepository.save(product);
        log.info("Product created with id: {}", product.getId());

        // Criar Inventory Request
        InventoryRequest inventoryRequest = InventoryRequest.builder()
                .skuCode(product.getName()) // Nome do produto é usado como skuCode
                .quantity(productRequest.quantity())
                .build();

        // Enviar solicitação para Inventory-Service
        sendInventoryRequest(inventoryRequest).thenAccept(response -> {
            if (response) {
                log.info("Inventory created successfully for product: {}", product.getId());
            } else {
                log.warn("Failed to create inventory for product: {}", product.getId());
            }
        });
    }

    @CircuitBreaker(name = "inventory", fallbackMethod = "inventoryFallback")
    @Retry(name = "inventory")
    @TimeLimiter(name = "inventory")
    public CompletableFuture<Boolean> sendInventoryRequest(InventoryRequest inventoryRequest) {
        return CompletableFuture.supplyAsync(() ->
                webClientBuilder.build().post()
                        .uri("http://inventory-service/inventory")
                        .bodyValue(inventoryRequest)
                        .retrieve()
                        .bodyToMono(Boolean.class)
                        .block()
        );
    }

    public CompletableFuture<Boolean> inventoryFallback(InventoryRequest inventoryRequest, Throwable throwable) {
        log.error("Failed to create inventory for skuCode: {}. Reason: {}", inventoryRequest.skuCode(), throwable.getMessage());
        // Implementar lógica de fallback, como salvar localmente ou notificar o administrador
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        log.info("Getting all products");
        return productRepository.findAll().stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .build())
                .collect(Collectors.toList());
    }
}
