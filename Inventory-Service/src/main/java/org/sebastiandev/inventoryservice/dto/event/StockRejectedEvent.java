package org.sebastiandev.inventoryservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockRejectedEvent {
    private Long orderId;
    private String skuCode;
    private int quantity;
    private String reason;
}