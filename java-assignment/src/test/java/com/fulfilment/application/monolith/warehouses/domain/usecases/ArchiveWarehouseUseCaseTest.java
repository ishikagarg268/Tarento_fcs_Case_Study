package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ArchiveWarehouseUseCaseTest {

    @Test
    public void testArchiveWarehouseShouldSetArchivedAt() {
        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

        ArchiveWarehouseUseCase archiveWarehouseUseCase =
                new ArchiveWarehouseUseCase(warehouseStore);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MWH.100";
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 30;
        warehouse.stock = 10;

        assertNull(warehouse.archivedAt);

        archiveWarehouseUseCase.archive(warehouse);

        assertNotNull(warehouse.archivedAt);
    }

    @Test
    public void testArchiveWarehouseShouldUpdateWarehouse() {
        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

        ArchiveWarehouseUseCase archiveWarehouseUseCase =
                new ArchiveWarehouseUseCase(warehouseStore);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MWH.100";
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 30;
        warehouse.stock = 10;

        archiveWarehouseUseCase.archive(warehouse);

        assertNotNull(warehouseStore.updatedWarehouse);
        assertNotNull(warehouseStore.updatedWarehouse.archivedAt);
    }

    private static class FakeWarehouseStore implements WarehouseStore {

        private final List<Warehouse> warehouses = new ArrayList<>();
        private Warehouse updatedWarehouse;

        @Override
        public List<Warehouse> getAll() {
            return warehouses;
        }

        @Override
        public void create(Warehouse warehouse) {
            warehouses.add(warehouse);
        }

        @Override
        public void update(Warehouse warehouse) {
            updatedWarehouse = warehouse;
        }

        @Override
        public void remove(Warehouse warehouse) {
            warehouses.remove(warehouse);
        }

        @Override
        public Warehouse findByBusinessUnitCode(String buCode) {
            return warehouses.stream()
                    .filter(
                            warehouse ->
                                    buCode.equals(warehouse.businessUnitCode))
                    .findFirst()
                    .orElse(null);
        }
        @Override
        public Warehouse findWarehouseById(Long id) {
            return warehouses.stream()
                    .filter(warehouse ->
                            id.equals(warehouse.id)
                                    && warehouse.archivedAt == null)
                    .findFirst()
                    .orElse(null);
        }
    }
}