package org.sebastiandev.inventoryservice.dto;

import lombok.Builder;

@Builder
public record InventoryResponse(String skuCode, boolean inStock) {
}
