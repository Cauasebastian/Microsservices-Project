package org.sebastiandev.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sebastiandev.inventoryservice.dto.InventoryRequest;
import org.sebastiandev.inventoryservice.dto.InventoryResponse;
import org.sebastiandev.inventoryservice.dto.event.ProductCreatedEvent;
import org.sebastiandev.inventoryservice.model.Inventory;
import org.sebastiandev.inventoryservice.repository.InventoryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;


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

    @Transactional
    public void createInventory(InventoryRequest inventoryRequest) {
        log.info("Creating inventory for SKU: {}", inventoryRequest.skuCode());
        Inventory inventory = new Inventory();
        inventory.setSkuCode(inventoryRequest.skuCode());
        inventory.setQuantity(inventoryRequest.quantity());
        inventoryRepository.save(inventory);
        log.info("Inventory created for SKU: {}", inventoryRequest.skuCode());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> AllInventory() {
        return inventoryRepository.findAll().stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .inStock(inventory.getQuantity() > 0)
                                .build()
                ).toList();
    }

    @Override
    public void deleteAllInventory() {
        inventoryRepository.deleteAll();
    }
}
