package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.exc.NotFoundException;
import com.tgb.gsvnbackend.lib.VNPayUtils;
import com.tgb.gsvnbackend.model.dto.CartStatusDTO;
import com.tgb.gsvnbackend.model.entity.Cart;
import com.tgb.gsvnbackend.model.entity.CartItem;
import com.tgb.gsvnbackend.model.enumeration.CartStatus;
import com.tgb.gsvnbackend.model.enumeration.PaymentMethod;
import com.tgb.gsvnbackend.queue.producer.CartProducer;
import com.tgb.gsvnbackend.repository.mongoRepository.CartRepository;
import com.tgb.gsvnbackend.service.CachingService;
import com.tgb.gsvnbackend.service.CartService;
import com.tgb.gsvnbackend.service.RedisHashOperationsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.tgb.gsvnbackend.lib.Oath2UtilsConverter.getUserId;

@Service
@Slf4j
public class CartServiceImp implements CartService {

    private final RedisHashOperationsService redisHashOperationsService;
    private final CachingService cachingService;
    private final CartRepository cartRepository;
    private final CartProducer cartProducer;
    private static final String CART_STATUS_CACHE_KEY_PREFIX = "cartStatus:";
    private static final String CART_CACHE_KEY_PREFIX = "cart:";
    private static final String PRODUCT_CACHE_KEY_PREFIX = "sku:";
    @Autowired
    public CartServiceImp(RedisHashOperationsService redisHashOperationsService, CachingService cachingService, CartRepository cartRepository, CartProducer cartProducer) {
        this.redisHashOperationsService = redisHashOperationsService;
        this.cachingService = cachingService;
        this.cartRepository = cartRepository;
        this.cartProducer = cartProducer;
        log.info("CartServiceImp initialized.");
    }

    public CartItem addItem(CartItem cartItem, Principal user) {
        String userId = getUserId(user);
        String cartCacheKey = CART_CACHE_KEY_PREFIX + userId;
        String productCacheKey = PRODUCT_CACHE_KEY_PREFIX + cartItem.getSkuId();
        log.info("Adding item with SKU ID {} to cart for user ID {}", cartItem.getSkuId(), userId);

        if (redisHashOperationsService.hExists(cartCacheKey, productCacheKey)) {
            int newQuantity = redisHashOperationsService.hIncrBy(cartCacheKey, productCacheKey, cartItem.getQuantity());
            cartItem.setQuantity(newQuantity);
            log.info("Item with SKU ID {} already in cart for user ID {}, quantity updated to {}", cartItem.getSkuId(), userId, newQuantity);
        } else {
            redisHashOperationsService.hSet(cartCacheKey, productCacheKey, cartItem.getQuantity());
            log.info("Item with SKU ID {} added to cart for user ID {} with quantity {}", cartItem.getSkuId(), userId, cartItem.getQuantity());
        }

        Optional<Cart> currentCartOptional = cartRepository.findByUserIdAndStatus(userId, CartStatus.Active);
        currentCartOptional.ifPresentOrElse(currentCart -> {
            currentCart.getCartItems().removeIf(item -> item.getSkuId().equals(cartItem.getSkuId()));
            currentCart.getCartItems().add(cartItem);
            CompletableFuture.runAsync(() -> {
                cartRepository.save(currentCart);
                log.info("Cart updated in database for user ID {}", userId);
            });
        }, () -> {
            Cart newCart = Cart.builder()
                    .userId(userId)
                    .status(CartStatus.Active)
                    .cartItems(List.of(cartItem))
                    .build();
            CompletableFuture.runAsync(() -> {
                cartRepository.save(newCart);
                log.info("New cart created in database for user ID {}", userId);
            });
        });
        return updateCartItemQuantityFromRedis(cartItem, userId);
    }

