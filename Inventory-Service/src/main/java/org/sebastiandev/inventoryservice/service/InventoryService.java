package org.sebastiandev.inventoryservice.service;

import org.sebastiandev.inventoryservice.dto.InventoryRequest;
import org.sebastiandev.inventoryservice.dto.InventoryResponse;

import java.util.List;

public interface InventoryService {
    List<InventoryResponse> isInStock(List<String> skuCode);
    void createInventory(InventoryRequest inventoryRequest);
}
