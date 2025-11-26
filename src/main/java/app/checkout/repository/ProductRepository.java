package app.checkout.repository;

import app.checkout.entity.ProductEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProductRepository implements PanacheRepositoryBase<ProductEntity, String> {

    public ProductEntity findByProductId(String productId) {
        return find("id", productId).firstResult();
    }

    public List<ProductEntity> findActiveProducts() {
        return find("isActive", true).list();
    }

    public List<ProductEntity> findByCategory(String category) {
        return find("category = ?1 AND isActive = true", category).list();
    }

    public List<ProductEntity> searchByName(String keyword) {
        return find("LOWER(productName) LIKE LOWER(?1) AND isActive = true", "%" + keyword + "%").list();
    }
}