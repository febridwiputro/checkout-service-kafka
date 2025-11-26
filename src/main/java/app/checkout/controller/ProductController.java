package app.checkout.controller;

import app.checkout.dto.BaseResponse;
import app.checkout.dto.ProductDto;
import app.checkout.services.ProductService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Products", description = "Product catalog operations")
public class ProductController {

    @Inject
    ProductService productService;

    @GET
    @Path("/")
    @Operation(summary = "Get All Products")
    public Response getAllProducts(
        @QueryParam("category") String category
    ) {
        List<ProductDto> products;

        if (category != null && !category.isEmpty()) {
            products = productService.getProductsByCategory(category);
        } else {
            products = productService.getAllProducts();
        }

        String message = category != null 
            ? String.format("Products in category '%s' retrieved successfully", category)
            : "All products retrieved successfully";

        return Response.ok(BaseResponse.success(message, products)).build();
    }

    @GET
    @Path("/{productId}")
    @Operation(summary = "Get Product Detail")
    public Response getProductById(
        @PathParam("productId") String productId
    ) {
        ProductDto product = productService.getProductById(productId);

        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(BaseResponse.notFound("Product not found with ID: " + productId))
                .build();
        }

        return Response.ok(BaseResponse.success("Product detail retrieved successfully", product)).build();
    }

    @GET
    @Path("/category/{category}")
    @Operation(summary = "Get Products by Category")
    public Response getProductsByCategory(
        @PathParam("category") String category
    ) {
        List<ProductDto> products = productService.getProductsByCategory(category);

        return Response.ok(
            BaseResponse.success(
                String.format("Products in category '%s' retrieved successfully", category),
                products
            )
        ).build();
    }
}