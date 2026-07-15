package com.gsvn.cartservice.service;

import com.gsvn.cartservice.client.SkuSearchInternalClient;
import com.gsvn.cartservice.exc.AppException;
import com.gsvn.cartservice.exc.ErrorCode;
import com.gsvn.cartservice.mapper.CartItemMapper;
import com.gsvn.cartservice.mapper.CartMapper;
import com.gsvn.cartservice.model.dto.request.AddToCartRequest;
import com.gsvn.cartservice.model.dto.request.GuestCartRequest;
import com.gsvn.cartservice.model.dto.request.UpdateCartItemRequest;
import com.gsvn.cartservice.model.dto.response.CartItemResponse;
import com.gsvn.cartservice.model.dto.response.CartResponse;
import com.gsvn.cartservice.model.entity.Cart;
import com.gsvn.cartservice.model.entity.CartItem;
import com.gsvn.cartservice.model.internal.SkuCartDetailsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final SkuSearchInternalClient skuSearchInternalClient;
    private final AuthenticationService authenticationService;

    public CartResponse getMyCart() {
        Long customerId = authenticationService.getCustomerIdFromToken();
        if (customerId == null) {
            return new CartResponse();
        }

        Cart cart = cartMapper.findByCustomerId(customerId)
                .orElseGet(() -> createNewCart(customerId));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return buildEmptyResponse(cart);
        }

        List<Long> skuIds = cart.getItems().stream().map(CartItem::getSkuId).toList();
        var skuDetailsResponse = skuSearchInternalClient.getCartDetails(skuIds);

        if (skuDetailsResponse == null || skuDetailsResponse.result() == null) {
            return buildEmptyResponse(cart);
        }

        Map<Long, SkuCartDetailsDTO> detailMap = skuDetailsResponse.result().stream()
                .collect(Collectors.toMap(SkuCartDetailsDTO::getSkuId, d -> d));

        List<CartItemResponse> itemResponses = new ArrayList<>();

        BigDecimal originalTotal = BigDecimal.ZERO;
        BigDecimal preTotal = BigDecimal.ZERO;
        BigDecimal depositTotal = BigDecimal.ZERO;
        BigDecimal finalTotal = BigDecimal.ZERO;
        BigDecimal requiredTotal = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (CartItem entity : cart.getItems()) {
            SkuCartDetailsDTO detail = detailMap.get(entity.getSkuId());
            if (detail == null) continue;

            CartItemResponse itemRes = mapToItemResponse(entity, detail);
            itemResponses.add(itemRes);

            if (Boolean.TRUE.equals(itemRes.getIsAvailable())) {

                totalQuantity += entity.getQuantity();

                originalTotal = originalTotal.add(itemRes.getSubOriginalTotal());
                preTotal = preTotal.add(itemRes.getSubPreTotal());

                BigDecimal itemActualPrice = detail.getIsPreOrder() ? detail.getPrePrice() : detail.getSellingPrice();
                finalTotal = finalTotal.add(itemActualPrice.multiply(BigDecimal.valueOf(entity.getQuantity())));

                if (detail.getIsPreOrder()) {
                    if (Boolean.TRUE.equals(entity.getIsDeposit())) {
                        requiredTotal = requiredTotal.add(itemRes.getSubDepositTotal());
                        depositTotal = depositTotal.add(itemRes.getSubDepositTotal());
                    } else {
                        requiredTotal = requiredTotal.add(itemRes.getSubPreTotal());
                    }
                } else {
                    requiredTotal = requiredTotal.add(itemRes.getSubOriginalTotal());
                }
            }
        }

        return CartResponse.builder()
                .id(cart.getId())
                .customerId(customerId)
                .items(itemResponses)
                .totalQuantity(totalQuantity)
                .originalTotal(originalTotal)
                .preTotal(preTotal)
                .depositTotal(depositTotal)
                .finalTotal(finalTotal)
                .requiredTotal(requiredTotal)
                .build();
    }

    @Transactional
    public void addToCart(AddToCartRequest request) {
        Long customerId = authenticationService.getCustomerIdFromToken();
        if (customerId == null) throw new AppException(ErrorCode.UNAUTHENTICATED);

        Cart cart = cartMapper.findByCustomerId(customerId)
                .orElseGet(() -> createNewCart(customerId));

        validateStockBeforeAdd(request.getSkuId(), request.getQuantity());

        CartItem existingItem = cartItemMapper.findByCartIdAndSkuId(cart.getId(), request.getSkuId());

        if (existingItem != null) {
            int newQty = existingItem.getQuantity() + request.getQuantity();
            cartItemMapper.updateQuantityAndDeposit(existingItem.getId(), newQty, request.getIsDeposit());
        } else {
            CartItem newItem = CartItem.builder()
                    .cartId(cart.getId())
                    .skuId(request.getSkuId())
                    .quantity(request.getQuantity())
                    .isDeposit(request.getIsDeposit())
                    .build();
            cartItemMapper.insert(newItem);
        }
    }

    @Transactional
    public void updateItem(Integer itemId, UpdateCartItemRequest request) {
        Long customerId = authenticationService.getCustomerIdFromToken();
        CartItem item = cartItemMapper.findById(itemId);
        if (item == null) throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        validateItemOwnership(itemId, customerId);

        validateStockBeforeAdd(item.getSkuId(), request.getQuantity());

        cartItemMapper.updateQuantityAndDeposit(itemId, request.getQuantity(), request.getIsDeposit());
    }

    @Transactional
    public void removeItem(Integer itemId) {
        Long customerId = authenticationService.getCustomerIdFromToken();

        validateItemOwnership(itemId, customerId);

        cartItemMapper.deleteById(itemId);
    }

    private void validateItemOwnership(Integer itemId, Long customerId) {
        CartItem item = cartItemMapper.findById(itemId);
        if (item == null) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }

        Cart cart = cartMapper.findById(item.getCartId());
        if (cart == null) {
            throw new AppException(ErrorCode.CART_NOT_FOUND);
        }

        if (!cart.getCustomerId().equals(customerId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    private Cart createNewCart(Long customerId) {
        Cart cart = Cart.builder().customerId(customerId).build();
        cartMapper.insert(cart);
        return cart;
    }

    private void validateStockBeforeAdd(Long skuId, Integer quantity) {
        var detailRes = skuSearchInternalClient.getCartDetails(List.of(skuId)).result();
        if (detailRes == null || detailRes.isEmpty()) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }

        SkuCartDetailsDTO detail = detailRes.get(0);

        if (detail.getIsPreOrder()) {
            if (detail.getPrePerQty() != null && quantity > detail.getPrePerQty()) {
                throw new AppException(ErrorCode.NOT_ALLOW);
            }
            if (detail.getPreOrders() != null && detail.getPreLimit() != null) {
                if (detail.getPreOrders() + quantity > detail.getPreLimit()) {
                    throw new AppException(ErrorCode.NOT_ALLOW);
                }
            }
        }
        else {
            if (detail.getPhysicalAvailable() == null || detail.getPhysicalAvailable() < quantity) {
                throw new AppException(ErrorCode.NOT_ALLOW);
            }
        }
    }

    private CartItemResponse mapToItemResponse(CartItem entity, SkuCartDetailsDTO detail) {
        BigDecimal qty = BigDecimal.valueOf(entity.getQuantity());


        BigDecimal subOriginalTotal = detail.getSellingPrice().multiply(qty);
        BigDecimal subPreTotal = (detail.getIsPreOrder() && detail.getPrePrice() != null)
                ? detail.getPrePrice().multiply(qty) : subOriginalTotal;
        BigDecimal subDepositTotal = (detail.getIsPreOrder() && detail.getPreDepositAmount() != null)
                ? detail.getPreDepositAmount().multiply(qty) : BigDecimal.ZERO;


        Long stock = detail.getPhysicalAvailable() != null ? detail.getPhysicalAvailable() : 0L;
        if (detail.getIsPreOrder()) {
            long preRemain = (detail.getPreLimit() != null ? detail.getPreLimit() : 0)
                    - (detail.getPreOrders() != null ? detail.getPreOrders() : 0);
            stock = Math.max(0, preRemain);
        }
        long maxAvailable = (detail.getPrePerQty() != null) ? Math.min(stock, detail.getPrePerQty()) : stock;

        return CartItemResponse.builder()
                .id(entity.getId())
                .skuId(entity.getSkuId())
                .skuCode(detail.getSkuCode())
                .quantity(entity.getQuantity())
                .isDeposit(entity.getIsDeposit())
                .productId(detail.getProductId())
                .productName(detail.getProductName())
                .imageUrl(detail.getImageUrl())
                // Prices
                .originalPrice(detail.getSellingPrice())
                .prePrice(detail.getPrePrice())
                .depositAmount(detail.getPreDepositAmount())
                // Sub Totals
                .subOriginalTotal(subOriginalTotal)
                .subPreTotal(subPreTotal)
                .subDepositTotal(subDepositTotal)
                // Pre-order Info
                .isPreOrder(detail.getIsPreOrder())
                .preStartAt(detail.getPreStartAt())
                .preEndAt(detail.getPreEndAt())
                .preReleaseDate(detail.getPreReleaseDate())
                // Status
                .isAvailable(Boolean.TRUE.equals(detail.getIsProductActive()) && Boolean.TRUE.equals(detail.getIsSkuActive()))
                .maxAvailable(maxAvailable)
                .build();
    }

    private CartResponse buildEmptyResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .customerId(cart.getCustomerId())
                .items(Collections.emptyList())
                .totalQuantity(0)
                .originalTotal(BigDecimal.ZERO)
                .preTotal(BigDecimal.ZERO)
                .depositTotal(BigDecimal.ZERO)
                .build();
    }

    public CartResponse getGuestCart(GuestCartRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return CartResponse.builder()
                    .items(Collections.emptyList())
                    .totalQuantity(0)
                    .originalTotal(BigDecimal.ZERO)
                    .preTotal(BigDecimal.ZERO)
                    .depositTotal(BigDecimal.ZERO)
                    .finalTotal(BigDecimal.ZERO)
                    .requiredTotal(BigDecimal.ZERO)
                    .build();
        }

        List<Long> skuIds = request.getItems().stream()
                .map(GuestCartRequest.GuestCartItem::getSkuId)
                .toList();
        var skuDetailsResponse = skuSearchInternalClient.getCartDetails(skuIds);
        if (skuDetailsResponse == null || skuDetailsResponse.result() == null) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }

        Map<Long, SkuCartDetailsDTO> detailMap = skuDetailsResponse.result().stream()
                .collect(Collectors.toMap(SkuCartDetailsDTO::getSkuId, d -> d));

        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal originalTotal = BigDecimal.ZERO;
        BigDecimal preTotal = BigDecimal.ZERO;
        BigDecimal depositTotal = BigDecimal.ZERO;
        BigDecimal finalTotal = BigDecimal.ZERO;
        BigDecimal requiredTotal = BigDecimal.ZERO;

        for (GuestCartRequest.GuestCartItem guestItem : request.getItems()) {
            SkuCartDetailsDTO detail = detailMap.get(guestItem.getSkuId());
            if (detail == null) continue;

            CartItem mockEntity = CartItem.builder()
                    .skuId(guestItem.getSkuId())
                    .quantity(guestItem.getQuantity())
                    .isDeposit(guestItem.getIsDeposit())
                    .build();

            CartItemResponse itemRes = mapToItemResponse(mockEntity, detail);
            itemResponses.add(itemRes);

            if (itemRes.getIsAvailable()) {
                BigDecimal qty = BigDecimal.valueOf(guestItem.getQuantity());

                originalTotal = originalTotal.add(itemRes.getSubOriginalTotal());
                preTotal = preTotal.add(itemRes.getSubPreTotal());
                depositTotal = depositTotal.add(itemRes.getSubDepositTotal());

                BigDecimal itemFinalPrice = detail.getIsPreOrder() ? detail.getPrePrice() : detail.getSellingPrice();
                finalTotal = finalTotal.add(itemFinalPrice.multiply(qty));

                if (Boolean.TRUE.equals(guestItem.getIsDeposit()) && Boolean.TRUE.equals(detail.getIsPreOrder())) {
                    requiredTotal = requiredTotal.add(detail.getPreDepositAmount().multiply(qty));
                } else if (Boolean.TRUE.equals(detail.getIsPreOrder())) {
                    requiredTotal = requiredTotal.add(detail.getPrePrice().multiply(qty));
                } else {
                    requiredTotal = requiredTotal.add(detail.getSellingPrice().multiply(qty));
                }
            }
        }

        return CartResponse.builder()
                .id(null)
                .customerId(null)
                .items(itemResponses)
                .totalQuantity(itemResponses.size())
                .originalTotal(originalTotal)
                .preTotal(preTotal)
                .depositTotal(depositTotal)
                .finalTotal(finalTotal)
                .requiredTotal(requiredTotal)
                .build();
    }

    @Transactional
    public void syncCart(GuestCartRequest request) {
        Long customerId = authenticationService.getCustomerIdFromToken();
        if (customerId == null) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (request.getItems() == null || request.getItems().isEmpty()) {
            return;
        }

        Cart cart = cartMapper.findByCustomerId(customerId)
                .orElseGet(() -> createNewCart(customerId));

        List<CartItem> existingItems = cartItemMapper.findByCartId(cart.getId());
        Map<Long, CartItem> dbItemMap = existingItems.stream()
                .collect(Collectors.toMap(CartItem::getSkuId, item -> item));

        for (GuestCartRequest.GuestCartItem guestItem : request.getItems()) {
            try {
                validateStockBeforeAdd(guestItem.getSkuId(), guestItem.getQuantity());
            } catch (Exception e) {
                continue;
            }

            if (dbItemMap.containsKey(guestItem.getSkuId())) {

                CartItem dbItem = dbItemMap.get(guestItem.getSkuId());
                int totalQty = dbItem.getQuantity() + guestItem.getQuantity();

                cartItemMapper.updateQuantityAndDeposit(dbItem.getId(), totalQty, guestItem.getIsDeposit());
            } else {
                CartItem newItem = CartItem.builder()
                        .cartId(cart.getId())
                        .skuId(guestItem.getSkuId())
                        .quantity(guestItem.getQuantity())
                        .isDeposit(guestItem.getIsDeposit())
                        .build();
                cartItemMapper.insert(newItem);
            }
        }
    }
    @Transactional
    public void clearMyCart() {
        Long customerId = authenticationService.getCustomerIdFromToken();
        if (customerId == null) throw new AppException(ErrorCode.UNAUTHENTICATED);

        Cart cart = cartMapper.findByCustomerId(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        int deletedCount = cartItemMapper.deleteByCartId(cart.getId());

    }
}