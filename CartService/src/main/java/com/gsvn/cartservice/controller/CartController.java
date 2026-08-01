package com.gsvn.cartservice.controller;

import com.gsvn.cartservice.common.ApiResponse;
import com.gsvn.cartservice.model.dto.request.AddToCartRequest;
import com.gsvn.cartservice.model.dto.request.GuestCartRequest;
import com.gsvn.cartservice.model.dto.request.UpdateCartItemRequest;
import com.gsvn.cartservice.model.dto.response.CartResponse;
import com.gsvn.cartservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ApiResponse<CartResponse> getMyCart() {
        return new ApiResponse<>(cartService.getMyCart());
    }

    @PostMapping("/items")
    public ApiResponse<Boolean> addToCart(@RequestBody @Valid AddToCartRequest request) {
        cartService.addToCart(request);
        return new ApiResponse<>(true);
    }

    @PutMapping("/items/{itemId}")
    public ApiResponse<Boolean> updateItem(
            @PathVariable Integer itemId,
            @RequestBody @Valid UpdateCartItemRequest request) {
        cartService.updateItem(itemId, request);
        return new ApiResponse<>(true);
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<Boolean> removeItem(@PathVariable Integer itemId) {
        cartService.removeItem(itemId);
        return new ApiResponse<>(true);
    }
    @PostMapping("/guest")
    public ApiResponse<CartResponse> getGuestCart(@RequestBody GuestCartRequest request) {
        return new ApiResponse<>(cartService.getGuestCart(request));
    }
    @PostMapping("/sync")
    public ApiResponse<Void> syncCart(@RequestBody @Valid GuestCartRequest request) {
        cartService.syncCart(request);
        return new ApiResponse<>(null);
    }
    @DeleteMapping("/clear")
    public ApiResponse<Void> clearCart() {
        cartService.clearMyCart();
        return new ApiResponse<>(null);
    }
}