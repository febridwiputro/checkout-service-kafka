package app.checkout.controller;

import app.checkout.dto.BaseResponse;
import app.checkout.dto.CheckoutRequestDto;
import app.checkout.services.CheckoutService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/checkout")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Checkout")
public class CheckoutController {

    @Inject
    CheckoutService checkoutService;

    @POST
    @Path("/")
    @Operation(summary = "Process Checkout")
    public Response processCheckout(@Valid CheckoutRequestDto request) {
        var order = checkoutService.processCheckout(request);
        return Response.ok(BaseResponse.created("Checkout completed successfully", order)).build();
    }

    @GET
    @Path("/order/{orderId}")
    @Operation(summary = "Get Order Status")
    public Response getOrderStatus(@PathParam("orderId") String orderId) {
        var order = checkoutService.getOrderStatus(orderId);
        return Response.ok(BaseResponse.success("Order status retrieved", order)).build();
    }
}