package org.sebastiandev.orderservice.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sebastiandev.orderservice.dto.InventoryResponse;
import org.sebastiandev.orderservice.dto.OrderRequest;
import org.sebastiandev.orderservice.dto.OrderResponse;
import org.sebastiandev.orderservice.model.Order;
import org.sebastiandev.orderservice.model.OrderLineItems;
import org.sebastiandev.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    @Override
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        log.info("Order number: {}", order.getOrderNumber());

        List<OrderLineItems> orderLineItems = new ArrayList<>();
        orderRequest.orderLineItems()
                .forEach(orderLineItemsDTO -> {
                    OrderLineItems orderLineItem = new OrderLineItems();
                    orderLineItem.setSkuCode(orderLineItemsDTO.skuCode());
                    orderLineItem.setPrice(orderLineItemsDTO.price());
                    orderLineItem.setQuantity(orderLineItemsDTO.quantity());
                    orderLineItems.add(orderLineItem);
                });
        order.setOrderLineItems(orderLineItems);

        List<String> skuCode = order.getOrderLineItems().stream().map(OrderLineItems::getSkuCode).toList();

        try {
            // call inventoryService to check inventory and place order if inventory is available
            InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                    .uri("http://Inventory-Service/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCode).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            if (inventoryResponseArray == null) {
                log.error("Inventory response is null, cannot place order");
                return new OrderResponse("Order cannot be placed as inventory service is unavailable", null);
            }

            boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::inStock);

            if (allProductsInStock) {
                orderRepository.save(order);
                log.info("Order placed successfully, Order number: {}", order.getOrderNumber());
                return new OrderResponse("Order placed successfully", order.getOrderNumber());
            } else {
                log.error("Order cannot be placed as some products are out of stock");
                return new OrderResponse("Order cannot be placed as some products are out of stock", null);
            }
        } catch (Exception e) {
            log.error("Error occurred while placing order: {}", e.getMessage());
            return new OrderResponse("Order cannot be placed due to an internal error", null);
        }
    }
}
