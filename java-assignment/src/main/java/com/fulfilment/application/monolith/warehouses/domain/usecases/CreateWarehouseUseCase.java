package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {
  private final WarehouseStore warehouseStore;
  public CreateWarehouseUseCase(WarehouseStore warehouseStore) { this.warehouseStore = warehouseStore; }

  @Override
  public void create(Warehouse warehouse) {
    if (warehouse == null) throw new IllegalArgumentException("Warehouse is required");
    if (warehouse.businessUnitCode == null || warehouse.businessUnitCode.isBlank())
      throw new IllegalArgumentException("Business unit code is required");
    if (warehouseRepositoryContains(warehouse.businessUnitCode))
      throw new IllegalArgumentException("Business unit code already exists: " + warehouse.businessUnitCode);
    warehouse.createdAt = warehouse.createdAt == null ? LocalDateTime.now() : warehouse.createdAt;
    warehouseStore.create(warehouse);
  }

  private boolean warehouseRepositoryContains(String code) {
    return warehouseStore.findByBusinessUnitCode(code) != null;
  }
}
