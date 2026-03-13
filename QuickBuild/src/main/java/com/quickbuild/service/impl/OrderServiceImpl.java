package com.quickbuild.service.impl;

import com.quickbuild.domain.*;
import com.quickbuild.domain.enums.ELocationType;
import com.quickbuild.domain.enums.OrderStatus;
import com.quickbuild.dto.request.GuestOrderRequest;
import com.quickbuild.dto.request.OrderItemRequest;
import com.quickbuild.dto.request.UserOrderRequest;
import com.quickbuild.dto.request.LocationRequest;
import com.quickbuild.dto.response.OrderResponse;
import com.quickbuild.exception.InsufficientStockException;
import com.quickbuild.exception.ResourceNotFoundException;
import com.quickbuild.mapper.OrderMapper;
import com.quickbuild.repository.*;
import com.quickbuild.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    @Override
    @Transactional
    public OrderResponse createGuestOrder(GuestOrderRequest request) {
        Order order = new Order();
        order.setGuestName(request.getGuestName());
        order.setGuestPhone(request.getGuestPhone());
        order.setGuestEmail(request.getGuestEmail());

        setupOrderDetails(order, request.getPaymentMethod(), request.getDeliveryMethod(), request.getLocation(),
                request.getItems());

        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse createUserOrder(Long userId, UserOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Order order = new Order();
        order.setUser(user);

        setupOrderDetails(order, request.getPaymentMethod(), request.getDeliveryMethod(), request.getLocation(),
                request.getItems());

        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(OrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void setupOrderDetails(Order order, com.quickbuild.domain.enums.PaymentMethod paymentMethod,
            com.quickbuild.domain.enums.DeliveryMethod deliveryMethod,
            LocationRequest locationReq, List<OrderItemRequest> itemRequests) {
        order.setPaymentMethod(paymentMethod);
        order.setDeliveryMethod(deliveryMethod);
        order.setOrderStatus(OrderStatus.PENDING);

        // Location Setup (Linking to an existing Village)
        Location village = null;
        if (locationReq != null) {
            if (locationReq.getVillageCode() != null) {
                village = locationRepository.findByCode(locationReq.getVillageCode())
                        .orElseThrow(() -> new ResourceNotFoundException("Village code not found"));
            } else if (locationReq.getVillageName() != null) {
                village = locationRepository.findByName(locationReq.getVillageName())
                        .orElseThrow(() -> new ResourceNotFoundException("Village name not found"));
            } else {
                throw new IllegalArgumentException("Village code or name must be provided");
            }

            if (village.getType() != ELocationType.VILLAGE) {
                throw new IllegalArgumentException("The provided location is not a VILLAGE");
            }
        } else {
            throw new IllegalArgumentException("Location information is required");
        }
        order.setLocation(village);

        // Items Setup
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : itemRequests) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + itemReq.getProductId()));

            if (product.getStockQuantity() < itemReq.getQuantity()) {
                throw new InsufficientStockException("Not enough stock for product: " + product.getName());
            }

            // Decrease stock
            product.setStockQuantity(product.getStockQuantity() - itemReq.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));

            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);
    }
}
