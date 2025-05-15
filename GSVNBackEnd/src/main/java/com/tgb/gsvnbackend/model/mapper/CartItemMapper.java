package com.tgb.gsvnbackend.model.mapper;

import com.tgb.gsvnbackend.model.domain.CartItemDomain;
import com.tgb.gsvnbackend.model.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CartItemMapper {

    @Mapping(source = "skuId", target = "skuId")
    @Mapping(source = "quantity", target = "quantity")
    CartItemDomain toCartItemDomain(CartItem cartItem);
}
