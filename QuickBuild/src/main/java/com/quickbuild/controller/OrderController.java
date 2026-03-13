package com.quickbuild.controller;

import com.quickbuild.dto.request.GuestOrderRequest;
import com.quickbuild.dto.request.UserOrderRequest;
import com.quickbuild.dto.response.OrderResponse;
import com.quickbuild.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/guest")
    public ResponseEntity<OrderResponse> createGuestOrder(@Valid @RequestBody GuestOrderRequest request) {
        return new ResponseEntity<>(orderService.createGuestOrder(request), HttpStatus.CREATED);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<OrderResponse> createUserOrder(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UserOrderRequest request) {
        return new ResponseEntity<>(orderService.createUserOrder(userId, request), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }
}
