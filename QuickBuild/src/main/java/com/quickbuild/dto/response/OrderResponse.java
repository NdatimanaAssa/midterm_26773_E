package com.quickbuild.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private LocalDateTime orderDate;
    private String orderStatus;
    private String paymentMethod;
    private String deliveryMethod;
    private BigDecimal totalAmount;
    private String guestName;
    private String guestPhone;
    private String guestEmail;
    private Long userId;

    // Address summarized or structured
    private String deliveryAddress;

    private List<OrderItemResponse> items;
}
