package com.tgb.gsvnbackend.service;

import com.tgb.gsvnbackend.model.entity.CartItem;

import java.security.Principal;
import java.util.List;

public interface CartService {
    CartItem addItem(CartItem cartItem, Principal user);
    CartItem updateItem(CartItem cartItem, Principal user, int alteration);
    void deleteItem(CartItem cartItem, Principal user);
    void cleanCart(Principal user);
    List<CartItem> getCart(Principal user);
}
