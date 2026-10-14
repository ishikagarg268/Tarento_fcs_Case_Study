package com.fulfilment.application.monolith.fulfilment.adapters.restapi;

import com.fulfilment.application.monolith.fulfilment.application.StoreProductWarehouseService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("store")
public class StoreProductWarehouseResource {

    @Inject
    StoreProductWarehouseService service;

    @POST
    @Path("{storeId}/product/{productId}/warehouse/{warehouseId}")
    public Response associate(
            @PathParam("storeId") Long storeId,
            @PathParam("productId") Long productId,
            @PathParam("warehouseId") Long warehouseId) {

        try {
            service.associate(storeId, productId, warehouseId);
            return Response.status(Response.Status.CREATED).build();
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(exception.getMessage())
                    .build();
        }
    }
}