package org.sebastiandev.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sebastiandev.inventoryservice.dto.event.OrderCreatedEvent;
import org.sebastiandev.inventoryservice.dto.event.StockRejectedEvent;
import org.sebastiandev.inventoryservice.dto.event.StockReservedEvent;
import org.sebastiandev.inventoryservice.model.Inventory;
import org.sebastiandev.inventoryservice.repository.InventoryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-topic", groupId = "inventory-group")
    @Transactional
    public void handleOrderCreatedEvent(@Payload OrderCreatedEvent event) {
        log.error("............................................................................");
        log.info("Received OrderCreatedEvent for order id: {} with SKU: {}", event.getOrderId(), event.getSkuCode());
        log.info("Quantity: {}", event.getQuantity());
        log.info("skuCode: {}", event.getSkuCode());
        log.info("orderNumber {}", event.getOrderNumber());
        log.error("............................................................................");

        try {
            // Tenta reservar o estoque
            boolean stockReserved = tryReserveStock(event.getSkuCode(), event.getQuantity());

            if (stockReserved) {
                // Envia StockReservedEvent
                StockReservedEvent reservedEvent = StockReservedEvent.builder()
                        .orderId(event.getOrderId())
                        .skuCode(event.getSkuCode())
                        .quantity(event.getQuantity())
                        .build();
                kafkaTemplate.send("stock-topic", reservedEvent);
                log.error("............................................................................");
                log.info("StockReservedEvent sent for order id: {}", event.getOrderId());
                log.error("............................................................................");
            } else {
                // Envia StockRejectedEvent
                StockRejectedEvent rejectedEvent = StockRejectedEvent.builder()
                        .orderId(event.getOrderId())
                        .skuCode(event.getSkuCode())
                        .quantity(event.getQuantity())
                        .reason("Insufficient stock")
                        .build();
                kafkaTemplate.send("reject-topic", rejectedEvent);
                log.error("............................................................................");
                log.info("StockRejectedEvent sent for order id: {}", event.getOrderId());
                log.error("............................................................................");
            }
        } catch (Exception e) {
            // Em caso de erro, envia StockRejectedEvent com a razão do erro
            StockRejectedEvent rejectedEvent = StockRejectedEvent.builder()
                    .orderId(event.getOrderId())
                    .skuCode(event.getSkuCode())
                    .quantity(event.getQuantity())
                    .reason(e.getMessage())
                    .build();
            kafkaTemplate.send("reject-topic", rejectedEvent);
            log.error("............................................................................");
            log.error("Error while trying to reserve stock for order id: {}. Error: {}", event.getOrderId(), e.getMessage());
            log.error("............................................................................");
        }
    }

    private boolean tryReserveStock(String skuCode, int quantity) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode);
        if (inventory != null && inventory.getQuantity() >= quantity) {
            inventory.setQuantity(inventory.getQuantity() - quantity);
            inventoryRepository.save(inventory);
            log.info("Stock reserved for SKU: {}. Quantity reserved: {}", skuCode, quantity);
            return true;
        } else {
            log.warn("Insufficient stock for SKU: {}. Available: {}, Requested: {}", skuCode,
                    inventory != null ? inventory.getQuantity() : 0, quantity);
            return false;
        }
    }
}
