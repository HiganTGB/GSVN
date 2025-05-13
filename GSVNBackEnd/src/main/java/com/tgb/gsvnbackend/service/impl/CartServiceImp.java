package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.exc.NotFoundException;
import com.tgb.gsvnbackend.model.entity.Cart;
import com.tgb.gsvnbackend.model.entity.CartItem;
import com.tgb.gsvnbackend.model.enumeration.CartStatus;
import com.tgb.gsvnbackend.repository.CartRepository;
import com.tgb.gsvnbackend.service.CartService;
import com.tgb.gsvnbackend.service.RedisHashOperationsService;
import com.tgb.gsvnbackend.service.client.SPUServiceClient;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.tgb.gsvnbackend.lib.Oath2UtilsConverter.getUserId;

@Service
public class CartServiceImp implements CartService {
    private final RedisHashOperationsService redisHashOperationsService;
    private final CartRepository cartRepository;

    private static final String CART_CACHE_KEY_PREFIX = "cart:";
    private static final String PRODUCT_CACHE_KEY_PREFIX = "sku:";

    public CartServiceImp(RedisHashOperationsService redisHashOperationsService, CartRepository cartRepository) {
        this.redisHashOperationsService = redisHashOperationsService;
        this.cartRepository = cartRepository;
    }

    public CartItem addItem(CartItem cartItem, Principal user) {
        String userId = getUserId(user);
        String cartCacheKey = CART_CACHE_KEY_PREFIX + userId;
        String productCacheKey = PRODUCT_CACHE_KEY_PREFIX + cartItem.getSkuId();

        if (redisHashOperationsService.hExists(cartCacheKey, productCacheKey)) {
            int newQuantity = redisHashOperationsService.hIncrBy(cartCacheKey, productCacheKey, cartItem.getQuantity());
            cartItem.setQuantity(newQuantity);
        } else {
            redisHashOperationsService.hSet(cartCacheKey, productCacheKey, cartItem.getQuantity());
        }

        Optional<Cart> currentCartOptional = cartRepository.findByUserIdAndStatus(userId, CartStatus.Active);
        currentCartOptional.ifPresentOrElse(currentCart -> {
            currentCart.getCartItems().removeIf(item -> item.getSkuId().equals(cartItem.getSkuId()));
            currentCart.getCartItems().add(cartItem);
            CompletableFuture.runAsync(() -> cartRepository.save(currentCart));
        }, () -> {
            Cart newCart = Cart.builder()
                    .userId(userId)
                    .status(CartStatus.Active)
                    .cartItems(List.of(cartItem))
                    .build();
            CompletableFuture.runAsync(() -> cartRepository.save(newCart));
        });
        return updateCartItemQuantityFromRedis(cartItem, userId);
    }

    public CartItem updateItem(CartItem cartItem, Principal user, int quantityChange) {
        String userId = getUserId(user);
        String cartCacheKey = CART_CACHE_KEY_PREFIX + userId;
        String productCacheKey = PRODUCT_CACHE_KEY_PREFIX + cartItem.getSkuId();

        if (redisHashOperationsService.hExists(cartCacheKey, productCacheKey)) {
            int newQuantityInCache = redisHashOperationsService.hIncrBy(cartCacheKey, productCacheKey, quantityChange);
            cartItem.setQuantity(newQuantityInCache);
        }

        Optional<Cart> currentCartOptional = cartRepository.findByUserIdAndStatus(userId, CartStatus.Active);
        Cart updatedCart = currentCartOptional.map(currentCart -> {
            currentCart.getCartItems().stream()
                    .filter(item -> item.getSkuId().equals(cartItem.getSkuId()))
                    .findFirst()
                    .ifPresent(item -> {
                        int newQuantityInList = item.getQuantity() + quantityChange;
                        item.setQuantity(newQuantityInList);
                        redisHashOperationsService.hSet(cartCacheKey, productCacheKey, newQuantityInList);
                    });
            return currentCart;
        }).orElseThrow(() -> new NotFoundException("CartItem not found"));
        CompletableFuture.runAsync(() -> cartRepository.save(updatedCart));
        return updateCartItemQuantityFromRedis(cartItem, userId);
    }

    public void deleteItem(CartItem cartItem, Principal user) {
        String userId = getUserId(user);
        String cartCacheKey = CART_CACHE_KEY_PREFIX + userId;
        String productCacheKey = PRODUCT_CACHE_KEY_PREFIX + cartItem.getSkuId();

        redisHashOperationsService.hDel(cartCacheKey, productCacheKey);

         Optional<Cart> currentCartOptional = cartRepository.findByUserIdAndStatus(userId, CartStatus.Active);
        Cart updatedCart =currentCartOptional.map(currentCart -> {
            currentCart.getCartItems().removeIf(item -> item.getSkuId().equals(cartItem.getSkuId()));
            return currentCart;
        }).orElseThrow(() -> new NotFoundException("Cart not found"));
        CompletableFuture.runAsync(() -> cartRepository.save(updatedCart));
    }
    public void cleanCart(Principal user)
    {
        String userId = getUserId(user);
        String cartCacheKey = CART_CACHE_KEY_PREFIX + userId;
        redisHashOperationsService.deleteByKey(cartCacheKey);

        Optional<Cart> currentCartOptional = cartRepository.findByUserIdAndStatus(userId, CartStatus.Active);
        currentCartOptional.ifPresent(cart -> {
            cart.setStatus(CartStatus.Abandoned);
            cart.getCartItems().clear();
            CompletableFuture.runAsync(() -> cartRepository.save(cart));
        });
    }
    private CartItem updateCartItemQuantityFromRedis(CartItem cartItem, String userId) {
        String cartCacheKey = CART_CACHE_KEY_PREFIX + userId;
        String productCacheKey = PRODUCT_CACHE_KEY_PREFIX + cartItem.getSkuId();
        Object redisValue = redisHashOperationsService.hGet(cartCacheKey, productCacheKey);
        if (redisValue instanceof Integer) {
            cartItem.setQuantity((Integer) redisValue);
        }
        return cartItem;
    }
    public List<CartItem> getCart(Principal user) {
        String userId = getUserId(user);
        String cartCacheKey = CART_CACHE_KEY_PREFIX + userId;
        Map<Object, Object> cartEntries = redisHashOperationsService.hGetAll(cartCacheKey);
        List<CartItem> cartItems = new ArrayList<>();
        if (cartEntries.isEmpty()) {

            Optional<Cart> cartFromDbOptional = cartRepository.findByUserIdAndStatus(userId, CartStatus.Active);
            return cartFromDbOptional.map(cartFromDb -> {
                cartFromDb.getCartItems().forEach(item ->
                        redisHashOperationsService.hSet(cartCacheKey, PRODUCT_CACHE_KEY_PREFIX + item.getSkuId(), item.getQuantity()));
                return cartFromDb.getCartItems();
            }).orElse(new ArrayList<>());
        } else {

            for (Map.Entry<Object, Object> entry : cartEntries.entrySet()) {
                    String skuId = ((String) entry.getKey()).substring(PRODUCT_CACHE_KEY_PREFIX.length());
                    int quantity = Integer.parseInt(entry.getValue().toString());
                    CartItem cartItem = new CartItem(skuId,quantity);
                    cartItem.setSkuId(skuId);
                    cartItem.setQuantity(quantity);
                    cartItems.add(cartItem);
            }
            return cartItems;
        }
    }
}