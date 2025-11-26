package app.checkout.services.impl;

import app.checkout.dto.ProductDto;
import app.checkout.entity.InventoryEntity;
import app.checkout.entity.ProductEntity;
import app.checkout.mapper.ProductMapper;
import app.checkout.repository.InventoryRepository;
import app.checkout.repository.ProductRepository;
import app.checkout.services.ProductService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductServiceImpl implements ProductService {

    @Inject
    ProductRepository productRepository;

    @Inject
    InventoryRepository inventoryRepository;

    @Inject
    ProductMapper productMapper;

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findActiveProducts().stream()
            .map(product -> {
                InventoryEntity inventory = inventoryRepository.findByProductId(product.getId());
                return productMapper.toDto(product, inventory);
            })
            .collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductById(String productId) {
        ProductEntity product = productRepository.findByProductId(productId);
        
        if (product == null || !product.getIsActive()) {
            return null;
        }

        InventoryEntity inventory = inventoryRepository.findByProductId(productId);
        return productMapper.toDto(product, inventory);
    }

    @Override
    public List<ProductDto> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
            .map(product -> {
                InventoryEntity inventory = inventoryRepository.findByProductId(product.getId());
                return productMapper.toDto(product, inventory);
            })
            .collect(Collectors.toList());
    }
}