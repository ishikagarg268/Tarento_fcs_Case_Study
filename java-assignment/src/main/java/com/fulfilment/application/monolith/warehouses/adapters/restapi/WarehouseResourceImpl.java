package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.validation.WarehouseValidator;
import com.warehouse.api.WarehouseResource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;
import java.time.LocalDateTime;
import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {
  @Inject WarehouseRepository warehouseRepository;
  @Inject WarehouseValidator warehouseValidator;

  @Override
  public List<com.warehouse.api.beans.Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll().stream()
        .filter(warehouse -> warehouse.archivedAt == null)
        .map(this::toWarehouseResponse).toList();
  }

  @Override
  @Transactional
  public com.warehouse.api.beans.Warehouse createANewWarehouseUnit(@NotNull com.warehouse.api.beans.Warehouse data) {
    if (data == null) throw new WebApplicationException("Warehouse request is required", 400);
    Warehouse warehouse = fromRequest(data);
    warehouseValidator.validate(warehouse, null);
    warehouse.createdAt = LocalDateTime.now();
    warehouseRepository.create(warehouse);
    return toWarehouseResponse(warehouse);
  }

  @Override
  public com.warehouse.api.beans.Warehouse getAWarehouseUnitByID(String id) {
    Warehouse warehouse = findWarehouseByPathId(id);
    if (warehouse == null) {
      // Preserve the assignment's existing endpoint contract for unknown IDs.
      throw new WebApplicationException("Active warehouse with id " + id + " does not exist", 500);
    }
    return toWarehouseResponse(warehouse);
  }

  @Override
  @Transactional
  public void archiveAWarehouseUnitByID(String id) {
    Warehouse warehouse = findWarehouseByPathId(id);
    if (warehouse == null) {
      // Preserve the assignment's existing endpoint contract for unknown IDs.
      throw new WebApplicationException("Active warehouse with id " + id + " does not exist", 500);
    }
    warehouse.archivedAt = LocalDateTime.now();
    warehouseRepository.update(warehouse);
  }

  @Override
  @Transactional
  public com.warehouse.api.beans.Warehouse replaceTheCurrentActiveWarehouse(
      String businessUnitCode, @NotNull com.warehouse.api.beans.Warehouse data) {
    if (data == null) throw new WebApplicationException("Warehouse request is required", 400);
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
    warehouseValidator.validate(replacement, previous);
    previous.archivedAt = LocalDateTime.now();
    warehouseRepository.update(previous);
    replacement.createdAt = LocalDateTime.now();
    warehouseRepository.create(replacement);
    return toWarehouseResponse(replacement);
  }

  private Warehouse findWarehouseByPathId(String id) {
    if (id == null) return null;
    try {
      return warehouseRepository.findWarehouseById(Long.parseLong(id));
    } catch (NumberFormatException ignored) {
      // Keep compatibility with clients that use the business unit code in this path.
      return warehouseRepository.findByBusinessUnitCode(id);
    }
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
    // The OpenAPI schema defines id as a string, so serialize the database ID.
    if (warehouse.id != null) {
      response.setId(String.valueOf(warehouse.id));
    }
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);
    return response;
  }
}
