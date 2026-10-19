package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {
  @Override
  public List<Warehouse> getAll() {
    return listAll().stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  @Transactional
  public void create(Warehouse warehouse) {
    DbWarehouse entity = fromWarehouse(warehouse);
    entity.id = null;
    persist(entity);
    warehouse.createdAt = entity.createdAt;
  }

  @Override
  @Transactional
  public void update(Warehouse warehouse) {
    DbWarehouse entity = find("businessUnitCode = ?1 and archivedAt is null", warehouse.businessUnitCode)
        .firstResult();
    // Updating a missing warehouse is a no-op; callers that need existence checks
    // should perform them at the resource/use-case boundary.
    if (entity == null) return;
    entity.location = warehouse.location;
    entity.capacity = warehouse.capacity;
    entity.stock = warehouse.stock;
    entity.createdAt = warehouse.createdAt;
    entity.archivedAt = warehouse.archivedAt;
  }

  @Override
  @Transactional
  public void remove(Warehouse warehouse) {
    DbWarehouse entity = find("businessUnitCode = ?1 and archivedAt is null", warehouse.businessUnitCode)
        .firstResult();
    if (entity != null) delete(entity);
  }

  /** Finds a warehouse by its database-generated identifier. Returns null when absent. */
  public Warehouse findWarehouseById(long id) {
    DbWarehouse entity = findById(id);
    return entity == null ? null : entity.toWarehouse();
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    DbWarehouse entity = find("businessUnitCode = ?1 and archivedAt is null", buCode)
        .firstResult();
    return entity == null ? null : entity.toWarehouse();
  }

  public DbWarehouse findEntityByBusinessUnitCode(String buCode) {
    return find("businessUnitCode = ?1 and archivedAt is null", buCode).firstResult();
  }

  private DbWarehouse fromWarehouse(Warehouse warehouse) {
    DbWarehouse entity = new DbWarehouse();
    entity.businessUnitCode = warehouse.businessUnitCode;
    entity.location = warehouse.location;
    entity.capacity = warehouse.capacity;
    entity.stock = warehouse.stock;
    entity.createdAt = warehouse.createdAt == null ? java.time.LocalDateTime.now() : warehouse.createdAt;
    entity.archivedAt = warehouse.archivedAt;
    return entity;
  }
}
