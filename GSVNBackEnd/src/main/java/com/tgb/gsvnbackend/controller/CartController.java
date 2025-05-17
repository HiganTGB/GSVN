package com.tgb.gsvnbackend.controller;

import com.tgb.gsvnbackend.model.dto.CartStatusDTO;
import com.tgb.gsvnbackend.model.entity.CartItem;
import com.tgb.gsvnbackend.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")

    @PreAuthorize("hasAnyAuthority('ROLE_customer')")
    public ResponseEntity<CartItem> addItem(@RequestBody CartItem cartItem, Principal user) {
        CartItem addedItem = cartService.addItem(cartItem, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedItem);
    }

    @PutMapping("/items")

    @PreAuthorize("hasAnyAuthority('ROLE_customer')")
    public ResponseEntity<CartItem> updateItem(@RequestBody CartItem cartItem, Principal user, @RequestParam int alteration) {
        CartItem updatedItem = cartService.updateItem(cartItem, user, alteration);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/items")

    @PreAuthorize("hasAnyAuthority('ROLE_customer')")
    public ResponseEntity<Void> deleteItem(@RequestBody CartItem cartItem, Principal user) {
        cartService.deleteItem(cartItem, user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clean")

    @PreAuthorize("hasAnyAuthority('ROLE_customer')")
    public ResponseEntity<Void> cleanCart(Principal user) {
        cartService.cleanCart(user);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('ROLE_customer')")
    public ResponseEntity<List<CartItem>> getCart(Principal user) {
        List<CartItem> cartItems = cartService.getCart(user);
        return ResponseEntity.ok(cartItems);
    }
    @GetMapping("/{cartId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_customer')")
    public ResponseEntity<CartStatusDTO> getCartStatus(Principal user, @PathVariable String cartId) {

        return ResponseEntity.ok(cartService.cartStatus(user,cartId));
    }
}
