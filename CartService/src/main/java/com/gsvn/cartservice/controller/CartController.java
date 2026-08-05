package com.gsvn.cartservice.controller;

import com.gsvn.cartservice.common.ApiResponse;
import com.gsvn.cartservice.model.dto.request.AddToCartRequest;
import com.gsvn.cartservice.model.dto.request.GuestCartRequest;
import com.gsvn.cartservice.model.dto.request.UpdateCartItemRequest;
import com.gsvn.cartservice.model.dto.response.CartResponse;
import com.gsvn.cartservice.service.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Shopping Cart Management", description = "Endpoints for managing customer shopping carts, guest cart previews, cart item updates, and login cart synchronization")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Get my cart", description = "Retrieves current active shopping cart details and items for the authenticated user.")
    @GetMapping
    public ApiResponse<CartResponse> getMyCart() {
        return new ApiResponse<>(cartService.getMyCart());
    }

    @Operation(summary = "Add item to cart", description = "Adds a SKU item with a specified quantity to the authenticated user's shopping cart.")
    @PostMapping("/items")
    public ApiResponse<Boolean> addToCart(@RequestBody @Valid AddToCartRequest request) {
        cartService.addToCart(request);
        return new ApiResponse<>(true);
    }

    @Operation(summary = "Update cart item quantity", description = "Updates the quantity or selection status of a specific item in the shopping cart.")
    @PutMapping("/items/{itemId}")
    public ApiResponse<Boolean> updateItem(
            @Parameter(description = "ID of the cart item") @PathVariable Integer itemId,
            @RequestBody @Valid UpdateCartItemRequest request) {
        cartService.updateItem(itemId, request);
        return new ApiResponse<>(true);
    }

    @Operation(summary = "Remove item from cart", description = "Removes a specific item from the shopping cart by cart item ID.")
    @DeleteMapping("/items/{itemId}")
    public ApiResponse<Boolean> removeItem(
            @Parameter(description = "ID of the cart item to remove") @PathVariable Integer itemId) {
        cartService.removeItem(itemId);
        return new ApiResponse<>(true);
    }

    @Operation(summary = "Get guest cart details", description = "Calculates prices, availability, and totals for an unauthenticated guest cart based on a list of local item selections.")
    @PostMapping("/guest")
    public ApiResponse<CartResponse> getGuestCart(@RequestBody GuestCartRequest request) {
        return new ApiResponse<>(cartService.getGuestCart(request));
    }

    @Operation(summary = "Synchronize guest cart on login", description = "Merges local guest cart items into the authenticated user's persistent cart upon logging in.")
    @PostMapping("/sync")
    public ApiResponse<Void> syncCart(@RequestBody @Valid GuestCartRequest request) {
        cartService.syncCart(request);
        return new ApiResponse<>(null);
    }

    @Operation(summary = "Clear shopping cart", description = "Removes all items from the authenticated user's active shopping cart (e.g., after successful order placement).")
    @DeleteMapping("/clear")
    public ApiResponse<Void> clearCart() {
        cartService.clearMyCart();
        return new ApiResponse<>(null);
    }
}