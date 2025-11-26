package app.checkout.services;

import app.checkout.dto.CheckoutRequestDto;
import app.checkout.dto.OrderResponseDto;

public interface OrderService {
    OrderResponseDto createOrder(CheckoutRequestDto request);
    OrderResponseDto getOrderById(String id);
    void updateOrderStatus(String orderId, String orderStatus, String paymentStatus);
}
