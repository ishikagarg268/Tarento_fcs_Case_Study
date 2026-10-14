package com.fulfilment.application.monolith.fulfilment.domain.validators;

import com.fulfilment.application.monolith.fulfilment.domain.ports.StoreProductWarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StoreProductWarehouseValidator {

    private final StoreProductWarehouseStore repository;

    public StoreProductWarehouseValidator(StoreProductWarehouseStore repository) {
        this.repository = repository;
    }

    public void validate(Long storeId, Long productId, Long warehouseId) {

        if (repository.exists(storeId, productId, warehouseId)) {
            throw new IllegalArgumentException(
                    "This Store, Product and Warehouse association already exists.");
        }

        long warehousesForProductInStore =
                repository.countWarehousesForStoreAndProduct(storeId, productId);

        if (warehousesForProductInStore >= 2) {
            throw new IllegalArgumentException(
                    "A Product can be fulfilled by a maximum of 2 Warehouses per Store.");
        }

        long warehousesForStore =
                repository.countWarehousesForStore(storeId);

        if (warehousesForStore >= 3) {
            throw new IllegalArgumentException(
                    "A Store can be fulfilled by a maximum of 3 Warehouses.");
        }

        long productsInWarehouse =
                repository.countProductsForWarehouse(warehouseId);

        if (productsInWarehouse >= 5) {
            throw new IllegalArgumentException(
                    "A Warehouse can store a maximum of 5 Products.");
        }
    }
}