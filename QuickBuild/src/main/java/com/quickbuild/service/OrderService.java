package com.quickbuild.service;

import com.quickbuild.dto.request.GuestOrderRequest;
import com.quickbuild.dto.request.UserOrderRequest;
import com.quickbuild.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createGuestOrder(GuestOrderRequest request);

    OrderResponse createUserOrder(Long userId, UserOrderRequest request);

    List<OrderResponse> getUserOrders(Long userId);
}
