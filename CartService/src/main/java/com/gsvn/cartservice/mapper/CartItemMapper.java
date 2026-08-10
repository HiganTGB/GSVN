package com.gsvn.cartservice.mapper;

import com.gsvn.cartservice.model.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartItemMapper {
    int insert(CartItem cartItem);

    int updateQuantity(@Param("id") Integer id, @Param("quantity") Integer quantity);

    int deleteById(@Param("id") Integer id);

    int deleteByCartId(@Param("cartId") Integer cartId);

    int updateQuantityAndDeposit(
            @Param("id") Integer id,
            @Param("quantity") Integer quantity,
            @Param("isDeposit") Boolean isDeposit
    );
    CartItem findById(@Param("id") Integer id);

    CartItem findByCartIdAndSkuId(@Param("cartId") Integer cartId, @Param("skuId") Long skuId);
    List<CartItem> findByCartId(@Param("cartId") Integer cartId);
}