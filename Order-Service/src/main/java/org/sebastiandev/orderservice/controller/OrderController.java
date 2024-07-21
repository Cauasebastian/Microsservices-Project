package org.sebastiandev.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.sebastiandev.orderservice.dto.OrderRequest;
import org.sebastiandev.orderservice.dto.OrderResponse;
import org.sebastiandev.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest order) {
        OrderResponse orderResponse = orderService.placeOrder(order);
        if (orderResponse.orderNumber() != null) {
            return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(orderResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
