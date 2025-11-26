package app.checkout.repository;

import app.checkout.entity.OrderItemEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class OrderItemRepository implements PanacheRepositoryBase<OrderItemEntity, String> {

    public List<OrderItemEntity> findByOrderId(String orderId) {
        return list("order.id", orderId);
    }

    public void deleteByOrderId(String orderId) {
        delete("order.id", orderId);
    }

    public boolean existsByProductId(String productId) {
        return count("productId", productId) > 0;
    }
}
