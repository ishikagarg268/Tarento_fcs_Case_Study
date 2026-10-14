package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
public class WarehouseRepositoryTest {

    @Inject
    WarehouseRepository warehouseRepository;

    @Test
    public void testGetAllReturnsActiveWarehouses() {
        List<Warehouse> warehouses = warehouseRepository.getAll();

        assertEquals(3, warehouses.size());
    }

    @Test
    @TestTransaction
    public void testCreateAndFindByBusinessUnitCode() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "TEST.001";
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 50;
        warehouse.stock = 10;
        warehouse.createdAt = LocalDateTime.now();

        warehouseRepository.create(warehouse);

        Warehouse savedWarehouse =
                warehouseRepository.findByBusinessUnitCode("TEST.001");

        assertNotNull(savedWarehouse);
        assertEquals("TEST.001", savedWarehouse.businessUnitCode);
        assertEquals("ZWOLLE-001", savedWarehouse.location);
        assertEquals(50, savedWarehouse.capacity);
        assertEquals(10, savedWarehouse.stock);
    }

    @Test
    public void testFindByBusinessUnitCodeReturnsNullForUnknownWarehouse() {
        Warehouse warehouse =
                warehouseRepository.findByBusinessUnitCode("UNKNOWN.001");

        assertNull(warehouse);
    }

    @Test
    public void testFindWarehouseById() {
        Warehouse warehouse =
                warehouseRepository.findWarehouseById(1L);

        assertNotNull(warehouse);
        assertEquals(1L, warehouse.id);
        assertEquals("MWH.001", warehouse.businessUnitCode);
    }

    @Test
    public void testFindWarehouseByIdReturnsNullForUnknownId() {
        Warehouse warehouse =
                warehouseRepository.findWarehouseById(9999L);

        assertNull(warehouse);
    }

    @Test
    @TestTransaction
    public void testUpdateWarehouse() {
        Warehouse warehouse =
                warehouseRepository.findByBusinessUnitCode("MWH.001");

        warehouse.location = "AMSTERDAM-001";
        warehouse.capacity = 120;
        warehouse.stock = 20;

        warehouseRepository.update(warehouse);

        Warehouse updatedWarehouse =
                warehouseRepository.findByBusinessUnitCode("MWH.001");

        assertNotNull(updatedWarehouse);
        assertEquals("AMSTERDAM-001", updatedWarehouse.location);
        assertEquals(120, updatedWarehouse.capacity);
        assertEquals(20, updatedWarehouse.stock);
    }

    @Test
    public void testUpdateUnknownWarehouseDoesNothing() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "UNKNOWN.001";
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 50;
        warehouse.stock = 10;

        warehouseRepository.update(warehouse);

        Warehouse result =
                warehouseRepository.findByBusinessUnitCode("UNKNOWN.001");

        assertNull(result);
    }

    @Test
    @TestTransaction
    public void testRemoveWarehouse() {
        Warehouse warehouse =
                warehouseRepository.findByBusinessUnitCode("MWH.001");

        warehouseRepository.remove(warehouse);

        Warehouse removedWarehouse =
                warehouseRepository.findByBusinessUnitCode("MWH.001");

        assertNull(removedWarehouse);
    }

    @Test
    public void testRemoveUnknownWarehouseDoesNothing() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "UNKNOWN.001";

        warehouseRepository.remove(warehouse);

        assertNull(
                warehouseRepository.findByBusinessUnitCode("UNKNOWN.001"));
    }
}