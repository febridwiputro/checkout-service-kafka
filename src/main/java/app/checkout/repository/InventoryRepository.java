package app.checkout.repository;

import app.checkout.entity.InventoryEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class InventoryRepository implements PanacheRepositoryBase<InventoryEntity, String> {

    public InventoryEntity findByProductId(String productId) {
        return find("productId", productId).firstResult();
    }

    public List<InventoryEntity> findLowStock(int threshold) {
        return list("availableStock < ?1", threshold);
    }
}
