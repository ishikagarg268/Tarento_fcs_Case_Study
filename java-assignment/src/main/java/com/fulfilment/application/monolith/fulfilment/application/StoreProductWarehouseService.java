package com.fulfilment.application.monolith.fulfilment.application;

import com.fulfilment.application.monolith.fulfilment.adapters.database.StoreProductWarehouse;
import com.fulfilment.application.monolith.fulfilment.domain.ports.StoreProductWarehouseStore;
import com.fulfilment.application.monolith.fulfilment.domain.validators.StoreProductWarehouseValidator;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class StoreProductWarehouseService {

    private final StoreProductWarehouseStore repository;
    private final StoreProductWarehouseValidator validator;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public StoreProductWarehouseService(
            StoreProductWarehouseStore repository,
            StoreProductWarehouseValidator validator,
            ProductRepository productRepository,
            WarehouseRepository warehouseRepository) {
        this.repository = repository;
        this.validator = validator;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional
    public void associate(Long storeId, Long productId, Long warehouseId) {

        Store store = Store.findById(storeId);
        if (store == null) {
            throw new IllegalArgumentException(
                    "Store with id " + storeId + " does not exist.");
        }

        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException(
                    "Product with id " + productId + " does not exist.");
        }

        DbWarehouse warehouse = warehouseRepository.findById(warehouseId);
        if (warehouse == null || warehouse.archivedAt != null) {
            throw new IllegalArgumentException(
                    "Warehouse with id " + warehouseId + " does not exist.");
        }

        validator.validate(storeId, productId, warehouseId);

        StoreProductWarehouse association =
                new StoreProductWarehouse(store, product, warehouse);

        repository.save(association);
    }
}