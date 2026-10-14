package com.fulfilment.application.monolith.fulfilment.adapters.database;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import com.fulfilment.application.monolith.fulfilment.domain.ports.StoreProductWarehouseStore;

@ApplicationScoped
public class StoreProductWarehouseRepository
        implements PanacheRepository<StoreProductWarehouse>,
        StoreProductWarehouseStore {

    public long countWarehousesForStore(Long storeId) {
        return find(
                "select count(distinct warehouse.id) from StoreProductWarehouse " +
                        "where store.id = ?1",
                storeId)
                .project(Long.class)
                .firstResult();
    }

    public long countWarehousesForStoreAndProduct(Long storeId, Long productId) {
        return find(
                "select count(distinct warehouse.id) from StoreProductWarehouse " +
                        "where store.id = ?1 and product.id = ?2",
                storeId,
                productId)
                .project(Long.class)
                .firstResult();
    }

    public long countProductsForWarehouse(Long warehouseId) {
        return find(
                "select count(distinct product.id) from StoreProductWarehouse " +
                        "where warehouse.id = ?1",
                warehouseId)
                .project(Long.class)
                .firstResult();
    }

    public boolean exists(Long storeId, Long productId, Long warehouseId) {
        return count(
                "store.id = ?1 and product.id = ?2 and warehouse.id = ?3",
                storeId,
                productId,
                warehouseId) > 0;
    }
    @Override
    public void save(StoreProductWarehouse association) {
        persist(association);
    }
}