package com.fulfilment.application.monolith.fulfilment.domain.ports;

import com.fulfilment.application.monolith.fulfilment.adapters.database.StoreProductWarehouse;

public interface StoreProductWarehouseStore {

    long countWarehousesForStore(Long storeId);

    long countWarehousesForStoreAndProduct(Long storeId, Long productId);

    long countProductsForWarehouse(Long warehouseId);

    boolean exists(Long storeId, Long productId, Long warehouseId);

    void save(StoreProductWarehouse association);
}