package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDTO;
import com.example.orderservice.publisher.OrderPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderPublisher orderPublisher;

    @PostMapping("/{region}")
    public ResponseEntity<String> createOrder(@RequestBody OrderDTO orderDTO, @PathVariable String region) {
        try {
            orderPublisher.sendOrderToPrepare(orderDTO, region);
            return new ResponseEntity<>("Order created and sent to region: " + region, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Order failed to create: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


