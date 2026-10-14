package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.FulfillmentAssignment;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/fulfillments")
@ApplicationScoped
@Consumes("application/json")
@Produces("application/json")
public class FulfillmentResource {
  @Inject WarehouseRepository warehouseRepository;
  @Inject ProductRepository productRepository;

  public static class AssignmentRequest {
    public Long storeId;
    public Long productId;
    public String warehouseBusinessUnitCode;
  }

  @POST
  @Transactional
  public AssignmentRequest assign(AssignmentRequest request) {
    if (request == null || request.storeId == null || request.productId == null
        || request.warehouseBusinessUnitCode == null || request.warehouseBusinessUnitCode.isBlank())
      throw new WebApplicationException("storeId, productId and warehouseBusinessUnitCode are required", 400);
    if (Store.findById(request.storeId) == null) throw new WebApplicationException("Store not found", 404);
    if (productRepository.findById(request.productId) == null) throw new WebApplicationException("Product not found", 404);
    if (warehouseRepository.findByBusinessUnitCode(request.warehouseBusinessUnitCode) == null)
      throw new WebApplicationException("Active warehouse not found", 404);

    List<FulfillmentAssignment> all = FulfillmentAssignment.listAll();
    boolean duplicate = all.stream().anyMatch(a -> a.storeId.equals(request.storeId)
        && a.productId.equals(request.productId)
        && a.warehouseBusinessUnitCode.equals(request.warehouseBusinessUnitCode));
    if (duplicate) throw new WebApplicationException("Assignment already exists", 409);

    Set<String> storeWarehouses = new HashSet<>();
    Set<String> productWarehouses = new HashSet<>();
    Set<Long> warehouseProducts = new HashSet<>();
    for (FulfillmentAssignment a : all) {
      if (a.storeId.equals(request.storeId)) storeWarehouses.add(a.warehouseBusinessUnitCode);
      if (a.storeId.equals(request.storeId) && a.productId.equals(request.productId))
        productWarehouses.add(a.warehouseBusinessUnitCode);
      if (a.warehouseBusinessUnitCode.equals(request.warehouseBusinessUnitCode))
        warehouseProducts.add(a.productId);
    }
    if (!storeWarehouses.contains(request.warehouseBusinessUnitCode) && storeWarehouses.size() >= 3)
      throw new WebApplicationException("A store can be fulfilled by at most 3 warehouses", 400);
    if (!productWarehouses.contains(request.warehouseBusinessUnitCode) && productWarehouses.size() >= 2)
      throw new WebApplicationException("A product can be fulfilled by at most 2 warehouses per store", 400);
    if (!warehouseProducts.contains(request.productId) && warehouseProducts.size() >= 5)
      throw new WebApplicationException("A warehouse can store at most 5 product types", 400);

    FulfillmentAssignment assignment = new FulfillmentAssignment();
    assignment.storeId = request.storeId;
    assignment.productId = request.productId;
    assignment.warehouseBusinessUnitCode = request.warehouseBusinessUnitCode;
    assignment.persist();
    return request;
  }

  @DELETE
  @Transactional
  @Path("/{id}")
  public void remove(Long id) {
    FulfillmentAssignment assignment = FulfillmentAssignment.findById(id);
    if (assignment == null) throw new WebApplicationException("Fulfillment assignment not found", 404);
    assignment.delete();
  }
}
