package org.sebastiandev.orderservice.service;

import org.sebastiandev.orderservice.dto.OrderRequest;
import org.sebastiandev.orderservice.dto.OrderResponse;
import org.sebastiandev.orderservice.model.Order;

import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest order);
    Void cancelOrder(Long orderId);
    OrderResponse getOrder(Long orderId);
    List<Order> getAllOrders();
    void deleteAllOrders();
}
