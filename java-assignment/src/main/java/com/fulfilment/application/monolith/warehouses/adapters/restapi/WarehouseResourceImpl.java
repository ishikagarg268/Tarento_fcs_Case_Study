package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

    @Inject
    private WarehouseRepository warehouseRepository;

    @Inject
    private CreateWarehouseUseCase createWarehouseUseCase;

    @Inject
    private ArchiveWarehouseUseCase archiveWarehouseUseCase;

    @Inject
    private ReplaceWarehouseUseCase replaceWarehouseUseCase;

    @Override
    public List<Warehouse> listAllWarehousesUnits() {
        return warehouseRepository.getAll()
                .stream()
                .map(this::toWarehouseResponse)
                .toList();
    }

    @Override
    @Transactional
    public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {

        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =
                toDomainWarehouse(data);

        createWarehouseUseCase.create(warehouse);

        return toWarehouseResponse(warehouse);
    }

    @Override
    public Warehouse getAWarehouseUnitByID(String id) {

        Long warehouseId = Long.valueOf(id);

        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =
                warehouseRepository.findWarehouseById(warehouseId);

        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Warehouse with id " + id + " does not exist.");
        }

        return toWarehouseResponse(warehouse);
    }

    @Override
    @Transactional
    public void archiveAWarehouseUnitByID(String id) {

        Long warehouseId = Long.valueOf(id);

        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =
                warehouseRepository.findWarehouseById(warehouseId);

        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Warehouse with id " + id + " does not exist.");
        }

        archiveWarehouseUseCase.archive(warehouse);
    }

    @Override
    @Transactional
    public Warehouse replaceTheCurrentActiveWarehouse(
            String businessUnitCode,
            @NotNull Warehouse data) {

        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse newWarehouse =
                toDomainWarehouse(data);

        newWarehouse.businessUnitCode = businessUnitCode;

        replaceWarehouseUseCase.replace(newWarehouse);

        return toWarehouseResponse(newWarehouse);
    }

    private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse
    toDomainWarehouse(Warehouse warehouse) {

        var domainWarehouse =
                new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();

        domainWarehouse.id =
                warehouse.getId() == null ? null : Long.valueOf(warehouse.getId());

        domainWarehouse.businessUnitCode = warehouse.getBusinessUnitCode();
        domainWarehouse.location = warehouse.getLocation();
        domainWarehouse.capacity = warehouse.getCapacity();
        domainWarehouse.stock = warehouse.getStock();

        return domainWarehouse;
    }

    private Warehouse toWarehouseResponse(
            com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {

        var response = new Warehouse();

        response.setId(
                warehouse.id == null ? null : warehouse.id.toString());

        response.setBusinessUnitCode(warehouse.businessUnitCode);
        response.setLocation(warehouse.location);
        response.setCapacity(warehouse.capacity);
        response.setStock(warehouse.stock);

        return response;
    }
}