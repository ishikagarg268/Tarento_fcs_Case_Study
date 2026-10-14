package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.warehouse.api.WarehouseResource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {
  @Inject WarehouseRepository warehouseRepository;
  @Inject LocationGateway locationGateway;

  @Override
  public List<com.warehouse.api.beans.Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
  }

  @Override
  @Transactional
  public com.warehouse.api.beans.Warehouse createANewWarehouseUnit(@NotNull com.warehouse.api.beans.Warehouse data) {
    Warehouse warehouse = fromRequest(data);
    validateNewWarehouse(warehouse, null);
    warehouse.createdAt = LocalDateTime.now();
    warehouseRepository.create(warehouse);
    return toWarehouseResponse(warehouse);
  }

  @Override
  public com.warehouse.api.beans.Warehouse getAWarehouseUnitByID(String id) {
    Warehouse warehouse = warehouseRepository.findByBusinessUnitCode(id);
    if (warehouse == null) throw new WebApplicationException("Active warehouse with business unit code " + id + " does not exist", 404);
    return toWarehouseResponse(warehouse);
  }

  @Override
  @Transactional
  public void archiveAWarehouseUnitByID(String id) {
    Warehouse warehouse = warehouseRepository.findByBusinessUnitCode(id);
    if (warehouse == null) throw new WebApplicationException("Active warehouse with business unit code " + id + " does not exist", 404);
    warehouse.archivedAt = LocalDateTime.now();
    warehouseRepository.update(warehouse);
  }

  @Override
  @Transactional
  public com.warehouse.api.beans.Warehouse replaceTheCurrentActiveWarehouse(
      String businessUnitCode, @NotNull com.warehouse.api.beans.Warehouse data) {
    Warehouse previous = warehouseRepository.findByBusinessUnitCode(businessUnitCode);
    if (previous == null) throw new WebApplicationException("Active warehouse with business unit code " + businessUnitCode + " does not exist", 404);

    Warehouse replacement = fromRequest(data);
    if (replacement.businessUnitCode != null && !businessUnitCode.equals(replacement.businessUnitCode)) {
      throw new WebApplicationException("Replacement must reuse business unit code " + businessUnitCode, 400);
    }
    replacement.businessUnitCode = businessUnitCode;
    if (replacement.stock == null || !replacement.stock.equals(previous.stock)) {
      throw new WebApplicationException("Replacement stock must match the current warehouse stock", 400);
    }
    validateNewWarehouse(replacement, previous);
    previous.archivedAt = LocalDateTime.now();
    warehouseRepository.update(previous);
    replacement.createdAt = LocalDateTime.now();
    warehouseRepository.create(replacement);
    return toWarehouseResponse(replacement);
  }

  private void validateNewWarehouse(Warehouse warehouse, Warehouse beingReplaced) {
    if (warehouse.businessUnitCode == null || warehouse.businessUnitCode.isBlank())
      throw new WebApplicationException("businessUnitCode is required", 400);
    if (warehouse.location == null || warehouse.location.isBlank())
      throw new WebApplicationException("location is required", 400);
    if (warehouse.capacity == null || warehouse.capacity < 0)
      throw new WebApplicationException("capacity must be zero or greater", 400);
    if (warehouse.stock == null || warehouse.stock < 0)
      throw new WebApplicationException("stock must be zero or greater", 400);
    if (warehouse.stock > warehouse.capacity)
      throw new WebApplicationException("stock cannot exceed warehouse capacity", 400);

    Location location = locationGateway.resolveByIdentifier(warehouse.location);
    if (location == null) throw new WebApplicationException("Unknown location: " + warehouse.location, 400);

    Warehouse existing = warehouseRepository.findByBusinessUnitCode(warehouse.businessUnitCode);
    if (existing != null && (beingReplaced == null || !existing.businessUnitCode.equals(beingReplaced.businessUnitCode)))
      throw new WebApplicationException("Business unit code already exists: " + warehouse.businessUnitCode, 409);

    List<Warehouse> atLocation = warehouseRepository.getAll().stream()
        .filter(w -> w.archivedAt == null && warehouse.location.equals(w.location))
        .filter(w -> beingReplaced == null || !w.businessUnitCode.equals(beingReplaced.businessUnitCode))
        .toList();
    if (beingReplaced == null && atLocation.size() >= location.maxNumberOfWarehouses)
      throw new WebApplicationException("Maximum number of warehouses reached for location " + warehouse.location, 400);
    int totalCapacity = atLocation.stream().mapToInt(w -> w.capacity == null ? 0 : w.capacity).sum();
    if (warehouse.capacity > location.maxCapacity || totalCapacity + warehouse.capacity > location.maxCapacity)
      throw new WebApplicationException("Warehouse capacity exceeds available capacity at location " + warehouse.location, 400);
    if (beingReplaced != null && warehouse.capacity < beingReplaced.stock)
      throw new WebApplicationException("Replacement capacity cannot accommodate existing stock", 400);
  }

  private Warehouse fromRequest(com.warehouse.api.beans.Warehouse data) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = data.getBusinessUnitCode();
    warehouse.location = data.getLocation();
    warehouse.capacity = data.getCapacity();
    warehouse.stock = data.getStock();
    return warehouse;
  }

  private com.warehouse.api.beans.Warehouse toWarehouseResponse(Warehouse warehouse) {
    var response = new com.warehouse.api.beans.Warehouse();
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);
    return response;
  }
}
