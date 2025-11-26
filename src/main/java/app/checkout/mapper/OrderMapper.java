package app.checkout.mapper;

import app.checkout.dto.CartItemDto;
import app.checkout.dto.OrderItemDto;
import app.checkout.dto.OrderResponseDto;
import app.checkout.dto.ShippingAddressDto;
import app.checkout.entity.OrderEntity;
import app.checkout.entity.OrderItemEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderMapper {

    public OrderItemDto toOrderItemDto(OrderItemEntity entity) {
        if (entity == null) return null;

        OrderItemDto dto = new OrderItemDto();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProductId());
        dto.setProductName(entity.getProductName());
        dto.setQuantity(entity.getQuantity());
        dto.setPrice(entity.getPrice());
        dto.setSubtotal(entity.getSubtotal());
        return dto;
    }

    public OrderItemEntity toOrderItemEntity(CartItemDto dto, OrderEntity order) {
        if (dto == null) return null;

        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(java.util.UUID.randomUUID().toString());
        entity.setOrder(order);
        entity.setProductId(dto.getProductId());
        entity.setQuantity(dto.getQuantity());
        entity.setPrice(dto.getPrice());
        entity.setSubtotal(calculateSubtotal(dto.getQuantity(), dto.getPrice()));

        return entity;
    }

    public OrderResponseDto toOrderResponseDto(OrderEntity entity) {
        if (entity == null) return null;

        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(entity.getId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setCustomerId(entity.getCustomerId());
        dto.setCustomerEmail(entity.getCustomerEmail());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setPaymentStatus(entity.getPaymentStatus());
        dto.setOrderStatus(entity.getOrderStatus());
        dto.setShippingAddress(entity.getShippingAddress());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setProcessInstanceId(entity.getProcessInstanceId());

        if (entity.getItems() != null) {
            dto.setItems(
                    entity.getItems()
                            .stream()
                            .map(this::toOrderItemDto)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public String formatShippingAddress(ShippingAddressDto address) {
        if (address == null) return "";

        return "%s, %s, %s, %s - Phone: %s".formatted(
                address.getStreet(),
                address.getCity(),
                address.getProvince(),
                address.getPostalCode(),
                address.getPhoneNumber()
        );
    }

    private BigDecimal calculateSubtotal(Integer quantity, BigDecimal price) {
        if (quantity == null || price == null) return BigDecimal.ZERO;
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
