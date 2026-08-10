package com.gsvn.cartservice.mapper;

import com.gsvn.cartservice.model.entity.Wishlist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WishlistMapper {
    int insert(Wishlist wishlist);

    int delete(@Param("customerId") Long customerId, @Param("productId") Integer productId);

    List<Wishlist> findByCustomerId(@Param("customerId") Long customerId);

    boolean exists(@Param("customerId") Long customerId, @Param("productId") Integer productId);
}