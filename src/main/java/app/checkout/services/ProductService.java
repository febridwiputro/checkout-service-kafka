package app.checkout.services;

import app.checkout.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> getAllProducts();
    ProductDto getProductById(String productId);
    List<ProductDto> getProductsByCategory(String category);
}