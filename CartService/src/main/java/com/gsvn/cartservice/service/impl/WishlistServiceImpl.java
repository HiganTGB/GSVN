package com.gsvn.cartservice.service.impl;

import com.gsvn.cartservice.exc.AppException;
import com.gsvn.cartservice.exc.DuplicateResourceException;
import com.gsvn.cartservice.exc.ErrorCode;
import com.gsvn.cartservice.mapper.WishlistMapper;
import com.gsvn.cartservice.model.entity.Wishlist;
import com.gsvn.cartservice.service.AuthenticationService;
import com.gsvn.cartservice.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistMapper wishlistMapper;
    private final AuthenticationService authenticationService;

    public List<Wishlist> getMyWishlist() {
        Long customerId = authenticationService.getCustomerIdFromToken();
        if (customerId == null) throw new AppException(ErrorCode.UNAUTHENTICATED);

        return wishlistMapper.findByCustomerId(customerId);
    }

    @Transactional
    public void addToWishlist(Integer productId) {
        Long customerId = authenticationService.getCustomerIdFromToken();
        if (customerId == null) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (wishlistMapper.exists(customerId, productId)) {

            throw new DuplicateResourceException(ErrorCode.ITEM_EXISTED,"productId");
        }

        Wishlist wishlist = Wishlist.builder()
                .customerId(customerId)
                .productId(productId)
                .build();

        wishlistMapper.insert(wishlist);
    }

    @Transactional
    public void removeFromWishlist(Integer productId) {
        Long customerId = authenticationService.getCustomerIdFromToken();
        if (customerId == null) throw new AppException(ErrorCode.UNAUTHENTICATED);

        int deletedRows = wishlistMapper.delete(customerId, productId);
        if (deletedRows == 0) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }
    }
}