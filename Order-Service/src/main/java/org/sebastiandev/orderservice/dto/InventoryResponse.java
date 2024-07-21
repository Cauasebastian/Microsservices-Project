package org.sebastiandev.orderservice.dto;

import lombok.Builder;

@Builder
public record InventoryResponse(String skuCode, boolean inStock) {
}
