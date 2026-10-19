package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.FulfillmentAssignment;
import com.fulfilment.application.monolith.warehouses.validation.FulfillmentValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;

@Path("/fulfillments")
@ApplicationScoped
@Consumes("application/json")
@Produces("application/json")
public class FulfillmentResource {
  @Inject FulfillmentValidator fulfillmentValidator;

  public static class AssignmentRequest {
    public Long storeId;
    public Long productId;
    public String warehouseBusinessUnitCode;
  }

  @POST
  @Transactional
  public AssignmentRequest assign(AssignmentRequest request) {
    if (request == null) {
      throw new WebApplicationException("Request body is required", 400);
    }
    fulfillmentValidator.validate(request.storeId, request.productId, request.warehouseBusinessUnitCode);

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
