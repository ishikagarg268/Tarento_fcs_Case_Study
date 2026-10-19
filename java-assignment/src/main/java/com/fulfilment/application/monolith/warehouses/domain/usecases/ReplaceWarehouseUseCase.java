package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {
  private final WarehouseStore warehouseStore;
  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore) { this.warehouseStore = warehouseStore; }

  @Override
  public void replace(Warehouse newWarehouse) {
    if (newWarehouse == null || newWarehouse.businessUnitCode == null)
      throw new IllegalArgumentException("Replacement warehouse and business unit code are required");
    Warehouse previous = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
    if (previous == null) throw new IllegalArgumentException("Active warehouse does not exist");
    if (newWarehouse.stock == null || !newWarehouse.stock.equals(previous.stock))
      throw new IllegalArgumentException("Replacement stock must match existing stock");
    if (newWarehouse.capacity == null || newWarehouse.capacity < previous.stock)
      throw new IllegalArgumentException("Replacement capacity cannot accommodate existing stock");
    previous.archivedAt = LocalDateTime.now();
    warehouseStore.update(previous);
    newWarehouse.createdAt = LocalDateTime.now();
    warehouseStore.create(newWarehouse);
  }
}
