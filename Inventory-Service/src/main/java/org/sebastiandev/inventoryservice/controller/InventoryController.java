package org.sebastiandev.inventoryservice.controller;

import org.sebastiandev.inventoryservice.dto.InventoryRequest;
import org.sebastiandev.inventoryservice.dto.InventoryResponse;
import org.sebastiandev.inventoryservice.model.Inventory;
import org.sebastiandev.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        return inventoryService.isInStock(skuCode);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public boolean createInventory(@RequestBody InventoryRequest inventoryRequest) {
        inventoryService.createInventory(inventoryRequest);
        return true;
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<Inventory> AllInventory() {
        return inventoryService.AllInventory();
    }
    // Delete all inventory, for testing purposes
    @DeleteMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllInventory() {
        inventoryService.deleteAllInventory();
    }
}
