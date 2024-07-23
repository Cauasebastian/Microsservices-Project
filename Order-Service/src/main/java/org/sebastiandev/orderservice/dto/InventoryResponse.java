package org.sebastiandev.orderservice.dto;

import lombok.Builder;

@Builder
public record InventoryResponse(String skuCode, boolean inStock, int availableQuantity) {

    // Método para obter a quantidade disponível
    public int availableQuantity() {
        return availableQuantity;
    }
}