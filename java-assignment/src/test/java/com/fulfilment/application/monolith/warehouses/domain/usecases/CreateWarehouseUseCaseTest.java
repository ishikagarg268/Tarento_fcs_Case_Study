package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class CreateWarehouseUseCaseTest {
  @Test void createsWarehouseAndSetsCreationTime() {
    FakeStore store = new FakeStore();
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "TEST.001";
    warehouse.location = "ZWOLLE-001";
    warehouse.capacity = 20;
    warehouse.stock = 10;
    new CreateWarehouseUseCase(store).create(warehouse);
    assertEquals(1, store.items.size());
    assertNotNull(warehouse.createdAt);
  }

  @Test void rejectsMissingWarehouseCode() {
    Warehouse warehouse = new Warehouse();
    assertThrows(IllegalArgumentException.class, () -> new CreateWarehouseUseCase(new FakeStore()).create(warehouse));
  }

  @Test void rejectsDuplicateActiveCode() {
    FakeStore store = new FakeStore();
    Warehouse existing = new Warehouse(); existing.businessUnitCode = "TEST.001";
    store.items.add(existing);
    Warehouse duplicate = new Warehouse(); duplicate.businessUnitCode = "TEST.001";
    assertThrows(IllegalArgumentException.class, () -> new CreateWarehouseUseCase(store).create(duplicate));
  }

  static class FakeStore implements WarehouseStore {
    final List<Warehouse> items = new ArrayList<>();
    public List<Warehouse> getAll() { return items; }
    public void create(Warehouse w) { items.add(w); }
    public void update(Warehouse w) {}
    public void remove(Warehouse w) { items.remove(w); }
    public Warehouse findByBusinessUnitCode(String code) {
      return items.stream().filter(w -> code.equals(w.businessUnitCode) && w.archivedAt == null).findFirst().orElse(null);
    }
  }
}
