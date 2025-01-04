package org.sebastiandev.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.sebastiandev.orderservice.dto.OrderRequest;
import org.sebastiandev.orderservice.dto.OrderResponse;
import org.sebastiandev.orderservice.dto.event.OrderCreatedEvent;
import org.sebastiandev.orderservice.model.Order;
import org.sebastiandev.orderservice.model.OrderLineItems;
import org.sebastiandev.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    @Override
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        // Constrói os itens do pedido
        List<OrderLineItems> orderLineItems = new ArrayList<>();
        orderRequest.getOrderLineItems().forEach(item -> {
            OrderLineItems lineItem = new OrderLineItems();
            lineItem.setSkuCode(item.getSkuCode());
            lineItem.setPrice(item.getPrice());
            lineItem.setQuantity(item.getQuantity());
            orderLineItems.add(lineItem);
        });

        // Cria o pedido e salva no banco com status PENDING
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderLineItems(orderLineItems);
        order.setStatus(Order.OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);

        // Cria o evento OrderCreatedEvent e envia para o Kafka
        OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
                .orderId(savedOrder.getId())
                .orderNumber(savedOrder.getOrderNumber())
                .skuCode(savedOrder.getOrderLineItems().get(0).getSkuCode()) // Supondo um SKU por pedido
                .quantity(savedOrder.getOrderLineItems().get(0).getQuantity())
                .build();

        orderEventProducer.sendOrderCreatedEvent(orderCreatedEvent);

        return OrderResponse.builder()
                .orderNumber(savedOrder.getOrderNumber())
                .message("Order placed successfully")
                .build();
    }

    @Override
    public Void cancelOrder(Long orderId) {
        // Implementar lógica para cancelar o pedido
        return null;
    }

    @Override
    public OrderResponse getOrder(Long orderId) {
        // Implementar lógica para obter um pedido
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        orders.forEach(order -> Hibernate.initialize(order.getOrderLineItems())); // Inicializa explicitamente
        return orders;
    }

    @Override
    public void deleteAllOrders() {
        orderRepository.deleteAll();
    }
}
