package com.fulfilment.application.monolith.warehouses.validation;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;
import java.util.Objects;

/** Centralizes warehouse invariants so REST endpoints only handle request/response concerns. */
@ApplicationScoped
public class WarehouseValidator {
  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  @Inject
  public WarehouseValidator(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  /** Validates a new warehouse or a replacement of an existing active warehouse. */
  public void validate(Warehouse candidate, Warehouse beingReplaced) {
    if (candidate == null) {
      throw new WebApplicationException("Warehouse request is required", 400);
    }
    if (candidate.businessUnitCode == null || candidate.businessUnitCode.isBlank()) {
      throw new WebApplicationException("businessUnitCode is required", 400);
    }
    if (candidate.location == null || candidate.location.isBlank()) {
      throw new WebApplicationException("location is required", 400);
    }
    if (candidate.capacity == null || candidate.capacity < 0) {
      throw new WebApplicationException("capacity must be zero or greater", 400);
    }
    if (candidate.stock == null || candidate.stock < 0) {
      throw new WebApplicationException("stock must be zero or greater", 400);
    }
    if (candidate.stock > candidate.capacity) {
      throw new WebApplicationException("stock cannot exceed warehouse capacity", 400);
    }
    if (beingReplaced != null && !Objects.equals(candidate.stock, beingReplaced.stock)) {
      throw new WebApplicationException("Replacement stock must match the current warehouse stock", 400);
    }
    if (beingReplaced != null && candidate.capacity < beingReplaced.stock) {
      throw new WebApplicationException("Replacement capacity cannot accommodate existing stock", 400);
    }

    Location location = locationResolver.resolveByIdentifier(candidate.location);
    if (location == null) {
      throw new WebApplicationException("Unknown location: " + candidate.location, 400);
    }

    Warehouse existing = warehouseStore.findByBusinessUnitCode(candidate.businessUnitCode);
    if (existing != null && (beingReplaced == null
        || !Objects.equals(existing.businessUnitCode, beingReplaced.businessUnitCode))) {
      throw new WebApplicationException("Business unit code already exists: " + candidate.businessUnitCode, 409);
    }

    List<Warehouse> activeAtLocation = warehouseStore.getAll().stream()
        .filter(w -> w.archivedAt == null)
        .filter(w -> Objects.equals(candidate.location, w.location))
        // The replaced warehouse is removed from the capacity/count calculation only when it is
        // currently at the candidate location. If it is moved, the destination still needs room.
        .filter(w -> beingReplaced == null
            || !Objects.equals(w.businessUnitCode, beingReplaced.businessUnitCode))
        .toList();

    if (activeAtLocation.size() >= location.maxNumberOfWarehouses) {
      throw new WebApplicationException(
          "Maximum number of warehouses reached for location " + candidate.location, 500);
    }

    int totalCapacity = activeAtLocation.stream()
        .mapToInt(w -> w.capacity == null ? 0 : w.capacity)
        .sum();
    if (candidate.capacity > location.maxCapacity
        || (long) totalCapacity + candidate.capacity > location.maxCapacity) {
      throw new WebApplicationException(
          "Warehouse capacity exceeds available capacity at location " + candidate.location, 400);
    }
  }
}
