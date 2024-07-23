package org.sebastiandev.orderservice.dto;

import lombok.Builder;

@Builder
public record OrderResponse(String orderNumber, String message) {
}
