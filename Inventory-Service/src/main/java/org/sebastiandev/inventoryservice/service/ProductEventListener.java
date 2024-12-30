package org.sebastiandev.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sebastiandev.inventoryservice.dto.InventoryRequest;
import org.sebastiandev.inventoryservice.dto.event.ProductCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {

    private final InventoryService inventoryService;

    @KafkaListener(topics = "product-topic", groupId = "inventory-group")
    public void handleProductCreated(ProductCreatedEvent event) {
        log.error("............................................................................");
        log.info("Received ProductCreatedEvent for product id: {}", event.getId());
        log.info("Product name: {}", event.getName());
        log.info("Product quantity: {}", event.getQuantity());
        log.info("Product price: {}", event.getPrice());
        log.info("Product description: {}", event.getDescription());
        log.error("............................................................................");

        InventoryRequest inventoryRequest = InventoryRequest.builder()
                .skuCode(event.getName()) // Assumindo que o nome do produto é usado como SKU
                .quantity(event.getQuantity())
                .build();

        inventoryService.createInventory(inventoryRequest);
    }
}
