package org.sebastiandev.inventoryservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor      // Construtor sem argumentos
@AllArgsConstructor
public class ProductCreatedEvent {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
}
