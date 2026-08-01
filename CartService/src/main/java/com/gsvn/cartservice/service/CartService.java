package com.gsvn.cartservice.service;

import com.gsvn.cartservice.model.dto.request.AddToCartRequest;
import com.gsvn.cartservice.model.dto.request.GuestCartRequest;
import com.gsvn.cartservice.model.dto.request.UpdateCartItemRequest;
import com.gsvn.cartservice.model.dto.response.CartResponse;

public interface CartService {

    CartResponse getMyCart();

    void addToCart(AddToCartRequest request);

    void updateItem(Integer itemId, UpdateCartItemRequest request);

    void removeItem(Integer itemId);

    CartResponse getGuestCart(GuestCartRequest request);

    void syncCart(GuestCartRequest request);

    void clearMyCart();
}