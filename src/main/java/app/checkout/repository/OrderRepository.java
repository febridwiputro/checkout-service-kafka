package app.checkout.repository;

import app.checkout.entity.OrderEntity;
import app.checkout.entity.OrderItemEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class OrderRepository implements PanacheRepositoryBase<OrderEntity, String> {

    public OrderEntity findByOrderNumber(String orderNumber) {
        return find("orderNumber", orderNumber).firstResult();
    }

    public List<OrderEntity> findByCustomerId(String customerId) {
        return list("customerId", customerId);
    }

    public List<OrderEntity> findByOrderStatus(String orderStatus) {
        return list("orderStatus", orderStatus);
    }

    public boolean existsByOrderNumber(String orderNumber) {
        return count("orderNumber", orderNumber) > 0;
    }
}
