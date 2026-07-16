package com.gsvn.promotionservice.mapper;

import com.gsvn.promotionservice.model.entity.VoucherUsageHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface VoucherUsageHistoryMapper {
    int insert(VoucherUsageHistory history);

    List<VoucherUsageHistory> findAll(@Param("customerId") String customerId,
                                      @Param("voucherId") Integer voucherId,
                                      @Param("limit") int limit,
                                      @Param("offset") int offset);

    long countAll(@Param("customerId") String customerId,
                  @Param("voucherId") Integer voucherId);

    int countUserUsage(@Param("voucherId") Integer voucherId,
                       @Param("customerId") Long customerId,
                       @Param("guestEmail") String guestEmail);
    int deleteBySagaId(@Param("sagaId") String sagaId);
    VoucherUsageHistory findBySagaId(@Param("sagaId") String sagaId);
}