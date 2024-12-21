package org.sebastiandev.inventoryservice.dto;

import lombok.Builder;

@Builder
public record InventoryRequest(String skuCode, Integer quantity) {
}
