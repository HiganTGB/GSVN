package com.gsvn.cartservice.controller;

import com.gsvn.cartservice.common.ApiResponse;
import com.gsvn.cartservice.model.entity.Wishlist;
import com.gsvn.cartservice.service.WishlistService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist Management", description = "Self-service endpoints for managing customer favorite product wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    @Operation(summary = "Get my wishlist", description = "Retrieves a list of all saved favorite products for the currently authenticated customer.")
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ApiResponse<List<Wishlist>> getMyWishlist() {
        return new ApiResponse<>(wishlistService.getMyWishlist());
    }

    @Operation(summary = "Add product to wishlist", description = "Adds a specific master product to the logged-in customer's wishlist.")
    @PostMapping("/{productId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ApiResponse<Void> addToWishlist(
            @Parameter(description = "ID of the product to add") @PathVariable Integer productId) {
        wishlistService.addToWishlist(productId);
        return new ApiResponse<>(null);
    }

    @Operation(summary = "Remove product from wishlist", description = "Removes a specific product from the logged-in customer's wishlist.")
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ApiResponse<Void> removeFromWishlist(
            @Parameter(description = "ID of the product to remove") @PathVariable Integer productId) {
        wishlistService.removeFromWishlist(productId);
        return new ApiResponse<>(null);
    }
}