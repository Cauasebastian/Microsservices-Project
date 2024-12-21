package org.sebastiandev.productservice.dto;

import lombok.Builder;

@Builder
public record InventoryRequest(String skuCode, Integer quantity) {
}
