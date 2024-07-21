package org.sebastiandev.orderservice.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sebastiandev.orderservice.dto.OrderRequest;
import org.sebastiandev.orderservice.model.Order;
import org.sebastiandev.orderservice.model.OrderLineItems;
import org.sebastiandev.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;


    @Override
    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        log.info("Order number: {}", order.getOrderNumber());

        List<OrderLineItems> orderLineItems = new ArrayList<>();
        orderRequest.orderLineItems()
                .stream()
                .forEach(orderLineItemsDTO -> {
                    OrderLineItems orderLineItem = new OrderLineItems();
                    orderLineItem.setSkuCode(orderLineItemsDTO.skuCode());
                    orderLineItem.setPrice(orderLineItemsDTO.price());
                    orderLineItem.setQuantity(orderLineItemsDTO.quantity());
                    orderLineItems.add(orderLineItem);
                });
        order.setOrderLineItems(orderLineItems);
        orderRepository.save(order);

        log.info("Order placed successfully");
    }
}
