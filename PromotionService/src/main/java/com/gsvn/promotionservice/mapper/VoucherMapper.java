package com.gsvn.promotionservice.mapper;

import com.gsvn.promotionservice.model.entity.Voucher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface VoucherMapper {
    int insert(Voucher voucher);

    Voucher findById(@Param("id") Integer id);

    Voucher findByCode(@Param("code") String code);

    List<Voucher> findSearch(@Param("code") String code,
                          @Param("limit") int limit,
                          @Param("offset") int offset,
                             @Param("sortField") String sortField,
                             @Param("sortOrder") String sortOrder);

    long countSearch(@Param("code") String code);

    int update(Voucher voucher);

    int delete(@Param("id") Integer id);

    int incrementUsedCount(@Param("id") Integer id, @Param("version") Integer version);
    int decrementUsedCount(@Param("id") Integer id);
}