package org.sebastiandev.orderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.sebastiandev.orderservice.dto.event.StockRejectedEvent;
import org.sebastiandev.orderservice.dto.event.StockReservedEvent;
import org.sebastiandev.orderservice.model.Order;
import org.sebastiandev.orderservice.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper; // Jackson ObjectMapper para conversão manual

    @KafkaListener(topics = {"stock-topic", "reject-topic"}, groupId = "order-group")
    @Transactional
    public void handleEvent(ConsumerRecord<String, Object> record) {
        Object event = record.value();  // Extrai o payload (valor) da mensagem Kafka

        try {
            if (event instanceof LinkedHashMap) {
                // Converte o LinkedHashMap para o tipo de evento apropriado
                if (isStockReservedEvent((LinkedHashMap<?, ?>) event)) {
                    StockReservedEvent stockReservedEvent = objectMapper.convertValue(event, StockReservedEvent.class);
                    log.error("............................................................................");
                    log.info("Received StockReservedEvent for order id: {}", stockReservedEvent.getOrderId());
                    log.error("............................................................................");
                    updateOrderStatus(stockReservedEvent.getOrderId(), Order.OrderStatus.CONFIRMED);
                } else if (isStockRejectedEvent((LinkedHashMap<?, ?>) event)) {
                    StockRejectedEvent stockRejectedEvent = objectMapper.convertValue(event, StockRejectedEvent.class);
                    log.error("............................................................................");
                    log.info("Received StockRejectedEvent for order id: {}. Reason: {}", stockRejectedEvent.getOrderId(), stockRejectedEvent.getReason());
                    log.error("............................................................................");
                    updateOrderStatus(stockRejectedEvent.getOrderId(), Order.OrderStatus.CANCELLED);
                } else {
                    log.error("Unknown event structure: {}", event);
                }
            } else {
                log.error("Event is not a LinkedHashMap: {}", event.getClass());
            }
        } catch (Exception e) {
            log.error("Error deserializing event: {}", e.getMessage(), e);
        }
    }

    private boolean isStockReservedEvent(LinkedHashMap<?, ?> event) {
        // Valida a estrutura para identificar se o evento é do tipo StockReservedEvent
        return event.containsKey("orderId") && event.containsKey("skuCode");  // Ajuste conforme a estrutura real
    }

    private boolean isStockRejectedEvent(LinkedHashMap<?, ?> event) {
        // Valida a estrutura para identificar se o evento é do tipo StockRejectedEvent
        return event.containsKey("orderId") && event.containsKey("reason");
    }

    private void updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
            log.info("Order id: {} status updated to {}", orderId, status);
        } else {
            log.error("Order not found with id: {}", orderId);
        }
    }
}
