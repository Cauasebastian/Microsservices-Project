package org.sebastiandev.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.sebastiandev.orderservice.dto.OrderRequest;
import org.sebastiandev.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest order) {
        orderService.placeOrder(order);
        return "Order placed successfully";
    }
}
