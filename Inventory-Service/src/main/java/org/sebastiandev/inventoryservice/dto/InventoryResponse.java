package org.sebastiandev.inventoryservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryResponse {
    private String skuCode;
    private boolean inStock;
}