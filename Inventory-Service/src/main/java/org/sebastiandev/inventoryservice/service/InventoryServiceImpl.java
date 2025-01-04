package org.sebastiandev.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sebastiandev.inventoryservice.dto.InventoryRequest;
import org.sebastiandev.inventoryservice.dto.InventoryResponse;
import org.sebastiandev.inventoryservice.dto.event.InventoryCreatedEvent;
import org.sebastiandev.inventoryservice.model.Inventory;
import org.sebastiandev.inventoryservice.repository.InventoryRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode) {
        log.info("Checking Inventory");
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .inStock(inventory.getQuantity() > 0)
                                .build()
                ).toList();
    }

    @Override
    @Transactional
    public void createInventory(InventoryRequest inventoryRequest) {
        log.info("Creating inventory for SKU: {}", inventoryRequest.getSkuCode());
        Inventory inventory = new Inventory();
        inventory.setSkuCode(inventoryRequest.getSkuCode());
        inventory.setQuantity(inventoryRequest.getQuantity());
        inventoryRepository.save(inventory);

        // Publica o evento InventoryCreatedEvent
        InventoryCreatedEvent event = InventoryCreatedEvent.builder()
                .skuCode(inventory.getSkuCode())
                .quantity(inventory.getQuantity())
                .inStock(inventory.getQuantity() > 0)
                .build();

        kafkaTemplate.send("inventory-topic", event);
        log.info("InventoryCreatedEvent sent for SKU: {}", inventory.getSkuCode());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> AllInventory() {
        try {
            return inventoryRepository.findAll();
        } catch (Exception e) {
            log.error("Error while fetching inventory: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public void deleteAllInventory() {
        inventoryRepository.deleteAll();
    }
}
