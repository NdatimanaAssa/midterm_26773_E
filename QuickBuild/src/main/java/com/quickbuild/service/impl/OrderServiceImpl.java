package com.quickbuild.service.impl;

import com.quickbuild.domain.*;
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
    private final ProvinceRepository provinceRepository;
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

        // Location Setup
        Location location = new Location();
        location.setDistrict(locationReq.getDistrict());
        location.setSector(locationReq.getSector());
        location.setStreet(locationReq.getStreet());

        Province province = null;
        if (locationReq.getProvinceCode() != null) {
            province = provinceRepository.findAll().stream()
                    .filter(p -> p.getCode().equalsIgnoreCase(locationReq.getProvinceCode()))
                    .findFirst().orElseThrow(() -> new ResourceNotFoundException("Province code not found"));
        } else if (locationReq.getProvinceName() != null) {
            province = provinceRepository.findAll().stream()
                    .filter(p -> p.getName().equalsIgnoreCase(locationReq.getProvinceName()))
                    .findFirst().orElseThrow(() -> new ResourceNotFoundException("Province name not found"));
        } else {
            throw new IllegalArgumentException("Province info must be provided");
        }
        location.setProvince(province);
        location = locationRepository.save(location);
        order.setLocation(location);

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
