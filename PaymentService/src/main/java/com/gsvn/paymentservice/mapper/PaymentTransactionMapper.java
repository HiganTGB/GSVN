package com.gsvn.paymentservice.mapper;

import com.gsvn.paymentservice.model.entity.PaymentStatus;
import com.gsvn.paymentservice.model.entity.PaymentTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PaymentTransactionMapper {
    int insert(PaymentTransaction tx);

    Optional<PaymentTransaction> findById(Long id);

    Optional<PaymentTransaction> findByReferenceId(String referenceId);

    void updateStatus(@Param("referenceId") String referenceId,
                      @Param("status") String status,
                      @Param("externalId") String externalId,
                      @Param("providerResponse") String providerResponse);

    void update(PaymentTransaction tx);

    List<PaymentTransaction> findPage(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("provider") String provider,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    long countSearch(
            @Param("keyword") String keyword,
            @Param("status") String status
    );
    int updateStatusByOrderCode(
            @Param("orderCode") String orderCode,
            @Param("status") String status,
            @Param("confirmedBy") String confirmedBy
    );
}