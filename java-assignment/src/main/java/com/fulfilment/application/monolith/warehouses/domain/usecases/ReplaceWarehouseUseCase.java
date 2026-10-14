package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

    private final WarehouseStore warehouseStore;
    private final LocationResolver locationResolver;

    public ReplaceWarehouseUseCase(
            WarehouseStore warehouseStore,
            LocationResolver locationResolver) {
        this.warehouseStore = warehouseStore;
        this.locationResolver = locationResolver;
    }

    @Override
    public void replace(Warehouse newWarehouse) {

        Warehouse oldWarehouse =
                warehouseStore.findByBusinessUnitCode(
                        newWarehouse.businessUnitCode);

        if (oldWarehouse == null) {
            throw new IllegalArgumentException(
                    "Warehouse with business unit code "
                            + newWarehouse.businessUnitCode
                            + " does not exist.");
        }

        if (newWarehouse.capacity < oldWarehouse.stock) {
            throw new IllegalArgumentException(
                    "New warehouse capacity cannot be less than existing stock.");
        }

        if (!newWarehouse.stock.equals(oldWarehouse.stock)) {
            throw new IllegalArgumentException(
                    "New warehouse stock must match existing stock.");
        }

        Location location =
                locationResolver.resolveByIdentifier(
                        newWarehouse.location);

        if (location == null) {
            throw new IllegalArgumentException(
                    "Location "
                            + newWarehouse.location
                            + " does not exist.");
        }

        long warehouseCount = warehouseStore.getAll().stream()
                .filter(existingWarehouse ->
                        !existingWarehouse.businessUnitCode.equals(
                                oldWarehouse.businessUnitCode))
                .filter(existingWarehouse ->
                        newWarehouse.location.equals(
                                existingWarehouse.location))
                .count();

        if (warehouseCount >= location.maxNumberOfWarehouses) {
            throw new IllegalArgumentException(
                    "Maximum number of warehouses reached for location "
                            + newWarehouse.location);
        }

        int totalCapacity = warehouseStore.getAll().stream()
                .filter(existingWarehouse ->
                        !existingWarehouse.businessUnitCode.equals(
                                oldWarehouse.businessUnitCode))
                .filter(existingWarehouse ->
                        newWarehouse.location.equals(
                                existingWarehouse.location))
                .mapToInt(existingWarehouse -> existingWarehouse.capacity)
                .sum();

        if (totalCapacity + newWarehouse.capacity > location.maxCapacity) {
            throw new IllegalArgumentException(
                    "Total warehouse capacity exceeds location max capacity.");
        }

        oldWarehouse.archivedAt =
                java.time.LocalDateTime.now();

        warehouseStore.update(oldWarehouse);

        warehouseStore.create(newWarehouse);
    }
}