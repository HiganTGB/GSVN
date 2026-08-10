package com.gsvn.cartservice.service;

import com.gsvn.cartservice.model.entity.Wishlist;

import java.util.List;

public interface WishlistService {

    List<Wishlist> getMyWishlist();

    void addToWishlist(Integer productId);

    void removeFromWishlist(Integer productId);
}