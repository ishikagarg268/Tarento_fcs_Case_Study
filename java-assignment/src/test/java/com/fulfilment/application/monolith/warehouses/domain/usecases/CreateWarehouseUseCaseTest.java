package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateWarehouseUseCaseTest {

    private FakeWarehouseStore warehouseStore;
    private FakeLocationResolver locationResolver;
    private CreateWarehouseUseCase useCase;

    @BeforeEach
    void setUp() {
        warehouseStore = new FakeWarehouseStore();
        locationResolver = new FakeLocationResolver();
        useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);
    }

    @Test
    void shouldCreateWarehouse() {
        Location location = createLocation("ZWOLLE-001", 3, 200);
        locationResolver.location = location;

        Warehouse warehouse = createWarehouse(
                "MWH.100",
                "ZWOLLE-001",
                50,
                10);

        useCase.create(warehouse);

        assertEquals(1, warehouseStore.warehouses.size());
        assertEquals("MWH.100", warehouseStore.warehouses.get(0).businessUnitCode);
    }

    @Test
    void shouldRejectDuplicateBusinessUnitCode() {
        Location location = createLocation("ZWOLLE-001", 3, 200);
        locationResolver.location = location;

        Warehouse existingWarehouse = createWarehouse(
                "MWH.001",
                "ZWOLLE-001",
                50,
                10);

        warehouseStore.warehouses.add(existingWarehouse);

        Warehouse newWarehouse = createWarehouse(
                "MWH.001",
                "ZWOLLE-001",
                40,
                10);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.create(newWarehouse));

        assertEquals(
                "Warehouse with business unit code MWH.001 already exists.",
                exception.getMessage());
    }

    @Test
    void shouldRejectInvalidLocation() {
        locationResolver.location = null;

        Warehouse warehouse = createWarehouse(
                "MWH.100",
                "UNKNOWN-001",
                50,
                10);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.create(warehouse));

        assertEquals(
                "Location UNKNOWN-001 does not exist.",
                exception.getMessage());
    }

    @Test
    void shouldRejectWhenMaximumWarehouseCountIsReached() {
        Location location = createLocation("ZWOLLE-001", 2, 300);
        locationResolver.location = location;

        warehouseStore.warehouses.add(
                createWarehouse("MWH.001", "ZWOLLE-001", 50, 10));

        warehouseStore.warehouses.add(
                createWarehouse("MWH.002", "ZWOLLE-001", 50, 10));

        Warehouse newWarehouse = createWarehouse(
                "MWH.003",
                "ZWOLLE-001",
                50,
                10);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.create(newWarehouse));

        assertEquals(
                "Maximum number of warehouses reached for location ZWOLLE-001",
                exception.getMessage());
    }

    @Test
    void shouldRejectWhenStockExceedsCapacity() {
        Location location = createLocation("ZWOLLE-001", 3, 300);
        locationResolver.location = location;

        Warehouse warehouse = createWarehouse(
                "MWH.100",
                "ZWOLLE-001",
                50,
                60);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.create(warehouse));

        assertEquals(
                "Warehouse stock cannot exceed warehouse capacity.",
                exception.getMessage());
    }

    @Test
    void shouldRejectWhenCumulativeLocationCapacityIsExceeded() {
        Location location = createLocation("ZWOLLE-001", 3, 200);
        locationResolver.location = location;

        warehouseStore.warehouses.add(
                createWarehouse("MWH.001", "ZWOLLE-001", 100, 10));

        warehouseStore.warehouses.add(
                createWarehouse("MWH.002", "ZWOLLE-001", 70, 10));

        Warehouse newWarehouse = createWarehouse(
                "MWH.003",
                "ZWOLLE-001",
                40,
                10);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.create(newWarehouse));

        assertEquals(
                "Total warehouse capacity exceeds location max capacity.",
                exception.getMessage());
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

    private Location createLocation(
            String identification,
            int maxNumberOfWarehouses,
            int maxCapacity) {

        Location location = new Location(
                identification,
                maxNumberOfWarehouses,
                maxCapacity);
        location.identification = identification;
        location.maxNumberOfWarehouses = maxNumberOfWarehouses;
        location.maxCapacity = maxCapacity;
        return location;
    }

    private static class FakeWarehouseStore implements WarehouseStore {

        private final List<Warehouse> warehouses = new ArrayList<>();

        @Override
        public List<Warehouse> getAll() {
            return warehouses.stream()
                    .filter(warehouse -> warehouse.archivedAt == null)
                    .toList();
        }

        @Override
        public void create(Warehouse warehouse) {
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
                    .filter(warehouse ->
                            buCode.equals(warehouse.businessUnitCode)
                                    && warehouse.archivedAt == null)
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

    private static class FakeLocationResolver implements LocationResolver {

        private Location location;

        @Override
        public Location resolveByIdentifier(String identifier) {
            if (location == null) {
                return null;
            }

            if (!location.identification.equals(identifier)) {
                return null;
            }

            return location;
        }
    }
}