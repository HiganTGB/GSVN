package com.gsvn.cartservice.mapper;

import com.gsvn.cartservice.model.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface CartMapper {
    Optional<Cart> findByCustomerId(@Param("customerId") Long customerId);

    int insert(Cart cart);

    int deleteById(@Param("id") Integer id);

    int deleteByCustomerId(@Param("customerId") Long customerId);

    Cart findById(@Param("id") Integer id);;
}