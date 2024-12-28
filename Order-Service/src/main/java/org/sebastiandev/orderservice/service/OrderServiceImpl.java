package org.sebastiandev.orderservice.service;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sebastiandev.orderservice.dto.InventoryResponse;
import org.sebastiandev.orderservice.dto.OrderRequest;
import org.sebastiandev.orderservice.dto.OrderResponse;
import org.sebastiandev.orderservice.event.OrderPlacedEvent;
import org.sebastiandev.orderservice.model.Order;
import org.sebastiandev.orderservice.model.OrderLineItems;
import org.sebastiandev.orderservice.repository.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final ObservationRegistry observationRegistry;
    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;

    @Override
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        // Construa os itens do pedido
        List<OrderLineItems> orderLineItems = new ArrayList<>();
        orderRequest.orderLineItems().forEach(item -> {
            OrderLineItems lineItem = new OrderLineItems();
            lineItem.setSkuCode(item.skuCode());
            lineItem.setPrice(item.price());
            lineItem.setQuantity(item.quantity());
            orderLineItems.add(lineItem);
        });

        // Extraia os códigos SKU dos itens do pedido
        List<String> skuCodes = orderLineItems.stream()
                .map(OrderLineItems::getSkuCode)
                .collect(Collectors.toList());

        // Crie uma observação para o serviço de inventário
        Observation inventoryServiceObservation = Observation.createNotStarted("inventory-service-lookup",
                this.observationRegistry);
        inventoryServiceObservation.lowCardinalityKeyValue("call", "inventory-service");
        return inventoryServiceObservation.observe(() -> {
        // Faça a solicitação ao serviço de inventário para verificar o estoque
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes.toArray()).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        // Crie um mapa de SKU para o status de estoque
        Map<String, Boolean> inventoryMap = Arrays.stream(inventoryResponseArray)
                .collect(Collectors.toMap(InventoryResponse::skuCode, InventoryResponse::inStock));

        // Verifique se todos os produtos estão em estoque
        boolean allProductsInStock = orderLineItems.stream()
                .allMatch(item -> inventoryMap.getOrDefault(item.getSkuCode(), false));

        if (!allProductsInStock) {
            return OrderResponse.builder()
                    .message("Some products are out of stock or insufficient quantity")
                    .build();
        }

        // Processar o pedido se tudo está em estoque
            if(allProductsInStock){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderLineItems(orderLineItems);
        orderRepository.save(order);
        kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));

        return OrderResponse.builder()
                .orderNumber(order.getOrderNumber())
                .message("Order placed successfully")
                .build();
    }else {
                throw new IllegalArgumentException("Product is not in stock, please try again later");
            }
        });
    }

    @Override
    public Void cancelOrder(Long orderId) {
        return null;
    }

    @Override
    public OrderResponse getOrder(Long orderId) {
        return null;
    }
}
