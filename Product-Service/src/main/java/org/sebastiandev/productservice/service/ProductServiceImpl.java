package org.sebastiandev.productservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sebastiandev.productservice.dto.InventoryRequest;
import org.sebastiandev.productservice.dto.ProductRequest;
import org.sebastiandev.productservice.dto.ProductResponse;
import org.sebastiandev.productservice.dto.event.ProductCreatedEvent;
import org.sebastiandev.productservice.model.Product;
import org.sebastiandev.productservice.repository.ProductRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
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

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    @Override
    public void createProduct(ProductRequest productRequest) {
        log.error("............................................................................");
        log.info("Creating product with name: {}", productRequest.name());
        log.error("............................................................................");
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .build();
        productRepository.save(product);
        log.error("............................................................................");
        log.info("Product created with id: {}", product.getId());
        log.error("............................................................................");

        // Criar e publicar o evento
        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(productRequest.quantity())
                .build();

        kafkaTemplate.send("product-topic", event);
        log.error("............................................................................");
        log.info("ProductCreatedEvent published for product id: {}", product.getId());
        log.error("............................................................................");
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

    @Override
    public ProductResponse getProduct(String productId) {
        log.info("Getting product with id: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

    @Override
    public void updateProduct(Long productId, ProductRequest productRequest) {

    }

    @Override
    public void deleteAllProducts() {
        productRepository.deleteAll();
    }
}
