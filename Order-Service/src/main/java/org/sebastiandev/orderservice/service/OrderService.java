package org.sebastiandev.orderservice.service;

import org.sebastiandev.orderservice.dto.OrderRequest;
import org.sebastiandev.orderservice.dto.OrderResponse;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest order);
    Void cancelOrder(Long orderId);
    OrderResponse getOrder(Long orderId);
}
