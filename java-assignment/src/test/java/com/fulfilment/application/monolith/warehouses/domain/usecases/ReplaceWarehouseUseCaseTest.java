package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ReplaceWarehouseUseCaseTest {

    @Test
    public void testReplaceWarehouseSuccessfully() {
        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        FakeLocationResolver locationResolver = new FakeLocationResolver();

        locationResolver.addLocation(
                new Location("ZWOLLE-002", 3, 200));

        Warehouse oldWarehouse =
                createWarehouse("MWH.001", "ZWOLLE-001", 100, 10);

        Warehouse newWarehouse =
                createWarehouse("MWH.001", "ZWOLLE-002", 50, 10);

        warehouseStore.warehouses.add(oldWarehouse);

        ReplaceWarehouseUseCase replaceWarehouseUseCase =
                new ReplaceWarehouseUseCase(
                        warehouseStore,
                        locationResolver);

        replaceWarehouseUseCase.replace(newWarehouse);

        assertNotNull(oldWarehouse.archivedAt);
        assertNull(newWarehouse.archivedAt);
        assertEquals("MWH.001", newWarehouse.businessUnitCode);
        assertEquals(10, newWarehouse.stock);
        assertEquals(newWarehouse, warehouseStore.createdWarehouse);
    }

    @Test
    public void testReplaceWarehouseWhenOldWarehouseDoesNotExist() {
        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        FakeLocationResolver locationResolver = new FakeLocationResolver();

        locationResolver.addLocation(
                new Location("ZWOLLE-002", 3, 200));

        Warehouse newWarehouse =
                createWarehouse("MWH.001", "ZWOLLE-002", 50, 10);

        ReplaceWarehouseUseCase replaceWarehouseUseCase =
                new ReplaceWarehouseUseCase(
                        warehouseStore,
                        locationResolver);

        assertThrows(
                IllegalArgumentException.class,
                () -> replaceWarehouseUseCase.replace(newWarehouse));

        assertNull(newWarehouse.archivedAt);
        assertNull(warehouseStore.createdWarehouse);
    }

    @Test
    public void testReplaceWarehouseWhenLocationDoesNotExist() {
        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        FakeLocationResolver locationResolver = new FakeLocationResolver();

        Warehouse oldWarehouse =
                createWarehouse("MWH.001", "ZWOLLE-001", 100, 20);

        Warehouse newWarehouse =
                createWarehouse("MWH.001", "UNKNOWN-001", 50, 20);

        warehouseStore.warehouses.add(oldWarehouse);

        ReplaceWarehouseUseCase replaceWarehouseUseCase =
                new ReplaceWarehouseUseCase(
                        warehouseStore,
                        locationResolver);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> replaceWarehouseUseCase.replace(newWarehouse));

        assertEquals(
                "Location UNKNOWN-001 does not exist.",
                exception.getMessage());

        assertNull(oldWarehouse.archivedAt);
        assertNull(warehouseStore.createdWarehouse);
    }

    @Test
    public void testReplaceWarehouseWhenCapacityCannotHandleOldStock() {
        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        FakeLocationResolver locationResolver = new FakeLocationResolver();

        locationResolver.addLocation(
                new Location("ZWOLLE-002", 3, 200));

        Warehouse oldWarehouse =
                createWarehouse("MWH.001", "ZWOLLE-001", 100, 20);

        Warehouse newWarehouse =
                createWarehouse("MWH.001", "ZWOLLE-002", 10, 20);

        warehouseStore.warehouses.add(oldWarehouse);

        ReplaceWarehouseUseCase replaceWarehouseUseCase =
                new ReplaceWarehouseUseCase(
                        warehouseStore,
                        locationResolver);

        assertThrows(
                IllegalArgumentException.class,
                () -> replaceWarehouseUseCase.replace(newWarehouse));

        assertNull(oldWarehouse.archivedAt);
        assertNull(warehouseStore.createdWarehouse);
    }

    @Test
    public void testReplaceWarehouseWhenStockDoesNotMatchOldStock() {
        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        FakeLocationResolver locationResolver = new FakeLocationResolver();

        locationResolver.addLocation(
                new Location("ZWOLLE-002", 3, 200));

        Warehouse oldWarehouse =
                createWarehouse("MWH.001", "ZWOLLE-001", 100, 20);

        Warehouse newWarehouse =
                createWarehouse("MWH.001", "ZWOLLE-002", 50, 10);

        warehouseStore.warehouses.add(oldWarehouse);

        ReplaceWarehouseUseCase replaceWarehouseUseCase =
                new ReplaceWarehouseUseCase(
                        warehouseStore,
                        locationResolver);

        assertThrows(
                IllegalArgumentException.class,
                () -> replaceWarehouseUseCase.replace(newWarehouse));

        assertNull(oldWarehouse.archivedAt);
        assertNull(warehouseStore.createdWarehouse);
    }

    @Test
    public void testReplaceWarehouseWhenMaximumWarehouseCountIsReached() {
        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        FakeLocationResolver locationResolver = new FakeLocationResolver();

        locationResolver.addLocation(
                new Location("ZWOLLE-002", 2, 300));

        Warehouse oldWarehouse =
                createWarehouse("MWH.001", "ZWOLLE-001", 100, 20);

        Warehouse existingWarehouse1 =
                createWarehouse("MWH.002", "ZWOLLE-002", 50, 10);

        Warehouse existingWarehouse2 =
                createWarehouse("MWH.003", "ZWOLLE-002", 50, 10);

        Warehouse newWarehouse =
                createWarehouse("MWH.001", "ZWOLLE-002", 50, 20);

        warehouseStore.warehouses.add(oldWarehouse);
        warehouseStore.warehouses.add(existingWarehouse1);
        warehouseStore.warehouses.add(existingWarehouse2);

        ReplaceWarehouseUseCase replaceWarehouseUseCase =
                new ReplaceWarehouseUseCase(
                        warehouseStore,
                        locationResolver);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> replaceWarehouseUseCase.replace(newWarehouse));

        assertEquals(
                "Maximum number of warehouses reached for location ZWOLLE-002",
                exception.getMessage());

        assertNull(oldWarehouse.archivedAt);
        assertNull(warehouseStore.createdWarehouse);
    }

    @Test
    public void testReplaceWarehouseWhenTotalCapacityIsExceeded() {
        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        FakeLocationResolver locationResolver = new FakeLocationResolver();

        locationResolver.addLocation(
                new Location("ZWOLLE-002", 5, 100));

        Warehouse oldWarehouse =
                createWarehouse("MWH.001", "ZWOLLE-001", 100, 20);

        Warehouse existingWarehouse =
                createWarehouse("MWH.002", "ZWOLLE-002", 80, 10);

        Warehouse newWarehouse =
                createWarehouse("MWH.001", "ZWOLLE-002", 30, 20);

        warehouseStore.warehouses.add(oldWarehouse);
        warehouseStore.warehouses.add(existingWarehouse);

        ReplaceWarehouseUseCase replaceWarehouseUseCase =
                new ReplaceWarehouseUseCase(
                        warehouseStore,
                        locationResolver);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> replaceWarehouseUseCase.replace(newWarehouse));

        assertEquals(
                "Total warehouse capacity exceeds location max capacity.",
                exception.getMessage());

        assertNull(oldWarehouse.archivedAt);
        assertNull(warehouseStore.createdWarehouse);
    }

    private Warehouse createWarehouse(
            String businessUnitCode,
            String location,
            int capacity,
            int stock) {

        Warehouse warehouse = new Warehouse();

        warehouse.businessUnitCode = businessUnitCode;
        warehouse.location = location;
        warehouse.capacity = capacity;
        warehouse.stock = stock;

        return warehouse;
    }

    private static class FakeWarehouseStore implements WarehouseStore {

        private final List<Warehouse> warehouses = new ArrayList<>();

        private Warehouse createdWarehouse;

        @Override
        public List<Warehouse> getAll() {
            return warehouses.stream()
                    .filter(warehouse -> warehouse.archivedAt == null)
                    .toList();
        }

        @Override
        public void create(Warehouse warehouse) {
            createdWarehouse = warehouse;
            warehouses.add(warehouse);
        }

        @Override
        public void update(Warehouse warehouse) {
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
                                    buCode.equals(warehouse.businessUnitCode)
                                            && warehouse.archivedAt == null)
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public Warehouse findWarehouseById(Long id) {
            return warehouses.stream()
                    .filter(
                            warehouse ->
                                    id.equals(warehouse.id)
                                            && warehouse.archivedAt == null)
                    .findFirst()
                    .orElse(null);
        }
    }

    private static class FakeLocationResolver
            implements LocationResolver {

        private final List<Location> locations = new ArrayList<>();

        void addLocation(Location location) {
            locations.add(location);
        }

        @Override
        public Location resolveByIdentifier(String identifier) {
            return locations.stream()
                    .filter(
                            location ->
                                    location.identification.equals(identifier))
                    .findFirst()
                    .orElse(null);
        }
    }
}