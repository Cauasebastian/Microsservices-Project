package org.sebastiandev.orderservice.service;

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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

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
                .toList();

        // Faça a solicitação ao serviço de inventário para verificar o estoque
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        // Converta o array de InventoryResponse em um mapa para facilitar a verificação
        Map<String, InventoryResponse> inventoryResponseMap = Arrays.stream(inventoryResponseArray)
                .collect(Collectors.toMap(InventoryResponse::skuCode, response -> response));

        // Verifique se todos os itens do pedido estão em estoque e a quantidade é suficiente
        boolean allProductsInStock = orderLineItems.stream().allMatch(item -> {
            InventoryResponse inventoryResponse = inventoryResponseMap.get(item.getSkuCode());
            return inventoryResponse != null && inventoryResponse.inStock() && inventoryResponse.availableQuantity() >= item.getQuantity();
        });

        if (!allProductsInStock) {
            return OrderResponse.builder()
                    .message("Some products are out of stock or insufficient quantity")
                    .build();
        }

        // Processar o pedido se tudo está em estoque
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderLineItems(orderLineItems);
        orderRepository.save(order);

        return OrderResponse.builder()
                .orderNumber(order.getOrderNumber())
                .message("Order placed successfully")
                .build();
    }
}
