package app.checkout.mapper;

import app.checkout.dto.ProductDto;
import app.checkout.entity.InventoryEntity;
import app.checkout.entity.ProductEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductMapper {

    public ProductDto toDto(ProductEntity product, InventoryEntity inventory) {
        Integer availableStock = (inventory != null) ? inventory.getAvailableStock() : 0;
        
        return new ProductDto(
            product.getId(),
            product.getProductName(),
            product.getDescription(),
            product.getPrice(),
            availableStock,
            product.getCategory(),
            generateImageUrl(product.getId(), product.getProductName()),
            product.getIsActive(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }

    private String generateImageUrl(String productId, String productName) {
        String encodedName = productName.replace(" ", "+");
        return "https://via.placeholder.com/300x300/0066cc/ffffff?text=" + encodedName;
    }
}