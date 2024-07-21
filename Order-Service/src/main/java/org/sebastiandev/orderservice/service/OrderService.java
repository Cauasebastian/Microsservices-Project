package org.sebastiandev.orderservice.service;

import org.sebastiandev.orderservice.dto.OrderRequest;

public interface OrderService {
    void placeOrder(OrderRequest order);
}
