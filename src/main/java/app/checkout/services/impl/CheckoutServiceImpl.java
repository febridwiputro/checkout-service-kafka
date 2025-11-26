package app.checkout.services.impl;

import app.checkout.dto.CheckoutRequestDto;
import app.checkout.dto.OrderResponseDto;
import app.checkout.exception.InsufficientStockException;
import app.checkout.kafka.producer.CheckoutOrderProducer;
import app.checkout.services.CheckoutService;
import app.checkout.services.InventoryService;
import app.checkout.services.OrderService;
import app.checkout.services.ProductService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CheckoutServiceImpl implements CheckoutService {

    @Inject
    InventoryService inventoryService;

    @Inject
    OrderService orderService;

    @Inject
    ProductService productService;

    @Inject
    CheckoutOrderProducer producer;

    @Override
    @Transactional
    public OrderResponseDto processCheckout(CheckoutRequestDto request) {

        // Step 1: Validate
        validateCart(request);
        validateProducts(request);
        checkStock(request);

        // Step 2: Reserve stock
        reserveStock(request);

        // Step 3: Create order (returns PENDING status)
        var order = orderService.createOrder(request);

        // Step 4: Process payment (dummy)
        boolean paymentSuccess = true;

        // Step 5: Update order status
        orderService.updateOrderStatus(
                order.getId(),
                paymentSuccess ? "CONFIRMED" : "CANCELLED",
                paymentSuccess ? "PAID" : "FAILED"
        );

        // Step 6: Confirm stock deduction
        if (paymentSuccess) {
            confirmStockDeduction(request);
        }

        // Step 7: Get updated order
        OrderResponseDto updatedOrder = orderService.getOrderById(order.getId());

        // Step 8: Send to Kafka with complete data
        producer.send(
                updatedOrder.getId(),
                "SUCCESS",
                updatedOrder.getOrderNumber(),
                updatedOrder.getCustomerId()
        );

        return updatedOrder;
    }

    @Override
    public OrderResponseDto getOrderStatus(String orderId) {
        return orderService.getOrderById(orderId);
    }

    private void validateCart(CheckoutRequestDto request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }
    }

    private void validateProducts(CheckoutRequestDto request) {
        for (var item : request.getItems()) {
            var product = productService.getProductById(item.getProductId());
            if (product == null) {
                throw new IllegalArgumentException(
                        "Product not found: " + item.getProductId()
                );
            }
            if (!product.getIsActive()) {
                throw new IllegalArgumentException(
                        "Product is inactive: " + item.getProductId()
                );
            }
        }
    }

    private void checkStock(CheckoutRequestDto request) {
        for (var item : request.getItems()) {
            if (!inventoryService.checkAvailability(item.getProductId(), item.getQuantity())) {
                throw new InsufficientStockException(
                        "Insufficient stock for " + item.getProductId()
                );
            }
        }
    }

    private void reserveStock(CheckoutRequestDto request) {
        for (var item : request.getItems()) {
            inventoryService.reserveStock(item.getProductId(), item.getQuantity());
        }
    }

    private void confirmStockDeduction(CheckoutRequestDto request) {
        for (var item : request.getItems()) {
            inventoryService.confirmDeduction(item.getProductId(), item.getQuantity());
        }
    }
}