package org.sebastiandev.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sebastiandev.productservice.dto.event.InventoryCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryEventListener {

    // Exemplo de listener no ProductService
    @KafkaListener(topics = "inventory-topic", groupId = "product-group")
    public void handleInventoryCreated(InventoryCreatedEvent event) {
        log.info("Received InventoryCreatedEvent for SKU: {}", event.getSkuCode());

        if (event.isSuccess()) {
            // Atualizar alguma flag local, notificar usuário, etc.
            // Exemplo: productRepository.updateInventoryStatus(...);
            log.info("Inventory created successfully! Quantity: {}", event.getQuantity());
        } else {
            // Tratar caso de falha
            log.warn("Failed to create inventory for SKU: {}", event.getSkuCode());
        }
    }
}
