package app.checkout.services.impl;

import app.checkout.entity.InventoryEntity;
import app.checkout.exception.InsufficientStockException;
import app.checkout.repository.InventoryRepository;
import app.checkout.services.InventoryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class InventoryServiceImpl implements InventoryService {

    @Inject 
    InventoryRepository repo;

    @Override
    public InventoryEntity getInventoryByProductId(String productId) {
        return repo.findByProductId(productId);
    }

    @Override
    public boolean checkAvailability(String productId, int quantity) {
        InventoryEntity inv = repo.findByProductId(productId);
        return inv != null && inv.getAvailableStock() >= quantity;
    }

    @Override
    @Transactional
    public void reserveStock(String productId, int qty) {
        InventoryEntity inv = repo.findByProductId(productId);

        if (inv == null || inv.getAvailableStock() < qty) {
            throw new InsufficientStockException("Not enough stock for product: " + productId);
        }

        inv.setAvailableStock(inv.getAvailableStock() - qty);
        inv.setReservedStock(inv.getReservedStock() + qty);
        inv.setTotalStock(inv.getAvailableStock() + inv.getReservedStock());
    }

    @Override
    @Transactional
    public void releaseStock(String productId, int qty) {
        InventoryEntity inv = repo.findByProductId(productId);

        if (inv == null) return;

        inv.setReservedStock(inv.getReservedStock() - qty);
        inv.setAvailableStock(inv.getAvailableStock() + qty);
        inv.setTotalStock(inv.getAvailableStock() + inv.getReservedStock());
    }

    @Override
    @Transactional
    public void confirmDeduction(String productId, int qty) {
        InventoryEntity inv = repo.findByProductId(productId);

        if (inv == null) return;

        inv.setReservedStock(inv.getReservedStock() - qty);
        inv.setTotalStock(inv.getAvailableStock() + inv.getReservedStock());
    }

    @Override
    @Transactional
    public void addStock(String productId, int qty) {
        InventoryEntity inv = repo.findByProductId(productId);
        if (inv == null) return;

        inv.setAvailableStock(inv.getAvailableStock() + qty);
        inv.setTotalStock(inv.getAvailableStock() + inv.getReservedStock());
    }

    @Override
    public List<InventoryEntity> findLowStock(int threshold) {
        return repo.findLowStock(threshold);
    }
}
