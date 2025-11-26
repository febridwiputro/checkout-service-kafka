package app.checkout.services.impl;

import app.checkout.dto.CheckoutRequestDto;
import app.checkout.dto.OrderResponseDto;
import app.checkout.dto.ProductDto;
import app.checkout.entity.OrderEntity;
import app.checkout.entity.OrderItemEntity;
import app.checkout.mapper.OrderMapper;
import app.checkout.repository.OrderItemRepository;
import app.checkout.repository.OrderRepository;
import app.checkout.services.OrderService;
import app.checkout.services.ProductService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;

@ApplicationScoped
public class OrderServiceImpl implements OrderService {

    @Inject
    OrderRepository repo;

    @Inject
    OrderItemRepository orderItemRepo;

    @Inject
    OrderMapper mapper;

    @Inject
    ProductService productService;

    @Override
    @Transactional
    public OrderResponseDto createOrder(CheckoutRequestDto request) {

        // Step 1: Calculate total
        BigDecimal total = BigDecimal.ZERO;
        for (var item : request.getItems()) {
            BigDecimal subtotal = item.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(subtotal);
        }

        // Step 2: Create order
        OrderEntity order = new OrderEntity();
        order.setOrderNumber("ORD-" + System.currentTimeMillis());
        order.setCustomerId(request.getCustomerId());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setShippingAddress(
                mapper.formatShippingAddress(request.getShippingAddress())
        );
        order.setPaymentMethod(request.getPaymentMethod());
        order.setOrderStatus("PENDING");
        order.setPaymentStatus("PENDING");
        order.setTotalAmount(total);

        repo.persist(order);

        // Step 3: Create items with product name from database
        for (var item : request.getItems()) {
            ProductDto product = productService.getProductById(item.getProductId());
            String productName = (product != null) ? product.getProductName() : "Unknown Product";

            OrderItemEntity oi = new OrderItemEntity();
            oi.setOrder(order);
            oi.setProductId(item.getProductId());
            oi.setProductName(productName);
            oi.setQuantity(item.getQuantity());
            oi.setPrice(item.getPrice());
            oi.setSubtotal(
                    item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );

            orderItemRepo.persist(oi);
            order.getItems().add(oi);
        }

        return mapper.toOrderResponseDto(order);
    }

    @Override
    @Transactional
    public OrderResponseDto getOrderById(String id) {
        OrderEntity order = repo.findById(id);
        if (order == null) return null;
        order.getItems().size();

        return mapper.toOrderResponseDto(order);
    }

    @Override
    @Transactional
    public void updateOrderStatus(String orderId, String orderStatus, String paymentStatus) {
        OrderEntity order = repo.findById(orderId);
        if (order == null) return;

        order.setOrderStatus(orderStatus);
        order.setPaymentStatus(paymentStatus);
    }
}