package com.quickbuild.mapper;

import com.quickbuild.domain.Order;
import com.quickbuild.domain.OrderItem;
import com.quickbuild.dto.response.OrderItemResponse;
import com.quickbuild.dto.response.OrderResponse;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderResponse toResponse(Order order) {
        if (order == null) {
            return null;
        }
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderDate(order.getOrderDate());
        response.setOrderStatus(order.getOrderStatus().name());
        response.setPaymentMethod(order.getPaymentMethod().name());
        response.setDeliveryMethod(order.getDeliveryMethod().name());
        response.setTotalAmount(order.getTotalAmount());

        response.setGuestName(order.getGuestName());
        response.setGuestPhone(order.getGuestPhone());
        response.setGuestEmail(order.getGuestEmail());

        if (order.getUser() != null) {
            response.setUserId(order.getUser().getId());
        }

        if (order.getLocation() != null) {
            response.setDeliveryAddress(order.getLocation().getName());
        }

        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                    .map(OrderMapper::toItemResponse)
                    .collect(Collectors.toList());
            response.setItems(itemResponses);
        }

        return response;
    }

    public static OrderItemResponse toItemResponse(OrderItem item) {
        if (item == null) {
            return null;
        }
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        if (item.getProduct() != null) {
            response.setProductId(item.getProduct().getId());
            response.setProductName(item.getProduct().getName());
        }
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setSubtotal(item.getSubtotal());
        return response;
    }
}
