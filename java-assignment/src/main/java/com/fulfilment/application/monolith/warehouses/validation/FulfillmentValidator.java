package com.fulfilment.application.monolith.warehouses.validation;

import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.FulfillmentAssignment;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Centralizes fulfilment assignment rules independently from the HTTP resource. */
@ApplicationScoped
public class FulfillmentValidator {
  private final WarehouseStore warehouseStore;
  private final ProductRepository productRepository;

  @Inject
  public FulfillmentValidator(WarehouseStore warehouseStore, ProductRepository productRepository) {
    this.warehouseStore = warehouseStore;
    this.productRepository = productRepository;
  }

  public void validate(Long storeId, Long productId, String warehouseBusinessUnitCode) {
    if (storeId == null || productId == null
        || warehouseBusinessUnitCode == null || warehouseBusinessUnitCode.isBlank()) {
      throw new WebApplicationException(
          "storeId, productId and warehouseBusinessUnitCode are required", 400);
    }
    if (Store.findById(storeId) == null) {
      throw new WebApplicationException("Store not found", 404);
    }
    if (productRepository.findById(productId) == null) {
      throw new WebApplicationException("Product not found", 404);
    }
    if (warehouseStore.findByBusinessUnitCode(warehouseBusinessUnitCode) == null) {
      throw new WebApplicationException("Active warehouse not found", 404);
    }

    List<FulfillmentAssignment> assignments = FulfillmentAssignment.listAll();
    boolean duplicate = assignments.stream().anyMatch(a ->
        Objects.equals(a.storeId, storeId)
            && Objects.equals(a.productId, productId)
            && Objects.equals(a.warehouseBusinessUnitCode, warehouseBusinessUnitCode));
    if (duplicate) {
      throw new WebApplicationException("Assignment already exists", 409);
    }

    Set<String> storeWarehouses = new HashSet<>();
    Set<String> productWarehouses = new HashSet<>();
    Set<Long> warehouseProducts = new HashSet<>();
    for (FulfillmentAssignment assignment : assignments) {
      if (Objects.equals(assignment.storeId, storeId)) {
        storeWarehouses.add(assignment.warehouseBusinessUnitCode);
        if (Objects.equals(assignment.productId, productId)) {
          productWarehouses.add(assignment.warehouseBusinessUnitCode);
        }
      }
      if (Objects.equals(assignment.warehouseBusinessUnitCode, warehouseBusinessUnitCode)) {
        warehouseProducts.add(assignment.productId);
      }
    }

    if (!storeWarehouses.contains(warehouseBusinessUnitCode) && storeWarehouses.size() >= 3) {
      throw new WebApplicationException("A store can be fulfilled by at most 3 warehouses", 400);
    }
    if (!productWarehouses.contains(warehouseBusinessUnitCode) && productWarehouses.size() >= 2) {
      throw new WebApplicationException("A product can be fulfilled by at most 2 warehouses per store", 400);
    }
    if (!warehouseProducts.contains(productId) && warehouseProducts.size() >= 5) {
      throw new WebApplicationException("A warehouse can store at most 5 product types", 400);
    }
  }
}
