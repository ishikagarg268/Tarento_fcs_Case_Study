package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

    private final WarehouseStore warehouseStore;
    private final LocationResolver locationResolver;

    public CreateWarehouseUseCase(
            WarehouseStore warehouseStore,
            LocationResolver locationResolver) {
        this.warehouseStore = warehouseStore;
        this.locationResolver = locationResolver;
    }

    @Override
    public void create(Warehouse warehouse) {

        if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
            throw new IllegalArgumentException(
                    "Warehouse with business unit code "
                            + warehouse.businessUnitCode
                            + " already exists.");
        }

        Location location = locationResolver.resolveByIdentifier(warehouse.location);

        if (location == null) {
            throw new IllegalArgumentException(
                    "Location " + warehouse.location + " does not exist.");
        }

        long warehouseCount = warehouseStore.getAll().stream()
                .filter(existingWarehouse ->
                        warehouse.location.equals(existingWarehouse.location))
                .count();

        if (warehouseCount >= location.maxNumberOfWarehouses) {
            throw new IllegalArgumentException(
                    "Maximum number of warehouses reached for location "
                            + warehouse.location);
        }

        int totalCapacity = warehouseStore.getAll().stream()
                .filter(existingWarehouse ->
                        warehouse.location.equals(existingWarehouse.location))
                .mapToInt(existingWarehouse -> existingWarehouse.capacity)
                .sum();

        if (totalCapacity + warehouse.capacity > location.maxCapacity) {
            throw new IllegalArgumentException(
                    "Total warehouse capacity exceeds location max capacity.");
        }

        if (warehouse.stock > warehouse.capacity) {
            throw new IllegalArgumentException(
                    "Warehouse stock cannot exceed warehouse capacity.");
        }

        warehouseStore.create(warehouse);
    }
}