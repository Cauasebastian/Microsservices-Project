package org.sebastiandev.orderservice.dto;


import java.util.List;

public record OrderRequest (List<OrderLineItemsDTO> orderLineItems) {
}
