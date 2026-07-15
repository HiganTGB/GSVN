package com.gsvn.cartservice.controller;

import com.gsvn.cartservice.common.ApiResponse;
import com.gsvn.cartservice.model.entity.Wishlist;
import com.gsvn.cartservice.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ApiResponse<List<Wishlist>> getMyWishlist() {
        return new ApiResponse<>(wishlistService.getMyWishlist());
    }

    @PostMapping("/{productId}")
    public ApiResponse<Void> addToWishlist(@PathVariable Integer productId) {
        wishlistService.addToWishlist(productId);
        return new ApiResponse<>( null);
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Void> removeFromWishlist(@PathVariable Integer productId) {
        wishlistService.removeFromWishlist(productId);
        return new ApiResponse<>(null);
    }
}