    public CartItem updateItem(CartItem cartItem, Principal user, int quantityChange) {
        String userId = getUserId(user);
        String cartCacheKey = CART_CACHE_KEY_PREFIX + userId;
        String productCacheKey = PRODUCT_CACHE_KEY_PREFIX + cartItem.getSkuId();
        log.info("Updating quantity of item with SKU ID {} in cart for user ID {}, quantity change: {}", cartItem.getSkuId(), userId, quantityChange);

        if (redisHashOperationsService.hExists(cartCacheKey, productCacheKey)) {
            int newQuantityInCache = redisHashOperationsService.hIncrBy(cartCacheKey, productCacheKey, quantityChange);
            cartItem.setQuantity(newQuantityInCache);
            log.info("Quantity of item with SKU ID {} in cart for user ID {} updated in cache to {}", cartItem.getSkuId(), userId, newQuantityInCache);
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
                        log.info("Quantity of item with SKU ID {} in cart for user ID {} updated in cart object to {}", cartItem.getSkuId(), userId, newQuantityInList);
                    });
            return currentCart;
        }).orElseThrow(() -> {
            log.error("CartItem not found while updating for user ID {}", userId);
            return new NotFoundException("CartItem not found");
        });
        CompletableFuture.runAsync(() -> {
            cartRepository.save(updatedCart);
            log.info("Cart updated in database for user ID {}", userId);
        });
        return updateCartItemQuantityFromRedis(cartItem, userId);
    }

    public void deleteItem(CartItem cartItem, Principal user) {
        String userId = getUserId(user);
        String cartCacheKey = CART_CACHE_KEY_PREFIX + userId;
        String productCacheKey = PRODUCT_CACHE_KEY_PREFIX + cartItem.getSkuId();
        log.info("Deleting item with SKU ID {} from cart for user ID {}", cartItem.getSkuId(), userId);

        redisHashOperationsService.hDel(cartCacheKey, productCacheKey);
        log.info("Item with SKU ID {} removed from cache for user ID {}", cartItem.getSkuId(), userId);

        Optional<Cart> currentCartOptional = cartRepository.findByUserIdAndStatus(userId, CartStatus.Active);
        Cart updatedCart =currentCartOptional.map(currentCart -> {
            currentCart.getCartItems().removeIf(item -> item.getSkuId().equals(cartItem.getSkuId()));
            return currentCart;
        }).orElseThrow(() -> {
            log.error("Cart not found while deleting item for user ID {}", userId);
            return new NotFoundException("Cart not found");
        });
        CompletableFuture.runAsync(() -> {
            cartRepository.save(updatedCart);
            log.info("Cart updated in database after deleting item for user ID {}", userId);
        });
    }
    public void summitCart(Principal user,
                           String note,
                           String receiver,
                           String street,
                           String city,
                           String state,
                           String zip,
                           String phone,
                           PaymentMethod paymentMethod,
                           HttpServletRequest request)
    {
        String userId = getUserId(user);
        String cartCacheKey = CART_CACHE_KEY_PREFIX + userId;
        log.info("Submitting cart for user ID {}", userId);
        redisHashOperationsService.deleteByKey(cartCacheKey);
        log.info("Cart data removed from cache for user ID {}", userId);
        Optional<Cart> currentCartOptional = cartRepository.findByUserIdAndStatus(userId, CartStatus.Active);
        currentCartOptional.ifPresent(cart -> {
            cart.setStatus(CartStatus.Pending);
            cartProducer.sendOrderInit(cart,note,receiver,street,city,state,zip,phone,paymentMethod, VNPayUtils.getIpAddress(request));
            log.info("Order initialization message sent to queue for cart ID {}", cart.getCardId());
            CompletableFuture.runAsync(() -> {
                cartRepository.save(cart);
                log.info("Cart status updated to Pending in database for cart ID {}", cart.getCardId());
            });
        });

    }
    public void cleanCart(Principal user)
    {
        String userId = getUserId(user);
        String cartCacheKey = CART_CACHE_KEY_PREFIX + userId;
        log.info("Cleaning cart for user ID {}", userId);
        redisHashOperationsService.deleteByKey(cartCacheKey);
        log.info("Cart data removed from cache for user ID {}", userId);

        Optional<Cart> currentCartOptional = cartRepository.findByUserIdAndStatus(userId, CartStatus.Active);
        currentCartOptional.ifPresent(cart -> {
            cart.setStatus(CartStatus.Abandoned);
            cart.getCartItems().clear();
            CompletableFuture.runAsync(() -> {
                cartRepository.save(cart);
                log.info("Cart status updated to Abandoned and items cleared in database for cart ID {}", cart.getCardId());
            });
        });
    }
    private CartItem updateCartItemQuantityFromRedis(CartItem cartItem, String userId) {
        String cartCacheKey = CART_CACHE_KEY_PREFIX + userId;
        String productCacheKey = PRODUCT_CACHE_KEY_PREFIX + cartItem.getSkuId();
        Object redisValue = redisHashOperationsService.hGet(cartCacheKey, productCacheKey);
        if (redisValue instanceof Integer) {
            cartItem.setQuantity((Integer) redisValue);
            log.debug("Cart item quantity updated from Redis for SKU ID {} and user ID {}: {}", cartItem.getSkuId(), userId, cartItem.getQuantity());
        }
        return cartItem;
    }
    public List<CartItem> getCart(Principal user) {
        String userId = getUserId(user);
        String cartCacheKey = CART_CACHE_KEY_PREFIX + userId;
        log.info("Getting cart for user ID {}", userId);
        Map<Object, Object> cartEntries = redisHashOperationsService.hGetAll(cartCacheKey);
        List<CartItem> cartItems = new ArrayList<>();
        if (cartEntries.isEmpty()) {
            log.info("Cart not found in cache for user ID {}, fetching from database.", userId);
            Optional<Cart> cartFromDbOptional = cartRepository.findByUserIdAndStatus(userId, CartStatus.Active);
            return cartFromDbOptional.map(cartFromDb -> {
                cartFromDb.getCartItems().forEach(item ->
                        redisHashOperationsService.hSet(cartCacheKey, PRODUCT_CACHE_KEY_PREFIX + item.getSkuId(), item.getQuantity()));
                log.info("Cart fetched from database for user ID {} and saved to cache.", userId);
                return cartFromDb.getCartItems();
            }).orElseGet(() -> {
                log.info("No active cart found in database for user ID {}.", userId);
                return new ArrayList<>();
            });
        } else {
            log.info("Cart found in cache for user ID {}.", userId);
            for (Map.Entry<Object, Object> entry : cartEntries.entrySet()) {
                String skuId = ((String) entry.getKey()).substring(PRODUCT_CACHE_KEY_PREFIX.length());
                int quantity = Integer.parseInt(entry.getValue().toString());
                CartItem cartItem = new CartItem(skuId,quantity);
                cartItem.setSkuId(skuId);
                cartItem.setQuantity(quantity);
                cartItems.add(cartItem);
                log.debug("Cart item from cache - SKU ID: {}, Quantity: {}", skuId, quantity);
            }
            return cartItems;
        }
    }
    public void handleCartResultReceiver(String cartId,String orderId,String paymentUrl,boolean success)
    {
        log.info("Handling cart result receiver for cart ID {}, order ID {}, success: {}", cartId, orderId, success);
        CartStatus status=(success)? CartStatus.Success:CartStatus.Fail;
        cartRepository.findById(cartId).ifPresent(x-> {
            x.setStatus(status);
            CompletableFuture.runAsync(() -> cartRepository.save(x));
            log.info("Cart status updated to {} for cart ID {}", status, cartId);
        });
        CartStatusDTO statusDTO=new CartStatusDTO(cartId,orderId,status,paymentUrl);
        cachingService.saveById(CART_STATUS_CACHE_KEY_PREFIX,cartId,statusDTO,CartStatusDTO.class);
        log.info("Cart status DTO saved to cache for cart ID {}", cartId);
    }
    public CartStatusDTO cartStatus(Principal user,String cartId)
    {
        String userId = getUserId(user);
        log.info("Getting cart status for user ID {}, cart ID {}", userId, cartId);
        CartStatusDTO statusDTO=cachingService.getById(CART_STATUS_CACHE_KEY_PREFIX,cartId,CartStatusDTO.class);
        if(statusDTO!=null)
        {
            log.info("Cart status DTO found in cache for cart ID {}", cartId);
            return statusDTO;
        }
        log.info("Cart status DTO not found in cache for cart ID {}, fetching from database.", cartId);
        Cart cart= cartRepository.findById(cartId).orElse(new Cart(cartId,userId,CartStatus.Abandoned, Collections.emptyList()));
        statusDTO=new CartStatusDTO(cart.getCardId(),null,cart.getStatus(),null);
        cachingService.saveById(CART_STATUS_CACHE_KEY_PREFIX,cartId,statusDTO,CartStatusDTO.class);
        log.info("Cart status DTO saved to cache for cart ID {}", cartId);
        return statusDTO;
    }
}